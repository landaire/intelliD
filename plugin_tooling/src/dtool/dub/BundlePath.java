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
package dtool.dub;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.misc.MiscUtil.InvalidPathExceptionX;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * A valid directory path for a dub bundle.
 * Is normalized, absolute, and has at least one segment 
 */
public class BundlePath {
	
	public static final String DUB_MANIFEST_FILENAME = "dub.json";
	
	public static BundlePath create(String pathStr) {
		try {
			File path = MiscUtil.createPath(pathStr);
			return BundlePath.create(path);
		} catch (InvalidPathExceptionX e) {
			return null;
		}
	}
	
	public static BundlePath create(File path) {
		if(isValidBundlePath(path)) {
			return new BundlePath(path);
		}
		return null;
	}
	
	public static boolean isValidBundlePath(File path) {
		assertNotNull(path);
		return path.isAbsolute() && FileUtil.getNameCount(path) > 0;
	}
	
	/* -----------------  ----------------- */
	
	public final File path;
	
	public BundlePath(File path) {
		assertTrue(BundlePath.isValidBundlePath(path));
		this.path = new File(path.toURI().normalize());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof BundlePath)) return false;
		
		BundlePath other = (BundlePath) obj;
		
		return areEqual(path, other.path);
	}
	
	@Override
	public int hashCode() {
		return path.hashCode();
	}
	
	public File getManifestFilePath() {
		return FileUtils.getFile(path, DUB_MANIFEST_FILENAME);
	}
	
//	public File resolve(File other) {
//		return path.resolve(other);
//	}
	
	public File resolve(String other) {
		return FileUtils.getFile(path, other);
	}
	
	@Override
	public String toString() {
		return path.toString();
	}
	
	/***
	 * Searches for a manifest file in any of the directories denoted by given path, starting in path. 
	 */
	public static BundlePath findBundleForPath(File path) {
		if(path == null || !path.isAbsolute()) {
			return null;
		}
		BundlePath bundleFile = create(path);
		if(bundleFile != null && bundleFile.getManifestFilePath().exists()) {
			return bundleFile;
		}
		return findBundleForPath(path.getParentFile());
	}
	
}
