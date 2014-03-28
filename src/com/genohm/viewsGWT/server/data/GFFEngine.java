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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.biojava3.genome.parsers.gff.FeatureI;
import org.biojava3.genome.parsers.gff.FeatureList;
import org.biojava3.genome.parsers.gff.GFF3Reader;

import com.genohm.viewsGWT.client.util.FeatureComparator;
import com.genohm.viewsGWT.client.util.LocationComparator;
import com.genohm.viewsGWT.shared.Location;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.genohm.viewsGWT.shared.data.feature.GFF3Feature;


// TODO: see what's useable and kick the rest out

public class GFFEngine {

	public static Feature findFeatureByID(String id, Collection<Feature> targetList) {
		if (targetList == null) return null;
		for (Feature existingFeature: targetList) {
			if (existingFeature.getId() != null && existingFeature.getId().equals(id)) {
				return existingFeature;
			} else {
				@SuppressWarnings("unchecked")
				Feature foundFeature = findFeatureByID(id,(List<Feature>)  existingFeature.getSubFeatures());
				if (foundFeature != null) return foundFeature;
			}
		}
		return null;
	}


	@SuppressWarnings("unchecked")
	public List<Feature> getFeatureGFF(String filePath) throws Exception {

		FeatureList features = (FeatureList) GFF3Reader.read(filePath);


		List<Feature> featureList = new LinkedList<Feature>();
		Map<Feature,String> orphans = new HashMap<Feature, String>();


		Iterator<FeatureI> it = features.iterator();
		while (it.hasNext()) {

			org.biojava3.genome.parsers.gff.Feature inputFeature = (org.biojava3.genome.parsers.gff.Feature) it.next();

			Long End = Math.abs(Long.parseLong(String.valueOf(inputFeature.location().getEnd())));
			Long Start = Math.abs(Long.parseLong(String.valueOf(inputFeature.location().getBegin())));

			String description = "ID: " + inputFeature.getAttribute("ID") + "\nType: " + inputFeature.type();
			GFF3Feature newFeature = new GFF3Feature (inputFeature.getAttribute("ID"),
					description,inputFeature.score(), inputFeature.getAttribute("ID"),null,null,null,null);
			Feature existingFeature = findFeatureByID(newFeature.getId(), featureList);
			if (existingFeature != null) {
				existingFeature.getLoc().add(new Location(Start, End, inputFeature.seqname(), !inputFeature.location().isNegative()));
				Collections.sort(existingFeature.getLoc(), new LocationComparator());

			} else {

				newFeature.getLoc().add(new Location(Start, End, inputFeature.seqname(), !inputFeature.location().isNegative()));
				Collections.sort(newFeature.getLoc(), new LocationComparator());


				String parentId = inputFeature.getAttribute("Parent");
				if (parentId != null) {
					Feature parentFeature = findFeatureByID(parentId, featureList);
					if (parentFeature == null) {
						orphans.put(newFeature,parentId);
					} else {
						if (parentFeature.getSubFeatures() == null) {
							parentFeature.setSubFeatures(new LinkedList<Feature>());
						}
						newFeature.setParent(parentFeature);
						((List<Feature>) parentFeature.getSubFeatures()).add(newFeature);
					}

				} else {
					featureList.add(newFeature);
				}
			}
		} 

		for (Feature orphan: orphans.keySet()) {
			String parentId = orphans.get(orphan);
			Feature parentFeature = findFeatureByID(parentId, featureList);
			if (parentFeature == null) {
				Set<Feature> otherOrphans = new HashSet<Feature>();
				otherOrphans.addAll(orphans.keySet());
				otherOrphans.remove(orphan);
				parentFeature = findFeatureByID(parentId, otherOrphans);
			} 
			if (parentFeature == null) {
				throw new Exception("Orphan features found with parent " + parentId);
			} else {
				if (parentFeature.getSubFeatures() == null) {
					parentFeature.setSubFeatures(new LinkedList<Feature>());
				}
				orphan.setParent(parentFeature);
				((List<Feature>) parentFeature.getSubFeatures()).add(orphan);
			}
		}
		sortFeatures(featureList);
		return featureList;
	}

	public void sortFeatures(List<? extends Feature> features) {
		for (Feature feature : features) {
			if (feature.getSubFeatures() != null) sortFeatures(feature.getSubFeatures());
		}
		Collections.sort(features,new FeatureComparator());
	}

	public List<org.biojava3.genome.parsers.gff.Feature> getFeaturesById (String filePath, String id) throws IOException{
		FeatureList features = (FeatureList)
				GFF3Reader.read(filePath);
		List<org.biojava3.genome.parsers.gff.Feature> featureList = new LinkedList<org.biojava3.genome.parsers.gff.Feature>();
		Iterator<FeatureI> it = features.iterator();
		while (it.hasNext()) {
			org.biojava3.genome.parsers.gff.Feature inputFeature = (org.biojava3.genome.parsers.gff.Feature) it.next();
			if (inputFeature.getAttribute("ID").equals(id)){
				featureList.add(inputFeature); 
			}
		}
		System.err.println("la liste des features du gff"+featureList.size());
		return featureList;
	}
}

