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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.mylyn.reviews.core.scm.IChangeset;

@XmlRootElement(name="Changeset")
@XmlAccessorType(XmlAccessType.FIELD)
public class Changeset implements IChangeset{
	@XmlAttribute(name = "revision")
	private String revision;
	
	private String message;
	
	private Date date = new Date();
	
	private String autor;
	
	private String projectUrl;
	
	@XmlElementWrapper(name="Files")
	private List<File> files = new ArrayList<File>();

	
	public Changeset() {
		super();
	}

	public Changeset(String revision, List<File> files) {
		super();
		this.revision = revision;
		this.files = files;
	}

	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRevision() {
		return revision;
	}
	
	public void setRevision(String revision) {
		this.revision = revision;
	}
	
	public List<File> getFiles() {
		return files;
	}
	
	public void setFiles(List<File> files) {
		this.files = files;
	}
	
	public String getProjectUrl() {
		return projectUrl;
	}

	public void setProjectUrl(String projectUrl) {
		this.projectUrl = projectUrl;
	}
	
	

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((files == null) ? 0 : files.hashCode());
		result = prime * result
				+ ((revision == null) ? 0 : revision.hashCode());
		return result;
	}
	
	public boolean equals(Object obj) {
		Changeset other = (Changeset) obj;
		return this.getRevision().equals(other);
	}

	public String toString() {
		return this.getRevision() + ":" + this.getMessage() + 
			":" + this.getAutor();
	}
	
}
