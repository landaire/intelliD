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


import dtool.ast.definitions.Module;
import dtool.engine.ModuleParseCache.ParseSourceException;
import dtool.engine.modules.BundleModulesVisitor;
import dtool.engine.modules.IModuleResolver;
import dtool.engine.modules.ModuleFullName;
import dtool.parser.DeeParserResult.ParsedModule;
import melnorme.utilbox.misc.ArrayUtil;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

public abstract class AbstractBundleResolution implements IBundleResolution {
	
	protected final SemanticManager manager;
	protected final BundleModules bundleModules;
	
	public AbstractBundleResolution(SemanticManager manager, List<File> importFolders) {
		this(manager, manager.new SM_BundleModulesVisitor(importFolders).toBundleModules());
	}
	
	public AbstractBundleResolution(SemanticManager manager, BundleModules bundleModules) {
		this.manager = manager;
		this.bundleModules = bundleModules;
	}
	
	
	public Set<File> getBundleModuleFiles() {
		return bundleModules.moduleFiles;
	}
	
	/** @return the absolute path of a module contained in this bundle resolution. */
	protected File getBundleModulePath(ModuleFullName moduleFullName) {
		return bundleModules.getModuleAbsolutePath(moduleFullName);
	}
	
	@Override
	public HashSet<String> findModules(String fullNamePrefix) {
		HashSet<String> matchedModules = new HashSet<String>();
		findModules(fullNamePrefix, matchedModules);
		return matchedModules;
	}
	
	protected void findModules(String fullNamePrefix, HashSet<String> matchedModules) {
		bundleModules.findModules(fullNamePrefix, matchedModules);
	}
	
	/** @return a resolved module from this bundle's full import path (including dependencies). */
	public ResolvedModule findResolvedModule(ModuleFullName moduleFullName) throws ParseSourceException {
		return getBundleResolvedModule(moduleFullName);
	}
	
	public boolean checkIsStale() {
		return checkIsModuleListStale() || checkIsModuleContentsStale();
	}
	
	public boolean checkIsModuleListStale() {
		List<File> importFolders = bundleModules.importFolders;
		BundleModulesVisitor modulesVisitor = manager.new SM_BundleModulesVisitor(importFolders) {
			@Override
			protected void addModuleEntry(ModuleFullName moduleFullName, File fullPath) {
				moduleFiles.add(fullPath);
			}
		};
		return !modulesVisitor.getModuleFiles().equals(bundleModules.moduleFiles);
	}
	
	/* -----------------  ----------------- */
	
	// TODO: proper synchronization - for now assume no concurrent acesss to resolve operations 
	protected final Map<File, ResolvedModule> resolvedModules = new HashMap<File, ResolvedModule>();
	
	public synchronized boolean checkIsModuleContentsStale() {
		ModuleParseCache parseCache = manager.parseCache;
		
		for (Entry<File, ResolvedModule> entry : resolvedModules.entrySet()) {
			File path = entry.getKey();
			ResolvedModule currentModule = entry.getValue();
			
			ParsedModule parsedModule = parseCache.getEntry(path).getParsedModuleIfNotStale(true);
			if(parsedModule == null) {
				return true;
			}
			
			if(parsedModule != currentModule.parsedModule) {
				return true;
			}
		}
		return false;
	}
	
	protected ResolvedModule getBundleResolvedModule(String moduleFullName) throws ParseSourceException {
		return getBundleResolvedModule(new ModuleFullName(moduleFullName));
	}
	
	protected ResolvedModule getBundleResolvedModule(ModuleFullName moduleFullName) throws ParseSourceException {
		File modulePath = getBundleModulePath(moduleFullName);
		return modulePath == null ? null : getBundleResolvedModule(modulePath);
	}
	
	public synchronized ResolvedModule getBundleResolvedModule(File filePath) throws ParseSourceException {
		ModuleParseCache parseCache = manager.parseCache;
		
		ResolvedModule resolvedModule = resolvedModules.get(filePath);
		if(resolvedModule == null) {
			ParsedModule parsedModule = parseCache.getParsedModule(filePath);
			resolvedModule = new ResolvedModule(parsedModule, this);
			resolvedModules.put(filePath, resolvedModule);
		}
		return resolvedModule;
	}
	
	@Override
	public Module findModule(String[] packages, String module) throws ParseSourceException {
		ModuleFullName moduleFullName = new ModuleFullName(ArrayUtil.concat(packages, module));
		ResolvedModule resolvedModule = findResolvedModule(moduleFullName);
		return resolvedModule == null ? null : resolvedModule.getModuleNode();
	}
	
	public static class ResolvedModule {
		
		protected final ParsedModule parsedModule;
		protected final AbstractBundleResolution bundleRes;
		
		public ResolvedModule(ParsedModule parsedModule, AbstractBundleResolution bundleRes) {
			this.parsedModule = parsedModule;
			this.bundleRes = bundleRes;
		}
		
		public ParsedModule getParsedModule() {
			return parsedModule;
		}
		
		public Module getModuleNode() {
			return parsedModule.module;
		}
		
		public File getModulePath() {
			return parsedModule.modulePath;
		}
		
		public AbstractBundleResolution getSemanticResolution() {
			return bundleRes;
		}
		
		public IModuleResolver getModuleResolver() {
			return bundleRes;
		}
		
	}
	
}
