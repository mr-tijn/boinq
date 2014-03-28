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

import java.util.LinkedList;
import java.util.List;

import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.Location;
import com.genohm.viewsGWT.shared.ZoomLevel;
import com.genohm.viewsGWT.shared.data.feature.Feature;

public class RegionalFeatureSet {
	// contains all features greater than minFeatureLength and detailed features that are larger than minDetailLength in a region
	protected GenomicRegion genomicRegion;
	//protected ZoomLevel zoomLevel;
	protected Integer minDetailLength;
	protected Integer minFeatureLength;
	protected String filterExpression;
 	protected List<Feature> features;
 	public RegionalFeatureSet(GenomicRegion genomicRegion, Integer minDetailLength, Integer minFeatureLength, String filterExpression, List<Feature> features) {
		super();
		this.genomicRegion = genomicRegion;
		this.minFeatureLength = minFeatureLength;
		//this.zoomLevel = zoomLevel;
		this.minDetailLength = minDetailLength;
		this.filterExpression = filterExpression;
		this.features = features;
	}
	public GenomicRegion getGenomicRegion() {
		return genomicRegion;
	}
	public void setGenomicRegion(GenomicRegion genomicRegion) {
		this.genomicRegion = genomicRegion;
	}
//	public ZoomLevel getZoomLevel() {
//		return zoomLevel;
//	}
//	public void setZoomLevel(ZoomLevel zoomLevel) {
//		this.zoomLevel = zoomLevel;
//	}
 	public List<Feature> getFeatures(GenomicRegion region, Integer minFeatureLength) {
		List<Feature> subFeatures = new LinkedList<Feature>();
		for (Feature feature: features) {
			for (Location loc: feature.getLoc()) {		
			if (loc.getStart() <= region.getVisibleEnd() && loc.getEnd() >= region.getVisibleStart() && loc.getLength() >= minFeatureLength) subFeatures.add(feature);
		}
			}
		return subFeatures;
	}
	public Integer getMinDetailLength() {
		return minDetailLength;
	}
	public void setMinDetailLength(Integer minDetailLength) {
		this.minDetailLength = minDetailLength;
	}
	public Integer getMinFeatureLength() {
		return minFeatureLength;
	}
	public void setMinFeatureLength(Integer minFeatureLength) {
		this.minFeatureLength = minFeatureLength;
	}
 	public String getFilterExpression() {
		return filterExpression;
	}
	public void setFilterExpression(String filterExpression) {
		this.filterExpression = filterExpression;
	}
	public List<Feature> getFeatures() {
		return features;
	}
 	public void setFeatures(List<Feature> features) {
		this.features = features;
	}
}
