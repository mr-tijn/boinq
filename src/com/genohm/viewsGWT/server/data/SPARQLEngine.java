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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.genohm.viewsGWT.client.util.FeatureComparator;
import com.genohm.viewsGWT.client.util.LocationComparator;
import com.genohm.viewsGWT.server.twinql.GFFModelBuilder;
import com.genohm.viewsGWT.server.twinql.GFFVocab;
import com.genohm.viewsGWT.server.twinql.SSBTools;
import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.Location;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.genohm.viewsGWT.shared.data.feature.GFF3Feature;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;

public class SPARQLEngine {
	
	public static String VAR_ID = "?id";
	public static String VAR_START = "?start";
	public static String VAR_END = "?end";
	public static String VAR_CHROMOSOME = "?chromosome";
	public static String VAR_STRAND = "?strand";
	public static String VAR_PARENT = "?parent";
	public static String VAR_FEATURE = "?feature";
	public static String VAR_PARENT_FEATURE = "?parent_feature";
	
	private static Logger log = Logger.getLogger(SPARQLEngine.class);
	
	protected String basePath;
	private GFFModelBuilder gffModelBuilder;
	
	public static Feature findFeatureByID(String id, Collection<Feature> targetList) {
		if (targetList == null) return null;
		for (Feature existingFeature: targetList) {
			if (existingFeature.getId() != null && existingFeature.getId().equals(id)) {
				return existingFeature;
			} else {
				@SuppressWarnings("unchecked")
				Feature foundFeature = findFeatureByID(id,(List<Feature>) existingFeature.getSubFeatures());
				if (foundFeature != null) return foundFeature;
			}
		}
		return null;
	}
	
	public void sortFeatures(List<? extends Feature> features) {
		for (Feature feature : features) {
			if (feature.getSubFeatures() != null) sortFeatures(feature.getSubFeatures());
		}
		Collections.sort(features,new FeatureComparator());
	}
	
