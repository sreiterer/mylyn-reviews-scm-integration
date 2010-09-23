/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Reiterer (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.scm.serialization.model;

import java.net.URI;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.reviews.core.scm.IRevision;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.ITag;

/** This class represents the revision of a file in the scm system.
 * TODO currently not fully implemented.
 * 
 * @author Stefan Reiterer
 *
 */
public class Revision implements IRevision {
	String author;
	String contentIdentifier;
	String comment;
	String name;
	
	
	public Revision(String contentIdentifier) {
		super();
		this.contentIdentifier = contentIdentifier;
	}

	public Revision(String author, String contentIdentifier, String comment,
			String name) {
		super();
		this.author = author;
		this.contentIdentifier = contentIdentifier;
		this.comment = comment;
		this.name = name;
	}

	public boolean exists() {
		return true;
	}

	public String getAuthor() {
		return author;
	}

	public String getComment() {
		return comment;
	}

	public String getContentIdentifier() {
		return contentIdentifier;
	}

	public String getName() {
		return name;
	}

	public IStorage getStorage(IProgressMonitor arg0) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public ITag[] getTags() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getTimestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	public URI getURI() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isPropertyMissing() {
		// TODO Auto-generated method stub
		return false;
	}

	public IFileRevision withAllProperties(IProgressMonitor arg0)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

}
