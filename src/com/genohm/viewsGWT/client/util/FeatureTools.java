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
package com.genohm.viewsGWT.client.util;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import com.genohm.viewsGWT.shared.data.feature.Feature;

public class FeatureTools {
	public static List<Feature> sortByStartPos(List<Feature> origList) {
		List<Feature> newList = new LinkedList<Feature>();
		TreeMap<Long, Feature> sortedMap = new TreeMap<Long, Feature>();
		for (Feature feature: origList) sortedMap.put(feature.getLoc().get(0).getStart(), feature);
		while (sortedMap.values().iterator().hasNext()) newList.add(sortedMap.values().iterator().next());
		return newList;
	}
	public static List<Feature> removeById(List<Feature> features, String id) {
		for (Feature feature: features) {
			if (feature.getId().equals(id)) {
				features.remove(feature);
				break;
			}
		}
		return features;
	}
}
