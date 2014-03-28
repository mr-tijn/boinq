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
package com.genohm.viewsGWT.server.data;

import java.util.LinkedList;
import java.util.List;

import uk.ac.ebi.das.jdas.adapters.features.DasGFFAdapter;
import uk.ac.ebi.das.jdas.adapters.features.DasGFFAdapter.GFFAdapter;
import uk.ac.ebi.das.jdas.adapters.features.DasGFFAdapter.SegmentAdapter;
import uk.ac.ebi.das.jdas.adapters.features.FeatureAdapter;
import uk.ac.ebi.das.jdas.client.FeaturesClient;

import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.Location;
import com.genohm.viewsGWT.shared.data.feature.Feature;


public class DASEngine {

	//FIXME: this is a preliminary stub
	
	protected FeaturesClient featuresClient;
	protected String DASURL;
	
	public DASEngine(String DASHost, int DASPort) throws Exception {
		featuresClient = new FeaturesClient();
	}
	
	protected String subPath(String url, String subPath) {
		while (url.endsWith("/")) {
			url = url.substring(0, url.length()-1);
		}
		while (subPath.startsWith("/")) {
			subPath = subPath.substring(1);
		}
		return url + "/" + subPath;
	}
	
	protected String range(GenomicRegion region) {
		return region.getChromosome() + ":" + region.getRegionStart() + "," + region.getRegionEnd();
	}
	
	protected String combine(List<String> notes) {
		StringBuffer sb = new StringBuffer();
		for (String note: notes) {
			sb.append(note);
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public List<Feature> getFeatures(String server, String datasource, GenomicRegion region) throws Exception {
		String dasServer = subPath(server, "das");
		String dsURI = subPath(dasServer, datasource);
		List<Feature> features = new LinkedList<Feature>();
		List<String> segments = new LinkedList<String>();
		segments.add(range(region));
		DasGFFAdapter adapter = featuresClient.fetchData(dsURI, segments);
		GFFAdapter gffAdapter = adapter.getGFF();
		for (SegmentAdapter segmentAdapter: gffAdapter.getSegment()) {
			for (FeatureAdapter featureAdapter: segmentAdapter.getFeature()) {
				Double score = null;
				try {
					score = Double.parseDouble(featureAdapter.getScore());
				} catch (Exception e) {}
				Location loc = new Location();
				List<Location> locs = new LinkedList<Location>();
				Feature feature = new Feature();
				feature.setId(featureAdapter.getId());
				feature.setName(featureAdapter.getLabel());
				feature.setScore(score);
				feature.setDescription(combine(featureAdapter.getNotes()));
				loc.setChr(region.getChromosome());
				loc.setStart(new Long(featureAdapter.getStart()));
				loc.setEnd(new Long(featureAdapter.getEnd()));
				locs.add(loc);
				feature.setLoc(locs);
				features.add(feature);
			}
		}
		return features;
	}
	
}
