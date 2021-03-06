/*******************************************************************************
 * Copyright (c) 2014, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine.compiler_installs;

import dtool.engine.StandardLibraryResolution.MissingStandardLibraryResolution;
import dtool.util.SearchPathEnvOperation;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class SearchCompilersOnPathOperation extends SearchPathEnvOperation {
	
	public static final String DUB_COMPILERS_PATH__ENV_VAR = "DUB_COMPILERS_PATH";
	
	protected final CompilerInstallDetector detector = new CompilerInstallDetector();
	protected final List<CompilerInstall> foundInstalls = new ArrayList<CompilerInstall>();
	
	public SearchCompilersOnPathOperation searchForCompilersInDefaultPathEnvVars() {
		searchEnvironmentVar(DUB_COMPILERS_PATH__ENV_VAR);
		searchEnvironmentVar("PATH");
		return this;
	}
	
	@Override
	protected void searchPathEntry(File pathEntry) {
		File exePath;
		if((exePath = executableExists(pathEntry, "dmd")) != null) {
			addPossibleInstall(detector.detectDMDInstall(exePath));
		}
		if((exePath = executableExists(pathEntry, "gdc")) != null) {
			addPossibleInstall(detector.detectGDCInstall(exePath));
		}
		if((exePath = executableExists(pathEntry, "ldc")) != null ||
			(exePath = executableExists(pathEntry, "ldc2")) != null) {
			addPossibleInstall(detector.detectLDCInstall(exePath));
		}
	}
	
	protected void addPossibleInstall(CompilerInstall install) {
		if(install != null) {
			foundInstalls.add(install);
		}
	}
	
	protected File executableExists(File pathEntry, String executableFileName) {
		File exePath;
		if((exePath = FileUtils.getFile(pathEntry, executableFileName)).exists()) {
			return exePath;
		}
		if((exePath = FileUtils.getFile(pathEntry, executableFileName + ".exe")).exists()) {
			return exePath;
		}
		return null;
	}
	
	public List<CompilerInstall> getFoundInstalls() {
		return foundInstalls;
	}
	
	/** @return a preferred compiler install. Not null. */
	public CompilerInstall getPreferredInstall() {
		if(foundInstalls != null && foundInstalls.size() > 0) {
			return foundInstalls.get(0);
		}
		return MissingStandardLibraryResolution.NULL_COMPILER_INSTALL;
	}
	
}
