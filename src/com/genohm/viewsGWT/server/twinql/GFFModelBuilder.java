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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.intermine.bio.io.gff3.GFF3Parser;
import org.intermine.bio.io.gff3.GFF3Record;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

//TODO: to be replaced with GFFIterator 

public class GFFModelBuilder {
	protected static Logger log = Logger.getLogger(GFFModelBuilder.class);
	protected static final String PREFIX_SSB = "http://www.semantic-systems-biology.org/SSB#";

	private SSBTools ssbTools;
	
	public Model buildModel(String gff3file) {
		Model gffModel = ModelFactory.createDefaultModel();
		int counter = 0;
		String baseURI = "file://"+gff3file+"#";
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(gff3file));
			@SuppressWarnings("unchecked")
			Iterator<GFF3Record> gff3_iterator = GFF3Parser.parse(fileReader);
			while (gff3_iterator.hasNext()) {
				try {
					GFF3Record lastRecord = gff3_iterator.next();
					String id = lastRecord.getId();
					if (id == null || id.length() == 0) {
						id = "GFFASSEMBLER_GENERATED_ID" + String.valueOf(++counter);
					}
					Resource newRes = gffModel.createResource(baseURI + id); // will fetch if exists
					// fetch type from SOFA
					Resource resType = null;
					if (lastRecord.getType().startsWith("SO:")) {
						resType = ssbTools.getSofaByID(lastRecord.getType().substring(3));
					} else {
						resType = ssbTools.getSofaByLabel(lastRecord.getType());
					}
					if (resType == null) {
						// type not known in SOFA
						resType = gffModel.createResource(baseURI + lastRecord.getType());
					}
					newRes.addProperty(RDF.type, resType);
					if (lastRecord.getId() != null) newRes.addLiteral(GFFVocab.id, lastRecord.getId());
					if (lastRecord.getSequenceID() != null) newRes.addLiteral(GFFVocab.seqid, lastRecord.getSequenceID());
					if (lastRecord.getSource() != null) newRes.addLiteral(GFFVocab.source, lastRecord.getSource());
					newRes.addLiteral(GFFVocab.start, lastRecord.getStart());
					newRes.addLiteral(GFFVocab.end, lastRecord.getEnd());
					if (lastRecord.getStrand() != null) newRes.addLiteral(GFFVocab.strand, lastRecord.getStrand());
					if (lastRecord.getScore() != null) newRes.addLiteral(GFFVocab.score, lastRecord.getScore());
					for (String additionalAttributeName: lastRecord.getAttributes().keySet()) {
						if (additionalAttributeName.equalsIgnoreCase("Parent")) {
							for (String parentId: lastRecord.getAttributes().get(additionalAttributeName)) {
								Resource parentRes = gffModel.createResource(baseURI + parentId);
								newRes.addProperty(gffModel.getProperty(PREFIX_SSB+"part_of"), parentRes); 
							}
						} else if (additionalAttributeName.equalsIgnoreCase("id")) {
							continue;
						} else {
							Property attributeProperty = gffModel.createProperty(baseURI + additionalAttributeName);
							for (String attributeValue: lastRecord.getAttributes().get(additionalAttributeName)) {
								newRes.addLiteral(attributeProperty, attributeValue);
							}
						}
					}
				} catch (Exception e) {
					log.error("Error while parsing "+gff3file,e);
				}

			} 
		} catch (Exception e) {
			log.error("Error while exposing "+gff3file,e);
		}
		return gffModel;
	}

	public void setSsbTools(SSBTools ssbTools) {
		this.ssbTools = ssbTools;
	}

}
