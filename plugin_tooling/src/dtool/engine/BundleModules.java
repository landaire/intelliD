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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dtool.engine.modules.ModuleFullName;

public class BundleModules {
	
	protected final Map<ModuleFullName, File> modules;
	protected final Set<File> moduleFiles;
	protected final List<File> importFolders;
	
	/**
	 * Optimized constructor 
	 */
	public BundleModules(HashMap<ModuleFullName, File> modules, HashSet<File> moduleFiles, List<File> importFolders) {
		this(modules, moduleFiles, importFolders, true);
	}
	
	public BundleModules(HashMap<ModuleFullName, File> modules, HashSet<File> moduleFiles, List<File> importFolders,
			boolean requireAbsolute) {
		this.modules = Collections.unmodifiableMap(modules);
		this.moduleFiles = Collections.unmodifiableSet(moduleFiles);
		this.importFolders = Collections.unmodifiableList(new ArrayList<File>(importFolders));
		
		if(requireAbsolute) {
			for (File path : moduleFiles) {
				assertTrue(path.isAbsolute());
			}
			for (File path : modules.values()) {
				assertTrue(path.isAbsolute());
			}
		}
	}
	
	public File getModuleAbsolutePath(ModuleFullName moduleFullName) {
		return modules.get(moduleFullName);
	}
	
	protected void findModules(String fullNamePrefix, HashSet<String> matchedModules) {
		Set<ModuleFullName> moduleEntries = modules.keySet();
		for (ModuleFullName moduleEntry : moduleEntries) {
			String moduleFullName = moduleEntry.getFullNameAsString();
			if(moduleFullName.startsWith(fullNamePrefix)) {
				matchedModules.add(moduleFullName);
			}
		}
	}
	
	protected static BundleModules createEmpty() {
		return new BundleModules(new HashMap<ModuleFullName, File>(), new HashSet<File>(), new ArrayList<File>());
	}
	
}
