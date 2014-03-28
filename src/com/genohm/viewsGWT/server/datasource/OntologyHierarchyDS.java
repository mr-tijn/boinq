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
package com.genohm.viewsGWT.server.datasource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.genohm.viewsGWT.client.ViewsGWT;
import com.genohm.viewsGWT.server.external.SPARQLClient;
import com.genohm.viewsGWT.server.ontologyclient.CommonVocabulary;
import com.genohm.viewsGWT.server.ontologyclient.DatasourceVocabulary;
import com.genohm.viewsGWT.server.ontologyclient.TrackVocabulary;
import com.genohm.viewsGWT.shared.data.SPARQLResultSet;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.SortCondition;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;
import com.hp.hpl.jena.sparql.expr.E_Bound;
import com.hp.hpl.jena.sparql.expr.E_LogicalNot;
import com.hp.hpl.jena.sparql.expr.E_LogicalOr;
import com.hp.hpl.jena.sparql.expr.E_Regex;
import com.hp.hpl.jena.sparql.expr.E_Str;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementNamedGraph;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.isomorphic.datasource.BasicDataSource;
import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.isomorphic.util.ErrorReport;

public class OntologyHierarchyDS extends BasicDataSource {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5189696730166598289L;
	private static final Logger log = Logger.getLogger(OntologyHierarchyDS.class);
	public static final String defaultPrefixes =
			"PREFIX owl:  <http://www.w3.org/2002/07/owl#>\n" +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";
	public static PrefixMapping commonPrefixes = new PrefixMappingImpl();
	{
		commonPrefixes.setNsPrefix("rdf",CommonVocabulary.rdfBaseURI);
		commonPrefixes.setNsPrefix("rdfs", CommonVocabulary.rdfsBaseURI);
		commonPrefixes.setNsPrefix("track", TrackVocabulary.baseURI);
		commonPrefixes.setNsPrefix("ds", DatasourceVocabulary.baseURI);
		commonPrefixes.setNsPrefix("prop", DatasourceVocabulary.propertyHolderBaseURI);
		commonPrefixes.setNsPrefix("obo", CommonVocabulary.oboBaseURI);
		commonPrefixes.setNsPrefix("xmlschema", CommonVocabulary.xmlSchemaURI);
		commonPrefixes.setNsPrefix("owl", CommonVocabulary.owlBaseURI);
	}

	protected SPARQLClient sparqlClient;

	protected String getRootNodesQuery(String graph, String targetFilter) {
		Query query = new Query();
		query.setQuerySelectType();
		query.setPrefixMapping(commonPrefixes);
		query.addResultVar("term");
		query.addResultVar("label");
		Node term = Node.createVariable("term");
		Node label = Node.createVariable("label");
		Node superClass = Node.createVariable("super");
		ElementTriplesBlock triples = new ElementTriplesBlock();
		triples.addTriple(new Triple(term, RDF.type.asNode() , OWL.Class.asNode()));
		triples.addTriple(new Triple(term, RDFS.label.asNode(), label));
		ElementTriplesBlock optionalTriplesBlock = new ElementTriplesBlock();
		optionalTriplesBlock.addTriple(new Triple(term, RDFS.subClassOf.asNode(), superClass));
		ElementOptional optionalTriples = new ElementOptional(optionalTriplesBlock);
		ElementFilter superClassNotBound = new ElementFilter(new E_LogicalNot(new E_Bound(new ExprVar(superClass))));
		ElementGroup group = new ElementGroup();
		group.addElement(triples);
		group.addElement(optionalTriples);
		group.addElement(superClassNotBound);
		if (targetFilter != null && targetFilter.length() > 0) {
				Element custom = QueryFactory.createElement(targetFilter);
				group.addElement(custom);
		}
		ElementNamedGraph graphQuery = new ElementNamedGraph(Node.createURI(graph),group);
		query.setQueryPattern(graphQuery);
		query.addOrderBy(label, 0);
		return query.toString(Syntax.syntaxSPARQL_11);
	}
	
	public OntologyHierarchyDS() {
		log.debug("Ontology Hierarchy datasource initialized");
	}
	
