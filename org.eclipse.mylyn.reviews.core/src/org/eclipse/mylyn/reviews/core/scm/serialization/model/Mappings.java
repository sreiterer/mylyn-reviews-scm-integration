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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="TaskChangesetMappings")
@XmlAccessorType(XmlAccessType.FIELD)
public class Mappings {
	
	// Task-Changeset mappings
	@XmlElement(name = "MappingsMap", required = true)
	@XmlJavaTypeAdapter(MappingsMapAdaptor.class)
	private Map<String, List<String>> mappings = new HashMap<String, List<String>>();

	@XmlElement(name = "ChangesetMap", required = true)
	@XmlJavaTypeAdapter(ChangesetMapAdaptor.class)
	private Map<String, Changeset> changesets = new HashMap<String, Changeset>();
	
	// last parsed revision
	private String lastRevision;
	
	// Pattern for parsing commit-messages
	private String commitPattern;
	
	public Mappings() {
		super();
	}
	
	public String getCommitPattern() {
		return commitPattern;
	}

	public void setCommitPattern(String commitPattern) {
		this.commitPattern = commitPattern;
	}

	public String getLastRevision() {
		return lastRevision;
	}

	public void setLastRevision(String lastRevision) {
		this.lastRevision = lastRevision;
	}
	
	public Map<String, List<String>> getTaskMappings() {
		return mappings;
	}

	public void addTaskMapping(String taskId, List<String> revision) {
		mappings.put(taskId, revision);
	}
	
	public void addChangesetToTask(String taskId, Changeset changeset) {
		List<String> changes = mappings.remove(taskId);
		
		changes.add(changeset.getRevision());
		addTaskMapping(taskId, changes);
		
		addChangeset(changeset);
	}

	public void setTaskMappings(Map<String, List<String>> mappings) {
		this.mappings = mappings;
	}
	
	public Map<String, Changeset> getChangesets() {
		return changesets;
	}

	public void setChangesets(Map<String, Changeset> changesets) {
		this.changesets = changesets;
	}
	
	public void addChangeset(Changeset changeset) {
		changesets.put(changeset.getRevision(), changeset);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mappings == null) ? 0 : mappings.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mappings other = (Mappings) obj;
		if (mappings == null) {
			if (other.mappings != null)
				return false;
		} else if (!mappings.equals(other.mappings))
			return false;
		return true;
	}
	
	
}
