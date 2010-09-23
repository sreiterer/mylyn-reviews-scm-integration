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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Changeset")
@XmlAccessorType(XmlAccessType.FIELD)
public class Task {
	
	@XmlAttribute(name = "id")
	private long taskId;
	private String repositoryUrl;
	
	
	
	public Task() {
		super();
	}

	public Task(long taskId, String repositoryUrl) {
		super();
		this.taskId = taskId;
		this.repositoryUrl = repositoryUrl;
	}
	
	public long getTaskId() {
		return taskId;
	}
	
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
	
	public String getRepositoryUrl() {
		return repositoryUrl;
	}
	
	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((repositoryUrl == null) ? 0 : repositoryUrl.hashCode());
		result = prime * result + (int) (taskId ^ (taskId >>> 32));
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		if (repositoryUrl == null) {
			if (other.repositoryUrl != null)
				return false;
		} else if (!repositoryUrl.equals(other.repositoryUrl))
			return false;
		if (taskId != other.taskId)
			return false;
		return true;
	}
}
