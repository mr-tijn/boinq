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

import java.io.File;
import java.io.FileInputStream;

import com.hp.hpl.jena.rdf.model.Model;

public class TDBLoader {
	
	/*
	 * Simple standalone loader to get rdf files into a TDB database
	 */
	
	protected static final String SO_FILE = "/ontologies/so.rdf";
	protected static final String GO_FILE = "/ontologies/gene_ontology_edit.rdf";
	protected static final String SO_URI = "http://www.semantic-systems-biology.org/so";
	protected static final String GO_URI = "http://www.semantic-systems-biology.org/go";
	protected static final String ASSEMBLER_FILE = "/ontologies/so.ttl";

	protected SSBTools ssbTools = null;

	public TDBLoader() {
		ssbTools = new SSBTools();
		ssbTools.setAssemblerFile(ASSEMBLER_FILE);
		ssbTools.setGoUri(GO_URI);
		ssbTools.setSoUri(SO_URI);
	}
	
	public void loadData(String so_file, String go_file) {
		Model sequenceOntology = ssbTools.getSequenceOntology();
		sequenceOntology.begin();
		try {
			System.out.println("starting to read "+SO_FILE);
			sequenceOntology.setNsPrefix("ssb", SSBTools.SSB_BASE);
			sequenceOntology.read(new FileInputStream(new File(SO_FILE)), SSBTools.SSB_BASE, "RDF/XML");
			sequenceOntology.commit();
			System.out.println("successfully read "+SO_FILE);
		} catch (Throwable t) {
			System.err.println("Error while reading "+SO_FILE);
			t.printStackTrace(System.err);
		} finally {
			sequenceOntology.close();
		}
		Model geneOntology = ssbTools.getGeneOntology();
		geneOntology.begin();
		try {
			System.out.println("starting to read "+GO_FILE);
			geneOntology.setNsPrefix("ssb", SSBTools.SSB_BASE);
			geneOntology.read(new FileInputStream(new File(GO_FILE)), SSBTools.SSB_BASE, "RDF/XML");
			geneOntology.commit();
			System.out.println("successfully read "+GO_FILE);
		} catch (Throwable t) {
			System.err.println("Error while reading "+GO_FILE);
			t.printStackTrace(System.err);
		} finally {
			geneOntology.close();
		}

	}
	
	public static void main(String[] args) {
		TDBLoader ldr = new TDBLoader();
		if (args.length == 0) {
			ldr.loadData(SO_FILE, GO_FILE);
		}
	}

}
