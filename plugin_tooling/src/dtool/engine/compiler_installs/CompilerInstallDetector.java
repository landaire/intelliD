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
package dtool.engine.compiler_installs;

import java.io.File;

import melnorme.utilbox.misc.StringUtil;
import dtool.engine.compiler_installs.CompilerInstall.ECompilerType;
import org.apache.commons.io.FileUtils;

public class CompilerInstallDetector {
	
	public static final String SPECIAL_EMPTY_INSTALL = "__special_empty_install";

	public CompilerInstallDetector() {
	}
	
	public CompilerInstall detectInstallFromCompilerCommandPath(File commandPath) {
		String fileName = commandPath.getName();
		
		if(fileName.equals(SPECIAL_EMPTY_INSTALL)) {
			// Special compiler install with no modules. Useful for testing purposes.
			return new CompilerInstall(commandPath, ECompilerType.OTHER);
		}
		
		if(executableMatches(fileName, "dmd")) {
			return detectDMDInstall(commandPath);
		} else if(executableMatches(fileName, "gdc")) {
			return detectGDCInstall(commandPath);
		} else if(executableMatches(fileName, "ldc2") || executableMatches(fileName, "ldc")) {
			return detectLDCInstall(commandPath);
		}
		
		return null;
	}
	
	protected CompilerInstall detectDMDInstall(File commandPath) {
		File cmdDir = commandPath.getParentFile();
		
		if(FileUtils.getFile(cmdDir, "../../src/druntime").exists()) {
			return new CompilerInstall(commandPath, ECompilerType.DMD,
				FileUtils.getFile(cmdDir, "../../src/druntime/import"),
				FileUtils.getFile(cmdDir, "../../src/phobos"));
		}
		// a MacOSX layout:
		if(FileUtils.getFile(cmdDir, "../src/druntime").exists()) {
			return new CompilerInstall(commandPath, ECompilerType.DMD,
				FileUtils.getFile(cmdDir, ("../src/druntime/import")),
				FileUtils.getFile(cmdDir, ("../src/phobos")));
		}
		// another MacOSX layout
		File resolvedCmdPath = FileUtils.getFile(cmdDir, "../share/dmd/bin/dmd");
		if(resolvedCmdPath.exists()) {
			File resolvedCmdDir = resolvedCmdPath.getParentFile();
			if(FileUtils.getFile(resolvedCmdDir, "../src/druntime").exists()) {
				return new CompilerInstall(resolvedCmdPath, ECompilerType.DMD,
					FileUtils.getFile(resolvedCmdDir, "../src/druntime/import"),
					FileUtils.getFile(resolvedCmdDir, "../src/phobos"));
			}
		}
		
		if(FileUtils.getFile(cmdDir, "../include/dlang/dmd").exists()) {
			return new CompilerInstall(commandPath, ECompilerType.DMD, 
				FileUtils.getFile(cmdDir, "../include/dlang/dmd"));
		}
		
		if(FileUtils.getFile(cmdDir, "../include/dmd").exists()) {
			return new CompilerInstall(commandPath, ECompilerType.DMD, 
				FileUtils.getFile(cmdDir, "../include/dmd/druntime/import"),
				FileUtils.getFile(cmdDir, "../include/dmd/phobos"));
		}
		
		if(FileUtils.getFile(cmdDir, "../../include/d/dmd").exists()) {
			return new CompilerInstall(commandPath, ECompilerType.DMD, 
				FileUtils.getFile(cmdDir, "../../include/d/dmd/druntime/import"),
				FileUtils.getFile(cmdDir, "../../include/d/dmd/phobos"));
		}
		return null;
	}
	
	protected CompilerInstall detectLDCInstall(File commandPath) {
		File cmdDir = commandPath.getParentFile();
		
		if(FileUtils.getFile(cmdDir, "../include/dlang/ldc").exists()) {
			return new CompilerInstall(commandPath, ECompilerType.LDC, 
				FileUtils.getFile(cmdDir, "../include/dlang/ldc"));
		}
		
		if(FileUtils.getFile(cmdDir, "../import/core").exists()) {
			return new CompilerInstall(commandPath, ECompilerType.LDC,
				FileUtils.getFile(cmdDir, "../import/ldc"),
				FileUtils.getFile(cmdDir, "../import"));
		}
		return null;
	}
	
	protected CompilerInstall detectGDCInstall(File commandPath) {
		File cmdDir = commandPath.getParentFile();
		
		if(FileUtils.getFile(cmdDir, "../include/dlang/gdc").exists()) {
			return new CompilerInstall(commandPath, ECompilerType.GDC, 
				FileUtils.getFile(cmdDir, "../include/dlang/gdc"));
		}
		
		CompilerInstall install = checkGDCLibrariesAt(FileUtils.getFile(cmdDir, "../include/d"), commandPath);
		if(install != null) 
			return install;
		
		return checkGDCLibrariesAt(FileUtils.getFile(cmdDir, "../include/d2"), commandPath);
	}
	
	protected CompilerInstall checkGDCLibrariesAt(File includeD2Dir, File commandPath) {
		if(includeD2Dir.exists()) {
			
			File[] d2entries = includeD2Dir.listFiles();
			if(d2entries == null) // Same as IOException
				return null;
			
			for (File d2entry : d2entries) {
				if(d2entry.isDirectory() && new File(d2entry, "object.di").exists()) {
					return new CompilerInstall(commandPath, ECompilerType.GDC, d2entry);
				}
			}
			
		}
		return null;
	}
	
	protected boolean executableMatches(String fileName, String executableName) {
		fileName = StringUtil.trimEnd(fileName, ".exe");
		return fileName.equals(executableName);
	}
	
}
