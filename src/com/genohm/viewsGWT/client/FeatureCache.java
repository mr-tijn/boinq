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
package com.genohm.viewsGWT.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.ZoomLevel;
import com.genohm.viewsGWT.shared.data.FeatureDatasource;
import com.genohm.viewsGWT.shared.data.feature.Feature;

public class FeatureCache {
	static final int MAX_FEATURE_BACKLOG_PER_SOURCE = 1000;
	// keeps a backlog of features of at most MAX_FEATURE_BACKLOG_PER_SOURCE
	
	protected final Map<FeatureDatasource,LinkedList<RegionalFeatureSet>> featuresPerDataSource = new HashMap<FeatureDatasource, LinkedList<RegionalFeatureSet>>();
	
	public Boolean contains(FeatureDatasource ds, Integer minDetailLength, Integer minFeatureLength, String filterExpression, GenomicRegion region) {
		List<RegionalFeatureSet> cachedFeatureSets = featuresPerDataSource.get(ds);
		if (cachedFeatureSets == null) return false;
		for (RegionalFeatureSet featureSet: cachedFeatureSets) {
			if (featureSet.getMinFeatureLength() <= minFeatureLength && featureSet.getMinDetailLength() <= minDetailLength && featureSet.getFilterExpression().equals(filterExpression) && featureSet.getGenomicRegion().contains(region)) return true;
		}
		return false;
	}
 	public List<Feature> getFeatures(FeatureDatasource ds, Integer minDetailLength, Integer minFeatureLength, GenomicRegion region) {
		List<RegionalFeatureSet> cachedFeatureSets = featuresPerDataSource.get(ds);
		if (cachedFeatureSets == null) return null;
		Iterator<RegionalFeatureSet> it = cachedFeatureSets.iterator();
		while (it.hasNext()) {
			RegionalFeatureSet featureSet = it.next();
			if (featureSet.getMinFeatureLength() <= minFeatureLength && featureSet.getMinDetailLength() <= minDetailLength && featureSet.getGenomicRegion().contains(region)) {
				// move to top
				it.remove();
				cachedFeatureSets.add(featureSet); // breaks the iterator but won't need it anymore
				return featureSet.getFeatures(region, minFeatureLength);
			}
		}
		return  null;
	}
 	public void addFeatures(FeatureDatasource ds, Integer minDetailLength, Integer minFeatureLength, String filterExpression, GenomicRegion region, List<Feature> features) {
		LinkedList<RegionalFeatureSet> cachedFeatureSets = featuresPerDataSource.get(ds);
		if (cachedFeatureSets == null) {
			cachedFeatureSets = new LinkedList<RegionalFeatureSet>();
			cachedFeatureSets.add(new RegionalFeatureSet(region, minDetailLength, minFeatureLength, filterExpression, features));
			featuresPerDataSource.put(ds, cachedFeatureSets);
		} else {
			// remove oldest
			int totalFeatures = 0;
			for (RegionalFeatureSet regionalFeatureSet: cachedFeatureSets) {
				totalFeatures += regionalFeatureSet.getFeatures().size();
			}
			while (totalFeatures > MAX_FEATURE_BACKLOG_PER_SOURCE) {
				totalFeatures -= cachedFeatureSets.getLast().getFeatures().size();
				cachedFeatureSets.removeLast();
			}
			// add newest
			cachedFeatureSets.add(new RegionalFeatureSet(region, minDetailLength, minFeatureLength, filterExpression, features));
		}
	}
}
