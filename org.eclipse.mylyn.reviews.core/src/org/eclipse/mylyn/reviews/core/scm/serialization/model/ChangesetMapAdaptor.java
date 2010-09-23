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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ChangesetMapAdaptor extends XmlAdapter<ChangesetMap, Map<String,Changeset>> {

	public ChangesetMap marshal(Map<String, Changeset> v) throws Exception {
		ChangesetMap myMap = new ChangesetMap();
        List<ChangesetTupel> changesetList = myMap.getChangesetTupel();
        for (Map.Entry<String,Changeset> e : v.entrySet()) {
        	changesetList.add(new ChangesetTupel(e.getKey(), e.getValue()));
        }
        return myMap;
	}

	public Map<String, Changeset> unmarshal(ChangesetMap v) throws Exception {
		Map<String,Changeset> map = new HashMap<String,Changeset>();
        for (ChangesetTupel e : v.getChangesetTupel()) {
            map.put(e.getKey(), e.getValue());
        }
        return map;
	}


}
