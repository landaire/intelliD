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
package dtool.engine.modules;

import dtool.engine.BundleModules;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

public abstract class BundleModulesVisitor {
	
	protected final HashMap<ModuleFullName, File> modules = new HashMap<ModuleFullName, File>();
	protected final HashSet<File> moduleFiles = new HashSet<File>();
	protected final List<File> importFolders;
	
	public BundleModulesVisitor(List<File> importFolders) {
		this.importFolders = importFolders;
		visitBundleModules(importFolders);
	}
	
	public void visitBundleModules(List<File> importFolders) {
		for (File importFolder : importFolders) {
			visitImportFolder(importFolder, importFolder);
		}
	}
	
	protected void visitImportFolder(final File importFolder, final File rootImportFolder) {
		if(!importFolder.exists()) {
			return;
		}


		Collection<File> files = FileUtils.listFilesAndDirs(importFolder, TrueFileFilter.INSTANCE, null);

		for (File currentFile : files) {
			// If the current file is a directory and is a potential package name, walk that too
			if (currentFile.isDirectory()) {
				if (ModuleNamingRules.isValidPackageNameSegment(currentFile.getName())) {
					visitPotentialModuleFile(currentFile, rootImportFolder);
				}

				continue;
			}

			visitPotentialModuleFile(currentFile, rootImportFolder);
		}
	}
	
	protected void visitPotentialModuleFile(File potentialFile, File importFolder) {
		String relPath = importFolder.toURI().relativize(potentialFile.toURI()).getPath();
		ModuleFullName moduleFullName = ModuleNamingRules.getValidModuleNameOrNull(relPath);
		if(moduleFullName != null) {
			assertTrue(potentialFile.isAbsolute());
			addModuleEntry(moduleFullName, potentialFile);
		}
	}

	protected void addModuleEntry(ModuleFullName moduleFullName, File fullPath) {
		modules.put(moduleFullName, fullPath);
		moduleFiles.add(fullPath);
	}
	
	public HashSet<File> getModuleFiles() {
		return moduleFiles;
	}
	
	public BundleModules toBundleModules() {
		return new BundleModules(modules, moduleFiles, importFolders);
	}
	
}
