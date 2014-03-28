/*  
 *   Copyright 2012-2014 Martijn Devisscher
 *
 *   This file is part of boinq.
 *
 *   boinq is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   boinq is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with boinq.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.genohm.viewsGWT.shared.data.feature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.genohm.viewsGWT.shared.Location;

public class GFF3Feature extends Feature {
	
	public GFF3Feature() {
		super();
 	}

	public GFF3Feature(String id, String description, Double score, String name, Feature parent,
			List<Location> locList, List<? extends Feature> subFeatures, Map<String,String> attributeMap) {
		super(id, description, score, name, parent, locList, subFeatures);
		this.attributeMap = attributeMap;
 	}

	Map<String, String> attributeMap = new HashMap<String, String>();

	public Map<String, String> getAttributeMap() {
		return attributeMap;
	}

	public void setAttributeMap(Map<String, String> attributeMap) {
		this.attributeMap = attributeMap;
	}
	
	
}

