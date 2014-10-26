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
package dtool.engine.util;

import java.io.File;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

/**
 * An entry caching some value, derived from a file as input. 
 * Keeps tracks of file and value timestamps, to see if current value is stale or not with regards to the file input.
 */
public class FileCachingEntry<VALUE> {
	
	protected final File filePath;
	
	protected volatile VALUE value;
	protected volatile Long valueTimeStamp;
	
	public FileCachingEntry(File path) {
		this.filePath = path;
	}
	
	public VALUE getValue() {
		return value;
	}
	
	public Long getValueTimeStamp() {
		return valueTimeStamp;
	}
	
	public void markStale() {
		valueTimeStamp = null;
	}
	
	public synchronized boolean isStale() {
		Long lastModifiedTime = filePath.lastModified();

		if(valueTimeStamp == null || valueTimeStamp < lastModifiedTime) {
			return true;
		}
		return false;
	}
	
	public void updateValue(VALUE value) {
		updateValue(value, null);
	}
	
	public void updateValue(VALUE value, Long newTimeStampMaximum) {
		assertNotNull(newTimeStampMaximum);
		
		Long newValueTimeStamp;

		newValueTimeStamp = filePath.lastModified();

		if(newTimeStampMaximum != null && newTimeStampMaximum.compareTo(newValueTimeStamp) < 0) {
			newValueTimeStamp = newTimeStampMaximum;
		}
		internalSetValue(value, newValueTimeStamp);
	}
	
	protected synchronized void internalSetValue(VALUE value, Long newTimeStamp) {
		this.value = value;
		this.valueTimeStamp = newTimeStamp;
	}
	
}
