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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.genohm.viewsGWT.shared.data.feature.Feature;

public class VerticalStacker {
	protected Map<Integer,Long> levelEnds = null;
	protected Map<Feature,Integer> featureLevels = null;
	protected Map<Integer,Integer> levelHeights = null;
	
	protected int maxFeatureLevel = 0;
	public VerticalStacker() {
		
	}
	

	public void init(List<? extends Feature> features) {
		List<Feature> sorted = new LinkedList<Feature>();
		sorted.addAll(features);
		Collections.sort(sorted, new Comparator<Feature> () {
			@Override
			public int compare(Feature o1, Feature o2) {
				return (int) (o1.getStart() - o2.getStart());
			}
		});
		maxFeatureLevel = 0;
		levelEnds = new HashMap<Integer, Long>();
		featureLevels = new HashMap<Feature, Integer>();
		levelHeights = new HashMap<Integer, Integer>();
		
		if (features.size()!=0){
			for (Feature feature: sorted) {
				int currentLevel = 0;
				for (currentLevel = 0; currentLevel < levelEnds.size(); currentLevel++) {
					if (levelEnds.get(currentLevel) < feature.getStart()) {
						levelEnds.put(currentLevel,feature.getEnd());
						featureLevels.put(feature, currentLevel);
						break;
					}
				}			
				levelEnds.put(currentLevel,feature.getEnd() );
				featureLevels.put(feature, currentLevel);
				maxFeatureLevel = currentLevel;

			}
		}
	}
 

	public int getLevel(Feature feature) throws Exception  {
		Integer level = null;
		if (featureLevels == null) 
			throw new Exception("stacker not initialized");
		level = featureLevels.get(feature);
		return level;
	}
	

	
	public int getLevelCount(){
		return levelEnds.size();
	}
	 

}