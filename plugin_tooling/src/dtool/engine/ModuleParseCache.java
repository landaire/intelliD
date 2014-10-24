/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.Module;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult.ParsedModule;
import org.apache.commons.io.monitor.FileEntry;

/**
 * Manages a cache of parsed modules, indexed by file path
 */
public class ModuleParseCache {
	
	protected final DToolServer dtoolServer;
	
	public ModuleParseCache(DToolServer dtoolServer) {
		this.dtoolServer = dtoolServer;
	}
	
	protected final HashMap<String, ModuleEntry> cache = new HashMap<String, ModuleEntry>();
	
	/* -----------------  ----------------- */
	
	public ParsedModule getParsedModule(File filePath) throws ParseSourceException {
		ModuleEntry entry = getEntry(filePath);
		try {
			return assertNotNull(entry.getParsedModule());
		} catch (IOException e) {
			throw new ParseSourceException(e);
		}
	}
	
	public ParsedModule getExistingParsedModule(File filePath) {
		// TODO: don't create entry
		return getEntry(filePath).parsedModule;
	}
	
	// util method
	public Module getExistingParsedModuleNode(File filePath) {
		ParsedModule parsedModule = getExistingParsedModule(filePath);
		return parsedModule == null ? null : parsedModule.module;
	}
	
	public ParsedModule setWorkingCopyAndGetParsedModule(File filePath, String source) {
		return parseModuleWithNewSource(filePath, source);
	}
	
	public static class ParseSourceException extends Exception {
		
		private static final long serialVersionUID = 1L;
		
		public ParseSourceException() {
		}
		
		public ParseSourceException(Throwable cause) {
			super(cause);
		}
	}
	
	/* -----------------  ----------------- */
	
	protected File validatePath(File filePath) {
		assertNotNull(filePath);
		//filePath can be relative
		//assertTrue(filePath.isAbsolute());
		assertTrue(FileUtil.getNameCount(filePath) > 0);
		filePath = new File(filePath.toURI().normalize());
		return filePath;
	}
	
	protected String getKeyFromPath(File filePath) {
		return filePath.toString();
	}
	
	public ModuleEntry getEntry(File filePath) {
		filePath = validatePath(filePath);
		String key = getKeyFromPath(filePath);
		
		synchronized(this) {
			ModuleEntry entry = cache.get(key);
			if(entry == null) {
				entry = new ModuleEntry(filePath);
				cache.put(key, entry);
			}
			return entry;
		}
	}
	
	protected ParsedModule parseModuleWithNewSource(File filePath, String source) {
		assertNotNull(source);
		
		return getEntry(filePath).getParsedModuleWithWorkingCopySource(source);
	}
	
	public void discardWorkingCopy(File filePath) {
		filePath = validatePath(filePath);
		String key = getKeyFromPath(filePath);
		ModuleEntry entry = cache.get(key);
		if(entry != null) {
			entry.discardWorkingCopy();
		}
	}
	
	public class ModuleEntry {
		
		public static final int FILE_MODIFY_TIME__GRANULARITY_Millis = 1000000;
		
		protected final File filePath;
		
		protected String source = null;
		protected volatile boolean isWorkingCopy = false;
		protected FileEntry sourceFileSyncAttributes;
		protected volatile ParsedModule parsedModule = null;
		
		public ModuleEntry(File filePath) {
			this.filePath = filePath;
			assertTrue(filePath != null);
		}
		
		public boolean isWorkingCopy() {
			return isWorkingCopy;
		}
		
		protected synchronized boolean isStale() {
			if(isWorkingCopy) {
				assertNotNull(source);
				return false;
			}
			if(source == null || parsedModule == null) {
				return true;
			}
			
			FileEntry newAttributes;
			newAttributes = new FileEntry(filePath);

			if (!filePath.exists()) {
				return true;
			}

			return hasBeenModified(sourceFileSyncAttributes, newAttributes);
		}
		
		public synchronized ParsedModule getParsedModule() throws FileNotFoundException, IOException {
			if(isStale()) {
				readSource();
			}			
			return doGetParsedModule(source);
		}
		
		protected void readSource() throws IOException, FileNotFoundException {
			sourceFileSyncAttributes = new FileEntry(filePath);
			String fileContents = FileUtil.readStringFromFile(filePath, StringUtil.UTF8); // TODO: detect encoding
			setNewSource(fileContents);
		}
		
		protected void setNewSource(String newSource) {
			if(!areEqual(source, newSource)) {
				source = newSource;
				parsedModule = null;
			}
		}
		
		public synchronized ParsedModule getParsedModuleIfNotStale(boolean attemptSourceRefresh) {
			if(!isStale()) {
				return parsedModule;		
			}
			
			if(attemptSourceRefresh) {
				try {
					readSource();
					return parsedModule; // parsedModule will remain the same if the source didn't change.
				} catch (IOException e) {
					return null;
				}
			} else {
				return null;
			}
		}
		
		public synchronized ParsedModule getParsedModuleWithWorkingCopySource(String newSource) {
			assertNotNull(newSource);
			setNewSource(newSource);
			isWorkingCopy = true;
			dtoolServer.logMessage("ParseCache: Set working copy: " + filePath);
			return doGetParsedModule(newSource);
		}
		
		protected synchronized void discardWorkingCopy() {
			if(isWorkingCopy) {
				isWorkingCopy = false;
				sourceFileSyncAttributes = null; // This will invalidate current source
				dtoolServer.logMessage("ParseCache: Discarded working copy: " + filePath);
			}
		}
		
		protected ParsedModule doGetParsedModule(String source) {
			if(parsedModule == null) {
				parsedModule = DeeParser.parseSource(source, filePath);
				dtoolServer.logMessage("ParseCache: Parsed module " + filePath +
					(isWorkingCopy ? " [WorkingCopy]" : ""));
			}
			return parsedModule;
		}
		
	}
	
	public static boolean hasBeenModified(FileEntry originalAttributes, FileEntry newAttributes) {
		return 
				originalAttributes == null ||
				originalAttributes.getLastModified() != newAttributes.getLastModified() ||
				originalAttributes.getLength() != newAttributes.getLength();
	}

}