	public List<Feature> makeFeatureTree(List<GFF3Feature> flatList) throws Exception {
		List<Feature> treeList = new LinkedList<Feature>();
		Map<Feature,String> orphans = new HashMap<Feature, String>();

		for (GFF3Feature feature: flatList) {


			Feature existingFeature = findFeatureByID(feature.getId(), treeList);
			if (existingFeature == null) existingFeature=findFeatureByID(feature.getId(), orphans.keySet());
			if (existingFeature != null) {
				// it is a location of an existing feature
				Location loc = feature.getLoc().get(0);
				Boolean duplicate = false;
				for (Location existingLoc: feature.getLoc()) {
					if (existingLoc.equals(loc)) {
						//TODO: check why DISTINCT does not eliminate
						// it is a duplicate [should never happen]
						duplicate = true;
					}
				}
				if (!duplicate) {
					existingFeature.getLoc().add(loc);
					Collections.sort(existingFeature.getLoc(), new LocationComparator());
				}
			} else {
				// is it a child ?
				String parentId = feature.getAttributeMap().get("Parent");
				if (parentId != null) {
					Feature parentFeature = findFeatureByID(parentId, treeList);
					if (parentFeature == null) {
						// the parent is still missing
						orphans.put(feature,parentId);
					} else {
						// we already have the parent
						if (parentFeature.getSubFeatures() == null) {
							parentFeature.setSubFeatures(new LinkedList<Feature>());
						}
						feature.setParent(parentFeature);
						((List<Feature>) parentFeature.getSubFeatures()).add(feature);
					}

				} else {
					// it is a new parent feature
					treeList.add(feature);
				}
			}
		} 

		// now process orphans
		for (Feature orphan: orphans.keySet()) {
			String parentId = orphans.get(orphan);
			Feature parentFeature = findFeatureByID(parentId, treeList);
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
		sortFeatures(treeList);
		return treeList;
	}
	
	public List<GFF3Feature> findFeatures(String queryString) {
		//TODO: parametrize basepath and filename
		Model gffModel = gffModelBuilder.buildModel(basePath+"/transcripts.gff3");
		//TODO: write to debug
		gffModel.write(System.out);
		Query query = QueryFactory.create(queryString);
		QueryExecution queryExecution = QueryExecutionFactory.create(query, gffModel);
		ResultSet resultSet = queryExecution.execSelect();
		LinkedList<GFF3Feature> featureList = new LinkedList<GFF3Feature>();
		while (resultSet.hasNext()) {
			try {
				GFF3Feature feat = new GFF3Feature();
				QuerySolution querySolution = resultSet.next();
				Literal id = querySolution.getLiteral(VAR_ID);
				feat.setId(id.getString());
				Literal start = querySolution.getLiteral(VAR_START);
				Literal end = querySolution.getLiteral(VAR_END);
				Literal chromosome = querySolution.getLiteral(VAR_CHROMOSOME);
				Literal strand = querySolution.getLiteral(VAR_STRAND);
				Location loc = new Location(start.getLong(), end.getLong(), chromosome.getString() , strand.getString().equals("+"));
				feat.getLoc().add(loc);
				Literal parentid = querySolution.getLiteral(VAR_PARENT);
				if (parentid != null) feat.getAttributeMap().put("Parent", parentid.getString());
				//			Iterator<String> varNameIterator = querySolution.varNames(); 
				//			while (varNameIterator.hasNext()) {
				//				String varName = varNameIterator.next();
				//				feat.getAttributeMap().put(varName, querySolution.getResource(varName).toString());
				//			}
				feat.setDescription("ID: "+id);
				featureList.add(feat);
			} catch (Throwable t) {
				log.error("could not add a feature",t);
			}
		}
		return featureList;
	}
	
	public String createSelect(String filterExpression, GenomicRegion region) {
		String CHR_PREFIX = "chr";
		String prefix = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
		String select = String.format("SELECT %s %s %s %s %s %s",VAR_ID,VAR_START,VAR_END,VAR_CHROMOSOME,VAR_STRAND,VAR_PARENT);
		
		String idClause = String.format("%s <%s> %s.", VAR_FEATURE, GFFVocab.id, VAR_ID);
		String start = String.format("%s <%s> %s.", VAR_FEATURE, GFFVocab.start, VAR_START);
		String end = String.format("%s <%s> %s.", VAR_FEATURE, GFFVocab.end, VAR_END);
		String chrom = String.format("%s <%s> %s.", VAR_FEATURE, GFFVocab.seqid, VAR_CHROMOSOME);
		String strand = String.format("%s <%s> %s.", VAR_FEATURE, GFFVocab.strand, VAR_STRAND);
		String locationFilter = String.format("FILTER(%s > %d) ",VAR_END, region.getRegionStart()) + 
								String.format("FILTER(%s < %d) ", VAR_START, region.getRegionEnd()) +
								String.format("FILTER (%s = \"%s\"^^xsd:string) ", VAR_CHROMOSOME, CHR_PREFIX + region.getChromosome());
//								String.format("FILTER (%s = \"%s\"^^xsd:string)", VAR_STRAND, region.getStrand()?"+":"-");
		String locationClause = String.format("%s %s %s %s %s", start, end, chrom, strand, locationFilter);
		
		String parentClause = String.format("OPTIONAL { %s <%s> %s. %s <%s> %s }", VAR_FEATURE, SSBTools.SSB_BASE + "part_of", VAR_PARENT_FEATURE, VAR_PARENT_FEATURE, GFFVocab.id, VAR_PARENT);
		String where = String.format("WHERE { %s %s %s %s }", idClause, locationClause, parentClause, filterExpression);
		return String.format("%s %s %s",prefix, select, where);
//		return "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
//			   "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
//			   "SELECT DISTINCT ?id ?start ?end ?chromosome ?strand ?parent ?type WHERE { ?feature <http://www.sequence-ontology.org/GFF#id> ?id. ?feature <http://www.sequence-ontology.org/GFF#start> ?start. ?feature <http://www.sequence-ontology.org/GFF#end> ?end. ?feature <http://www.sequence-ontology.org/GFF#seqid> ?chromosome. ?feature <http://www.sequence-ontology.org/GFF#strand> ?strand. OPTIONAL { ?feature <http://www.semantic-systems-biology.org/SSB#part_of> ?parent_feature. ?parent_feature <http://www.sequence-ontology.org/GFF#id> ?parent } ?feature rdf:type <http://www.semantic-systems-biology.org/SSB#SO_0000704>.}"; //FILTER(?end > 130) FILTER(?start < 219) FILTER (?chromosome = "chr1"^^xsd:string) FILTER (?strand = "+"^^xsd:string) ?feature rdf:type <http://www.semantic-systems-biology.org/SSB#SO_0000704>.  }"
	}
	
	public List<Feature> getFeatures(String filterExpression, GenomicRegion region) throws Exception {
		String fullQuery = createSelect(filterExpression, region);
		log.debug("FULL QUERY: "+fullQuery);
		return makeFeatureTree(findFeatures(fullQuery));
	}

	public String getBasePath() {
		return basePath;
	}
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	public void setGffModelBuilder(GFFModelBuilder gffModelBuilder) {
		this.gffModelBuilder = gffModelBuilder;
	}
	
	
}
