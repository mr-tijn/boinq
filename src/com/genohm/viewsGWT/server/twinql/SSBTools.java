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

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.assembler.AssemblerUtils;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

/*
 * exposes Sequence Ontology and Gene Ontology
 * from the Semantic Systems Biology rdf downloads
 * as jena Models
 */
public class SSBTools {

	public static final String DEFAULT_LANG = "en";
	public static final String PREFIX_ASM = "http://jena.hpl.hp.com/2005/11/Assembler#";
	public static final String SSB_BASE = "http://www.semantic-systems-biology.org/SSB#";
	
	//TODO parametrize with spring
	protected String assemblerFile = "/ontologies/so.ttl";
	protected String soUri = "http://www.semantic-systems-biology.org/so";
	protected String goUri = "http://www.semantic-systems-biology.org/go";
	
	
	protected static final Logger log = Logger.getLogger(SSBTools.class);

	protected static Dataset mainDataset = null;
	protected static Model sequenceOntology = null;
	protected static Model geneOntology = null;
	protected static Model sofa = null;
	
	public SSBTools() {
	}
	
	public Dataset getMainDataset() {
		if (mainDataset == null) {
//			mainDataset = TDBFactory.assembleDataset(ASSEMBLER_FILE); 
			mainDataset = (Dataset) AssemblerUtils.build(assemblerFile,PREFIX_ASM+"RDFDataset");
			//(Dataset) AssemblerUtils.build(ASSEMBLER_FILE,PREFIX_ASM+"RDFDataset");
		}
		return mainDataset;
	}

	public Model getSequenceOntology() {
		if (sequenceOntology == null) {
			sequenceOntology = getMainDataset().getNamedModel(soUri);
		}
		return sequenceOntology;
	}
	
	public Model getGeneOntology() {
		if (geneOntology == null) {
			geneOntology = getMainDataset().getNamedModel(goUri);
		}
		return geneOntology;
	}
	
	public Model getSOFA() {
		if (sofa == null) {
			String queryString = "PREFIX ssb: <http://www.semantic-systems-biology.org/SSB#> " +
					"CONSTRUCT {?s ?p ?o} WHERE {" +
					"?s ssb:subset \"SOFA\" ." +
					"?s ?p ?o ." +
					"}";
			Query query = QueryFactory.create(queryString);
			QueryExecution qe = QueryExecutionFactory.create(query, getSequenceOntology());
			sofa = qe.execConstruct();
		}
		return sofa;
	}

	public Resource getSofaByID(String id) {
		return getSofaByURI(SSB_BASE+"SO_"+id);
	}
	
	public Resource getGoByID(String id) {
		return getGoByURI(SSB_BASE+"GO_"+id);
	}

	protected Resource getOneResourceByLabel(Model m, String label, String lang) throws Exception {
		Resource found = null;
		Property hasLabel = m.getProperty(RDFS.label.getURI()); // using RDFS.label directly does not work !!!
		ResIterator resourceIterator = m.listResourcesWithProperty(hasLabel, m.createLiteral(label, lang));
		if (!resourceIterator.hasNext()) {
			throw new Exception("No resource found with label: " + label);
		} else {
			found = resourceIterator.next();
		}
		if (resourceIterator.hasNext()) {
			throw new Exception("Multiple resources found with label " + label);
		}
		return found;
	}
	
	public Resource getSofaByLabel(String label) {
		Resource result = null;
		try {
			result = getOneResourceByLabel(getSOFA(), label, DEFAULT_LANG);
		} catch (Exception e) {
			log.error("Could not get SOFA resource for label "+label,e);
			return null;
		}
		return result;
	}
		
	public Resource getSofaByURI(String URI) {
		Resource result = getSOFA().getResource(URI);
		if (getSOFA().contains(result, null)) return result;
		else return null;
	}
	
	public Resource getGoByURI(String URI) {
		Resource result = getGeneOntology().getResource(URI);
		if (getGeneOntology().contains(result,null)) return result;
		else return null;
	}

	public void setAssemblerFile(String assemblerFile) {
		this.assemblerFile = assemblerFile;
	}

	public void setSoUri(String soUri) {
		this.soUri = soUri;
	}

	public void setGoUri(String goUri) {
		this.goUri = goUri;
	}
	
	
	
}
