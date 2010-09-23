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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Tupel")
public class ChangesetTupel {
	@XmlAttribute(name = "key", required = true)
    private String key;
    @XmlElement(name = "value", required = true)
    private Changeset value;

    public ChangesetTupel(String key, Changeset value) {
        this.key = key;
        this.value = value;
    }
    
    public ChangesetTupel() {
    	this.key = "";
    	this.value = null;
    }

	public String getKey() {
		return key;
	}

	public Changeset getValue() {
		return value;
	}
    
    
}
