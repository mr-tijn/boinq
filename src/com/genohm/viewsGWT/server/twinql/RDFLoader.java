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
package com.genohm.viewsGWT.server.twinql;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

public class RDFLoader {
	protected String rdfPath = "/Users/martijn/Documents/Biodata/ontologies/obo_in_rdf/gene_ontology_edit.rdf";
	protected static final String SO_URI = "http://www.semantic-systems-biology.org/so";
	protected static final String GO_URI = "http://www.semantic-systems-biology.org/go";
	protected static final String ASSEMBLER_FILE = "/ontologies/so.ttl";
	
	public static void main(String[] args) {
//		RDFLoader ldr = new RDFLoader();
//		Model m = FileManager.get().loadModel(ldr.rdfPath);
		SSBTools ssbTools = new SSBTools();
		ssbTools.setAssemblerFile(ASSEMBLER_FILE);
		ssbTools.setGoUri(GO_URI);
		ssbTools.setSoUri(SO_URI);
		Model go = ssbTools.getGeneOntology();
		System.out.println(go.size());
		QueryExecution qe = QueryExecutionFactory.create("SELECT DISTINCT ?s WHERE { ?s ?p ?o } LIMIT 10", go);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			System.out.println(qs.getLiteral("?s"));
		}
		
	}

}
