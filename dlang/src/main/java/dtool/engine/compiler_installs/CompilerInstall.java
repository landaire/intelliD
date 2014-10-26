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

import dtool.util.NewUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

public class CompilerInstall {
	
	public static enum ECompilerType {
		DMD, GDC, LDC, OTHER
	}
	
	protected final File compilerPath;
	protected final ECompilerType compilerType;
	protected final List<File> librarySourceFolders;
	
	
	public CompilerInstall(File compilerPath, ECompilerType compilerType, File... librarySourceFolders) {
		this(compilerPath, compilerType, NewUtils.normalizePaths(librarySourceFolders));
	}
	
	public CompilerInstall(File compilerPath, ECompilerType compilerType, List<File> librarySourceFolders) {
		this.compilerPath = new File(compilerPath.toURI().normalize());
		this.compilerType = compilerType;
		this.librarySourceFolders = Collections.unmodifiableList(librarySourceFolders);
		for (File path : librarySourceFolders) {
			assertTrue(path.isAbsolute() && path.equals(new File(path.toURI().normalize())));
		}
	}
	
	public File getCompilerPath() {
		return compilerPath;
	}
	
	public ECompilerType getCompilerType() {
		return compilerType;
	}
	
	public List<File> getLibrarySourceFolders() {
		return librarySourceFolders;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof CompilerInstall)) return false;
		
		CompilerInstall other = (CompilerInstall) obj;
		
		return areEqual(compilerPath, other.compilerPath) &&
				areEqual(librarySourceFolders, other.librarySourceFolders);
	}
	
	@Override
	public int hashCode() {
		return compilerPath.hashCode();
	}
	
}
