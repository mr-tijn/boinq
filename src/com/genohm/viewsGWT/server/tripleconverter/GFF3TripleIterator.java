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
package com.genohm.viewsGWT.server.tripleconverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.LineIterator;
import org.intermine.bio.io.gff3.GFF3Parser;
import org.intermine.bio.io.gff3.GFF3Record;

import com.genohm.viewsGWT.server.twinql.GFFVocab;
import com.genohm.viewsGWT.server.twinql.SSBTools;
import com.genohm.viewsGWT.shared.GenomicRegion;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class GFF3TripleIterator implements Iterator<Triple> {
	
	public static String baseURI = "http://www.genohm.com/gff3/feature#";
	private static SSBTools ssbTools = new SSBTools();
	private Iterator<GFF3Record> gff3Iterator;
	private GenomicRegion region = null;
	private List<Triple> currentTriples = new LinkedList<Triple>();
	private long counter = 0;
	
	public GFF3TripleIterator(File file, GenomicRegion region) throws FileNotFoundException, IOException {
		this(file);
		this.region = region;
	}
	public GFF3TripleIterator(File file) throws FileNotFoundException, IOException {
		BufferedReader fileReader = new BufferedReader(new FileReader(file));
		this.gff3Iterator = GFF3Parser.parse(fileReader);
	}
	
	@Override
	public boolean hasNext() {
		if (currentTriples.isEmpty()) {
			return gff3Iterator.hasNext();
		} else {
			return true;
		}
	}
	@Override
	public Triple next() {
		
		if (currentTriples.isEmpty()) {
			GFF3Record lastRecord = gff3Iterator.next();
			String id = lastRecord.getId();
			if (id == null || id.length() == 0) {
				id = "GFFASSEMBLER_GENERATED_ID" + ++counter;
			}
			// fetch type from SOFA
			Node resType = null;
			if (lastRecord.getType().startsWith("SO:")) {
				resType = ssbTools.getSofaByID(lastRecord.getType().substring(3)).asNode();
			} else {
				resType = ssbTools.getSofaByLabel(lastRecord.getType()).asNode();
			}
			if (resType == null) {
				// type not known in SOFA
				resType = Node.createURI(baseURI + lastRecord.getType());
			}
			Node feature = Node.createURI(baseURI + id);
		}
		return currentTriples.remove(0);
		
	}
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}		
}
