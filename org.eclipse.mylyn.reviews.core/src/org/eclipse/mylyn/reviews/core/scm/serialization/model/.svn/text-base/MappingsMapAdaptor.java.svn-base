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

public class MappingsMapAdaptor extends XmlAdapter<MappingsMap, Map<String, List<String>>> {

	@Override
	public MappingsMap marshal(Map<String, List<String>> v) throws Exception {
		MappingsMap myMap = new MappingsMap();
        List<Tupel> aList = myMap.getTupel();
        for (Map.Entry<String, List<String>> e : v.entrySet()) {
            aList.add(new Tupel(e.getKey(), e.getValue()));
        }
        return myMap;
	}

	@Override
	public Map<String, List<String>> unmarshal(MappingsMap v) throws Exception {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (Tupel e : v.getTupel()) {
            map.put(e.getKey(), e.getValue());
        }
        return map;
	}
}