	protected String getChildNodesQuery(String graph, String parentURI, String targetFilter) {
		Query query = new Query();
		query.setQuerySelectType();
		query.setPrefixMapping(commonPrefixes);
		Node term = Node.createVariable("term");
		Node label = Node.createVariable("label");
		Node parent = Node.createVariable("parent");
		query.addResultVar(term);
		query.addResultVar(label);
		query.addResultVar(parent);
		ElementTriplesBlock triples = new ElementTriplesBlock();
		triples.addTriple(new Triple(term, RDF.type.asNode() , OWL.Class.asNode()));
		triples.addTriple(new Triple(term, RDFS.label.asNode(), label));
		triples.addTriple(new Triple(term, RDFS.subClassOf.asNode(), Node.createURI(parentURI)));
		ElementGroup group = new ElementGroup();
		group.addElement(triples);
		if (targetFilter != null && targetFilter.length() > 0) {
				Element custom = QueryFactory.createElement(targetFilter);
				group.addElement(custom);
		}
		ElementNamedGraph graphQuery = new ElementNamedGraph(Node.createURI(graph),group);
		query.setQueryPattern(graphQuery);
		query.addOrderBy(label, 0);
		return query.toString(Syntax.syntaxSPARQL_11);
	}
		
	private String getLabelSearchQuery(String graph, String labelFilter, String targetFilter) {
		Query query = new Query();
		query.setQuerySelectType();
		query.setPrefixMapping(commonPrefixes);
		Node term = Node.createVariable("term");
		Node label = Node.createVariable("label");
		Node parent = Node.createVariable("parent");
		query.addResultVar(term);
		query.addResultVar(label);
		query.addResultVar(parent);
		ElementTriplesBlock triples = new ElementTriplesBlock();
		triples.addTriple(new Triple(term, RDF.type.asNode() , OWL.Class.asNode()));
		triples.addTriple(new Triple(term, RDFS.label.asNode(), label));
		Expr matchLabel = new E_Regex(new E_Str(new ExprVar(label)), labelFilter,"i");
		ElementFilter regexFilter = new ElementFilter(matchLabel);
		ElementGroup group = new ElementGroup();
		group.addElement(triples);
		group.addElement(regexFilter);
		if (targetFilter != null && targetFilter.length() > 0) {
				Element custom = QueryFactory.createElement(targetFilter);
				group.addElement(custom);
		}
		ElementNamedGraph graphQuery = new ElementNamedGraph(Node.createURI(graph),group);
		query.setQueryPattern(graphQuery);
		return query.toString(Syntax.syntaxSPARQL_11);
	}

	
	@Override
	public DSResponse executeFetch(DSRequest request) throws Exception {
		
		
//		long offset = request.getStartRow();
//		long limit = 1L + request.getEndRow() - offset;
		
		String labelFilter = (String) request.getAdvancedCriteria().getFieldValue("label");
		String ontologyGraph = (String) request.getAdvancedCriteria().getFieldValue("ontologyGraph");
		String baseIRI = (String) request.getAdvancedCriteria().getFieldValue("parent");
		String endPoint = (String) request.getAdvancedCriteria().getFieldValue("targetEndpoint");
		if (endPoint == null) endPoint = "http://sparql.bioontology.org/ontologies/sparql/?apikey=6934daf9-5e1a-4a5a-a571-9833e04a6fa0";
		
		Boolean allChildren = ((String) request.getAdvancedCriteria().getFieldValue("allChildren") != null);
		
		String targetFilter = (String) request.getAdvancedCriteria().getFieldValue("targetFilter");
		if (targetFilter == null) targetFilter = "";
		
		sparqlClient = new SPARQLClient(endPoint);
		
		String sparqlQuery = null;
		if (baseIRI != null) {
			sparqlQuery = getChildNodesQuery(ontologyGraph, baseIRI, targetFilter);
		} else if (labelFilter != null) {
			sparqlQuery = getLabelSearchQuery(ontologyGraph, labelFilter, targetFilter);
		} else   {
			sparqlQuery = getRootNodesQuery(ontologyGraph, targetFilter);
		}
		DSResponse resp = new DSResponse();
		SPARQLResultSet result = null;
		try {
			if (allChildren) {
				result = sparqlClient.query(sparqlQuery,true,false);
			} else {
				result = sparqlClient.query(sparqlQuery);
			}
		} catch (Exception e) {
			resp.setStatus(DSResponse.STATUS_FAILURE);
			ErrorReport rep = new ErrorReport();
			rep.addError("ontologyGraph", "Could not perform query for ontology hierarchy\n"+e.getMessage());
			resp.setErrorReport(rep);
			return resp;			
		}
		resp.setStatus(DSResponse.STATUS_SUCCESS);
		resp.setData(result.getRecords());
		resp.setStatus(0);
		return resp;

	}

	
}
