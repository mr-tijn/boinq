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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.genohm.viewsGWT.shared.data.feature.GFF3Feature;

public class SPARQLEngineTest {

	protected SPARQLEngine sparqlEngine;
	
	@Before
	public void setUp() throws Exception {
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("WEB-INF/spring-service.xml");
		sparqlEngine = (SPARQLEngine) context.getBean("sparqlEngine");
	}

	@Test
	public void testFindFeatures() {
//		String id = String.format("?res <%s> ?id.", GFFVocab.id);
//		String location = String.format("?res <%s> ?start. ?res <%s> ?end. ?res <%s> ?chromosome. ?res <%s> ?strand.", GFFVocab.start,GFFVocab.end,GFFVocab.seqid,GFFVocab.strand);
//		String parent = String.format("OPTIONAL { ?res <%s> ?parent_res. ?parent_res <%s> ?parent }", SSBTools.SSB_BASE + "part_of", GFFVocab.id);
//		String queryString = String.format("SELECT ?id ?start ?end ?chromosome ?strand ?parent WHERE { %s %s %s }",id,location,parent);
		String queryString = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
							 "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
							 "SELECT ?id ?start ?end ?chromosome ?strand ?parent " +
							 "WHERE { ?feature <http://www.sequence-ontology.org/GFF#id> ?id. " +
							 		 "?feature <http://www.sequence-ontology.org/GFF#start> ?start. " +
							 		 "?feature <http://www.sequence-ontology.org/GFF#end> ?end. " +
							 		 "?feature <http://www.sequence-ontology.org/GFF#seqid> ?chromosome. " +
							 		 "?feature <http://www.sequence-ontology.org/GFF#strand> ?strand. " +
							 		 "FILTER(?end > 0)" +
							 		 "FILTER(?start < 20000)" +
							 		 "FILTER (?chromosome = \"chr1\"^^xsd:string)" +
							 		 "FILTER (?strand = \"+\"^^xsd:string) " +
							 		 "OPTIONAL { ?res <http://www.semantic-systems-biology.org/SSB#part_of> ?parent_res. ?parent_res <http://www.sequence-ontology.org/GFF#id> ?parent } " +
							 		 "}";
							 		 //"{?feature rdf:class <http://www.semantic-systems-biology.org/SSB#SO_0000704>. } UNION {?feature rdf:class <http://www.semantic-systems-biology.org/SSB#SO_0000673>. }  }";
		System.out.println(queryString);
		List<GFF3Feature> resultList = sparqlEngine.findFeatures(queryString);
		for (Feature feat: resultList) {
			System.out.println(feat);
		}
		try {
			List <? extends Feature> sortedList = sparqlEngine.makeFeatureTree(resultList);
			for (Feature feat: sortedList) {
				System.out.println(feat);
			}
		} catch (Throwable t) {
			System.err.print(t);
		}
	}

}
