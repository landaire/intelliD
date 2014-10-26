/*******************************************************************************
 * Copyright (c) 2007, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial implementation
 *******************************************************************************/
package melnorme.utilbox.misc;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * Miscellaneous file utilities.
 */
public final class FileUtil {

	public static int getNameCount(File file) {
		String[] tokens = file.getPath().split(File.separator);
		return tokens.length;
	}

	/** Read all bytes of the given file. 
	 * @return the bytes that where read in a {@link java.io.ByteArrayOutputStream}. */
	public static IByteSequence readBytesFromFile(File file) throws IOException, FileNotFoundException {
		long fileLength = file.length();
		/*
		 * You cannot create an array using a long type. It needs to be an
		 * int type. Before converting to an int type, check to ensure
		 * that file is not larger than Integer.MAX_VALUE.
		 */
		if (fileLength > Integer.MAX_VALUE) 
			throw new IOException("File is too large, size is bigger than " + Integer.MAX_VALUE);
		
		return StreamUtil.readAllBytesFromStream(new FileInputStream(file), (int) fileLength);
	}
	
	/** Read all bytes from the given file.
	 * @return a String created from those bytes, with given charsetName. */
	public static String readStringFromFile(File file, String charsetName) throws IOException, FileNotFoundException {
		return readBytesFromFile(file).toString(Charset.forName(charsetName));
	}
	
	/** Read all bytes from the given file.
	 * @return a String created from those bytes, with given charset. */
	public static String readStringFromFile(File file, Charset charset) throws IOException, FileNotFoundException {
		return readBytesFromFile(file).toString(charset);
	}
	
	
	/** Write the given array of bytes to given file */
	public static void writeBytesToFile(File file, byte[] bytes) throws IOException, FileNotFoundException {
		FileOutputStream fileOS = new FileOutputStream(file);
		StreamUtil.writeBytesToStream(bytes, fileOS);
	}
	
	/** Writes given chars array to given writer. 
	 * Close writer afterwards. */
	public static void writeCharsToFile(File file, char[] chars, Charset charset) 
			throws IOException, FileNotFoundException {
		FileOutputStream fileOS = new FileOutputStream(file);
		OutputStreamWriter osWriter = new OutputStreamWriter(fileOS, charset);
		StreamUtil.writeCharsToWriter(chars, osWriter);
	}
	
	/** Writes given string to given writer. 
	 * Close writer afterwards. */
	public static void writeStringToFile(File file, String string, Charset charset) 
			throws IOException, FileNotFoundException {
		FileOutputStream fileOS = new FileOutputStream(file);
		OutputStreamWriter osWriter = new OutputStreamWriter(fileOS, charset);
		StreamUtil.writeStringToWriter(string, osWriter);
	}
	
	/* -----------------  ----------------- */
	
	public static boolean deleteIfExists(File path) throws IOException {
		if (path.exists()) {
			return path.delete();
		}

		return false;
	}
	
	public static void deleteDirContents(File dir) throws IOException {
		deleteDirContents(dir, false);
	}
	
	public static void deleteDir(File dir) throws IOException {
		FileUtils.deleteDirectory(dir);
	}
	
	protected static void deleteDirContents(final File directory, final boolean deleteDirectory) throws IOException {
		if(!directory.exists()) {
			return;
		}

		for (File currentFile : FileUtils.listFilesAndDirs(directory, TrueFileFilter.INSTANCE, null)) {
			if (!currentFile.exists()) {
				continue;
			}

			if (currentFile.isDirectory()) {
				FileUtils.deleteDirectory(currentFile);
			} else {
				currentFile.delete();
			}
		}
	}
	
}
