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
package com.genohm.viewsGWT.server.ontologyclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.collections.list.TypedList;
import org.apache.derby.impl.sql.execute.CountAggregator;
import org.apache.log4j.Logger;
import org.hibernate.annotations.AccessType;
import org.hibernate.hql.ast.tree.LiteralNode;
import org.hibernate.search.query.fieldcache.FieldCacheLoadingType;

import com.genohm.viewsGWT.server.external.SPARQLClient;
import com.genohm.viewsGWT.shared.data.RawSPARQLResultSet;
import com.genohm.viewsGWT.shared.data.SPARQLResultSet;
import com.genohm.viewsGWT.shared.data.Term;
import com.genohm.viewsGWT.shared.fieldconfig.FieldConfig;
import com.genohm.viewsGWT.shared.fieldconfig.FieldType;
import com.genohm.viewsGWT.shared.fieldconfig.Target;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.graph.query.Domain;
import com.hp.hpl.jena.graph.query.Element;
import com.hp.hpl.jena.graph.query.NamedGraphMap;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Bound;
import com.hp.hpl.jena.sparql.expr.E_IsBlank;
import com.hp.hpl.jena.sparql.expr.E_LogicalAnd;
import com.hp.hpl.jena.sparql.expr.E_LogicalNot;
import com.hp.hpl.jena.sparql.expr.E_LogicalOr;
import com.hp.hpl.jena.sparql.expr.E_NotOneOf;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprAggregator;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.aggregate.AggCountDistinct;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueNode;
import com.hp.hpl.jena.sparql.path.P_Alt;
import com.hp.hpl.jena.sparql.path.P_Link;
import com.hp.hpl.jena.sparql.path.P_ZeroOrMore1;
import com.hp.hpl.jena.sparql.path.Path;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementNamedGraph;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.sparql.util.NodeFactory;
import com.hp.hpl.jena.sparql.util.NodeUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class TrackClient {
	
	public static Logger log = Logger.getLogger(TrackClient.class);
	protected static Model dummy = ModelFactory.createDefaultModel();

	
	public static PrefixMapping commonPrefixes = new PrefixMappingImpl();
	{
		commonPrefixes.setNsPrefix("rdf",CommonVocabulary.rdfBaseURI);
		commonPrefixes.setNsPrefix("rdfs", CommonVocabulary.rdfsBaseURI);
		commonPrefixes.setNsPrefix("track", TrackVocabulary.baseURI);
		commonPrefixes.setNsPrefix("obo", CommonVocabulary.oboBaseURI);
		commonPrefixes.setNsPrefix("xmlschema", CommonVocabulary.xmlSchemaURI);
		commonPrefixes.setNsPrefix("owl", CommonVocabulary.owlBaseURI);
	}
	
	// spring managed
	protected SPARQLClient sparqlClient;
	
	public List<Term> getDatasources() throws Exception {
		Query query = new Query();
		query.setQuerySelectType();
		query.setPrefixMapping(commonPrefixes);
		query.addResultVar("datasource");
		query.addResultVar("datasourcename");
		//query.addGraphURI("http://www.boinq.org/tracks");
		ElementTriplesBlock triples = new ElementTriplesBlock();
		triples.addTriple(new Triple(Node.createVariable("datasource"), RDF.type.asNode(), DatasourceVocabulary.datasource));
		ElementTriplesBlock optionalTriplesBlock = new ElementTriplesBlock();
		optionalTriplesBlock.addTriple(new Triple(Node.createVariable("datasource"), RDFS.label.asNode(), Node.createVariable("datasourcename")));
		ElementOptional optionalTriples = new ElementOptional(optionalTriplesBlock);
		ElementGroup group = new ElementGroup();
		group.addElement(triples);
		group.addElement(optionalTriples);
		ElementNamedGraph graphQuery = new ElementNamedGraph(DatasourceVocabulary.datasourceGraph,group);
		query.setQueryPattern(graphQuery);
		log.debug("Querying for datasources. Query: \n"+query.toString(Syntax.syntaxSPARQL));
		SPARQLResultSet result = sparqlClient.query(query);
		List<Term> resultList = new LinkedList<Term>();
		for (Map<String,String> record: result.getRecords()) {
			resultList.add(new Term(record.get("datasource"),record.get("datasourcename")));
		}
		return resultList;
	}
	
	public List<Term> getFeatureTypes(String datasource) throws Exception {
		//String query = String.format("%s select ?featuretype ?featuretypename where { graph <http://www.boinq.org/tracks> {<%s> track:contains ?featuretype. ?featuretype rdfs:label ?featuretypename}}", defaultPrefixes, datasource); 
		Query query = new Query();
		query.setQuerySelectType();
		query.setPrefixMapping(commonPrefixes);
		query.addResultVar("featuretype");
		query.addResultVar("featuretypename");
		Node datasourceClass = Node.createVariable("datasourceClass");
		Node featureType = Node.createVariable("featuretype");
		ElementTriplesBlock triples = new ElementTriplesBlock();
		triples.addTriple(new Triple(Node.createURI(datasource), RDF.type.asNode() , datasourceClass));
		triples.addTriple(new Triple(datasourceClass, OWL.someValuesFrom.asNode(), featureType));
		triples.addTriple(new Triple(datasourceClass, OWL.onProperty.asNode(), DatasourceVocabulary.provides));
		ElementTriplesBlock optionalTriplesBlock = new ElementTriplesBlock();
		optionalTriplesBlock.addTriple(new Triple(featureType, RDFS.label.asNode(), Node.createVariable("featuretypename")));
		ElementOptional optionalTriples = new ElementOptional(optionalTriplesBlock);
		ElementGroup group = new ElementGroup();
		group.addElement(triples);
		group.addElement(optionalTriples);
		ElementNamedGraph graphQuery = new ElementNamedGraph(DatasourceVocabulary.datasourceGraph,group);
		query.setQueryPattern(graphQuery);
		log.debug("Querying for feature types. Query: \n"+query.toString(Syntax.syntaxSPARQL));
		SPARQLResultSet result = sparqlClient.query(query);
		List<Term> resultList = new LinkedList<Term>();
		for (Map<String,String> record: result.getRecords()) {
			resultList.add(new Term(record.get("featuretype"),record.get("featuretypename")));
		}
		return resultList;	
	}
	
	public List<FieldConfig> getFields(String featuretype) throws Exception {
		Map<String,FieldConfig> fieldmap = new HashMap<String, FieldConfig>();
		return getFields(featuretype, fieldmap);
	}


	public List<FieldConfig> getFields(String featuretype, Map<String, FieldConfig> fieldMap) throws Exception {
		Query query = new Query();
		query.setQuerySelectType();
		query.setPrefixMapping(commonPrefixes);
		Node supertype = Node.createVariable("supertype");
		Node range = Node.createVariable("range");
		Node property = Node.createVariable("property");
		Node propertyName = Node.createVariable("propertyName");
		Node motherTerm = Node.createVariable("motherTerm");
		Node targetGraph = Node.createVariable("targetGraph");
		Node targetEndpoint = Node.createVariable("targetEndpoint");
		Node targetFilter = Node.createVariable("targetFilter");
		query.addResultVar(property);
		query.addResultVar(propertyName);
		query.addResultVar(range);
		query.addResultVar(motherTerm);
		query.addResultVar(targetGraph);
		query.addResultVar(targetEndpoint);
		query.addResultVar(targetFilter);
		ElementPathBlock supertypeProperty = new ElementPathBlock();
		Path sub_or_equiv = new P_Alt(new P_Link(RDFS.subClassOf.asNode()), new P_Link(OWL.equivalentClass.asNode()));
		Path substar = new P_ZeroOrMore1(sub_or_equiv);
		supertypeProperty.addTriplePath(new TriplePath(Node.createURI(featuretype), substar, supertype));
		Path some_or_all = new P_Alt(new P_Link(OWL.someValuesFrom.asNode()), new P_Link(OWL.allValuesFrom.asNode()));
		supertypeProperty.addTriple(new Triple(supertype, OWL.onProperty.asNode(), property));
		ElementPathBlock valuesFrom = new ElementPathBlock();
		valuesFrom.addTriplePath(new TriplePath(supertype, some_or_all, range));
		ElementUnion union = new ElementUnion();
		union.addElement(valuesFrom);
		ElementTriplesBlock cardinality = new ElementTriplesBlock();
		cardinality.addTriple(new Triple(supertype, Node.createURI("http://www.w3.org/2002/07/owl#onClass"), range));
		cardinality.addTriple(new Triple(supertype, Node.createURI("http://www.w3.org/2002/07/owl#qualifiedCardinality"), Node.createVariable("cardinality")));
		union.addElement(cardinality);
		ElementTriplesBlock label = new ElementTriplesBlock();
		label.addTriple(new Triple(property, RDFS.label.asNode(), propertyName));
		ElementOptional optionalLabel = new ElementOptional(label);
		ElementTriplesBlock termInfo = new ElementTriplesBlock();
		termInfo.addTriple(new Triple(property, DatasourceVocabulary.hasGraph, targetGraph));
		termInfo.addTriple(new Triple(property, DatasourceVocabulary.hasEndpoint, targetEndpoint));
		ElementOptional optionalTermInfo = new ElementOptional(termInfo);
		ElementGroup elements = new ElementGroup();
		elements.addElement(supertypeProperty);
		elements.addElement(union);
		elements.addElement(optionalLabel);
		elements.addElement(optionalTermInfo);
		ElementNamedGraph graphElement = new ElementNamedGraph(DatasourceVocabulary.datasourceGraph, elements);
		query.setQueryPattern(graphElement);
		log.debug("Querying for fields. Query: \n"+query.toString(Syntax.syntaxSPARQL));
		List<Map<String,RDFNode>> result = sparqlClient.queryForListOfNodeMaps(query.toString(Syntax.syntaxSPARQL));
		List<FieldConfig> fields = new LinkedList<FieldConfig>();
		for (Map<String,RDFNode> record : result) {
			RDFNode propertyURI = record.get("property");
			RDFNode propertyLabel = record.get("propertyName");
			RDFNode propertyRange = record.get("range");
			String propertyString = OntoTools.getNiceString(propertyLabel);
			if (propertyString == null) propertyString = propertyURI.asNode().getLocalName();
			FieldConfig config = null;
			if (fieldMap.containsKey(propertyURI.toString())) {
				config = fieldMap.get(propertyURI.toString());
			} else {
				config = new FieldConfig();
				config.setIRI(propertyURI.toString());
				config.setRange(propertyRange.toString());
				config.setName(propertyString);
				if (equalsType(propertyRange,"string")) {
					config.setType(FieldType.STRING_TYPE);
				} else if (equalsType(propertyRange,"integer")) {
					config.setType(FieldType.INTEGER_TYPE);
				} else if (equalsType(propertyURI,"externalTerm") || equalsType(propertyURI,"externalGOTerm")) {
					//TODO: subclass of checking
					RDFNode motherTermNode = record.get("motherTerm");
					RDFNode targetGraphNode = record.get("targetGraph");
					RDFNode targetEndpointNode = record.get("targetEndpoint");
					RDFNode targetFilterNode = record.get("targetFilter");
					config.setType(FieldType.TERM_TYPE);
					if (motherTermNode != null) config.setMotherTerm(motherTermNode.toString());
					config.setTargetGraph(targetGraphNode.toString());
					if (targetEndpointNode != null) config.setTargetEndpoint(targetEndpointNode.toString());
					if (targetFilterNode != null) config.setTargetFilter(targetFilterNode.toString());	
				} else if (internalTerm(propertyRange)) {
					config.setType(FieldType.INTERNAL_TERM_TYPE);
					//TODO: remove: subfields are fetched by the child's editor
					config.setFields(getFields(propertyRange.toString(),fieldMap));
				} else {
					config.setType(FieldType.UNKNOWN_TYPE);
				}
				fieldMap.put(propertyURI.toString(), config);
			}
			fields.add(config);
		}
		return fields;
	}

	public List<Term> getProperties(String featuretype) {
		Query query = new Query();
		query.setQuerySelectType();
		query.setDistinct(true);
		query.setPrefixMapping(commonPrefixes);

		Node superclass = Node.createVariable("superclass");
		Node property = Node.createVariable("property");
		Node propertyName = Node.createVariable("propertyName");
		query.addResultVar(property);
		query.addResultVar(propertyName);
		ElementPathBlock supertypeMatch = new ElementPathBlock();
		Path sub_or_equiv = new P_Alt(new P_Link(RDFS.subClassOf.asNode()), new P_Link(OWL.equivalentClass.asNode()));
		Path substar = new P_ZeroOrMore1(sub_or_equiv);
		supertypeMatch.addTriplePath(new TriplePath(Node.createURI(featuretype), substar, superclass));
		// don't go all the way up 
		ExprList tooGeneral = new ExprList();
		tooGeneral.add(new NodeValueNode(OWL.Thing.asNode()));
		tooGeneral.add(new NodeValueNode(RDFS.Resource.asNode()));
		Expr restrictSuperclassExpression = new E_NotOneOf(new ExprVar(superclass), tooGeneral);
		ElementFilter restrictSuperclassFilter = new ElementFilter(restrictSuperclassExpression);
		//Expr notClassExpression = new E_NotEquals(new ExprVar(superclass), new NodeValueNode(OWL.Class.asNode()));
		//ElementFilter notClassFilter = new ElementFilter(notClassExpression);
		ElementUnion propertyMatch = new ElementUnion();
		ElementTriplesBlock restriction = new ElementTriplesBlock();
		restriction.addTriple(new Triple(superclass, RDF.type.asNode(), OWL.Restriction.asNode()));
		restriction.addTriple(new Triple(superclass, OWL.onProperty.asNode(), property));
		propertyMatch.addElement(restriction);
		ElementTriplesBlock domain = new ElementTriplesBlock();
		domain.addTriple(new Triple(property, RDFS.domain.asNode(), superclass));
		propertyMatch.addElement(domain);
		ElementTriplesBlock label = new ElementTriplesBlock();
		label.addTriple(new Triple(property, RDFS.label.asNode(), propertyName));
		ElementOptional optionalLabel = new ElementOptional(label);
		ElementGroup elements = new ElementGroup();
		elements.addElement(supertypeMatch);
		elements.addElement(restrictSuperclassFilter);
		elements.addElement(propertyMatch);
		elements.addElement(optionalLabel);
		ElementNamedGraph graphElement = new ElementNamedGraph(DatasourceVocabulary.datasourceGraph, elements);
		query.setQueryPattern(graphElement);
		log.debug("Querying for properties. Query: \n"+query.toString(Syntax.syntaxSPARQL));
		List<Term> resultTerms = new LinkedList<Term>();
		try {
			SPARQLResultSet result = sparqlClient.query(query);
			for (Map<String,String> record : result.getRecords()) {
				resultTerms.add(new Term(record.get("property"), record.get("propertyName")));
			}
		} catch (Exception e) {
			log.error("Could not launch query",e);
		}
		return resultTerms;
	}
	
	
	public List<Target> getTargets(String featureTypeString, String propertyString) {
		Query query = new Query();
		query.setQuerySelectType();
		query.setDistinct(true);
		Node anonymousRestriction = Node.createVariable("restriction");
		Node superrange = Node.createVariable("superrange");
		Node supertype = Node.createVariable("supertype");
		Node range = Node.createVariable("range");
		Node label = Node.createVariable("rangeLabel");
		Node featureType = Node.createURI(featureTypeString);
		Node property = Node.createURI(propertyString);
		Node targetGraph = Node.createVariable("targetGraph");
		Node targetEndpoint = Node.createVariable("targetEndpoint");
		query.addResultVar(range);
		query.addResultVar(label);
		query.addResultVar(targetGraph);
		query.addResultVar(targetEndpoint);

		Path some_or_all = new P_Alt(new P_Link(OWL.someValuesFrom.asNode()), new P_Link(OWL.allValuesFrom.asNode()));
		ElementPathBlock valuesFromMatch = new ElementPathBlock();
		valuesFromMatch.addTriple(new Triple(featureType, RDFS.subClassOf.asNode(), anonymousRestriction));
		valuesFromMatch.addTriple(new Triple(anonymousRestriction, RDF.type.asNode(), OWL.Restriction.asNode()));
		valuesFromMatch.addTriple(new Triple(anonymousRestriction, OWL.onProperty.asNode(), property));
		valuesFromMatch.addTriplePath(new TriplePath(anonymousRestriction, some_or_all, superrange));
		Path sub_or_equiv = new P_Alt(new P_Link(RDFS.subClassOf.asNode()), new P_Link(OWL.equivalentClass.asNode()));
		Path substar = new P_ZeroOrMore1(sub_or_equiv);
		
		ElementPathBlock rangeMatch = new ElementPathBlock();
		rangeMatch.addTriplePath(new TriplePath(featureType, substar, supertype));
		rangeMatch.addTriple(new Triple(property, RDFS.domain.asNode(), supertype));
		rangeMatch.addTriple(new Triple(property, RDFS.range.asNode(), superrange));
				
		ElementUnion union = new ElementUnion();
		union.addElement(valuesFromMatch);
		union.addElement(rangeMatch);
		
		ElementPathBlock subRangeMatch = new ElementPathBlock();
		subRangeMatch.addTriplePath(new TriplePath(range, substar, superrange));
		
		ExprList tooGeneral = new ExprList();
		tooGeneral.add(new NodeValueNode(OWL.Thing.asNode()));
		tooGeneral.add(new NodeValueNode(RDFS.Resource.asNode()));
		Expr superTypeNotBound = new E_LogicalNot(new E_Bound(new ExprVar(supertype)));
		Expr superTypeNotBlank = new E_LogicalNot(new E_IsBlank(new ExprVar(supertype)));
		Expr superTypeNotTooGeneral = new E_NotOneOf(new ExprVar(supertype), tooGeneral);
		ElementFilter restrictSuperclassFilter = new ElementFilter(new E_LogicalOr(superTypeNotBound, new E_LogicalAnd(superTypeNotBlank, superTypeNotTooGeneral)));
		
		ElementTriplesBlock labelMatch = new ElementTriplesBlock();
		labelMatch.addTriple(new Triple(range,RDFS.label.asNode(),label));
		ElementOptional optionalLabel = new ElementOptional(labelMatch);
		
		ElementTriplesBlock externalTermEndpoint = new ElementTriplesBlock();
		externalTermEndpoint.addTriple(new Triple(property, RDFS.range.asNode(), DatasourceVocabulary.externalTerm));
		externalTermEndpoint.addTriple(new Triple(property, DatasourceVocabulary.targetEndpoint, targetEndpoint));
		ElementOptional optionalExternalTermEndpoint= new ElementOptional(externalTermEndpoint);

		ElementTriplesBlock externalTermGraph = new ElementTriplesBlock();
		externalTermGraph.addTriple(new Triple(property, RDFS.range.asNode(), DatasourceVocabulary.externalTerm));
		externalTermGraph.addTriple(new Triple(property, DatasourceVocabulary.targetGraph, targetGraph));
		ElementOptional optionalExternalTermGraph = new ElementOptional(externalTermGraph);
		
		ElementGroup elements = new ElementGroup();
		elements.addElement(union);
		elements.addElement(subRangeMatch);
		elements.addElement(optionalLabel);
		elements.addElement(optionalExternalTermGraph);
		elements.addElement(optionalExternalTermEndpoint);
		elements.addElement(restrictSuperclassFilter);
		ElementNamedGraph graphElement = new ElementNamedGraph(DatasourceVocabulary.datasourceGraph, elements);
		
		
		
		query.setQueryPattern(graphElement);
		List<Target> targets = new LinkedList<Target>();
		try {
			RawSPARQLResultSet result = sparqlClient.rawQuery(query);
			if (result.getRecords() == null || result.getRecords().size() == 0) return null;
			for (Map<String,RDFNode> record : result.getRecords()) {
				Target target = new Target();
				RDFNode rangeNode = record.get("range");
				target.setIRI(rangeNode.toString());
				String nameSpace = rangeNode.asResource().getNameSpace();
				String localName = rangeNode.asResource().getLocalName();
				if ("http://www.w3.org/2001/XMLSchema#".equals(nameSpace)) {
					// Literal
					if (stringTypes.contains(localName)) {
						target.setType(FieldType.STRING_TYPE);
					} else if (integerTypes.contains(localName)) {
						target.setType(FieldType.INTEGER_TYPE);
					} else if (decimalTypes.contains(localName)) {
						target.setType(FieldType.DECIMAL_TYPE);
					} else if (decimalTypes.contains(booleanTypes)) {
						target.setType(FieldType.BOOLEAN_TYPE);
					}
				} else if (DatasourceVocabulary.externalTerm.toString().equals(rangeNode.toString())) {
					target.setType(FieldType.TERM_TYPE);
					RDFNode targetEndpointResultNode = record.get("targetEndpoint");
					RDFNode targetGraphResultNode = record.get("targetGraph");
					RDFNode targetFilterResultNode = record.get("targetFilter");
					target.setTargetEndpoint(targetEndpointResultNode!=null?targetEndpointResultNode.toString():null);
//					target.setTargetFilter(targetFilterResultNode!=null?targetFilterResultNode.toString():null);
					target.setTargetGraph(targetGraphResultNode!=null?targetGraphResultNode.toString():null);
				} else if (internalNode(rangeNode)) {
					target.setType(FieldType.INTERNAL_TERM_TYPE);
				}
				targets.add(target);
			}
		} catch (Exception e) {
			log.error("Could not launch query",e);
		}
		return targets;

	}
	
	protected List<String> integerTypes = Arrays.asList("int","integer","long");
	protected List<String> decimalTypes = Arrays.asList("float","double","decimal");
	protected List<String> stringTypes = Arrays.asList("string");
	protected List<String> booleanTypes = Arrays.asList("boolean");
	
	
	
//	protected void fetchTermProperties(RDFNode node, Target target) throws Exception {
//		Query query = new Query();
//		query.setQuerySelectType();
//		Node targetGraph = Node.createVariable("targetGraph");
//		Node targetEndpoint = Node.createVariable("targetEndpoint");
//		query.addResultVar(targetGraph);
//		query.addResultVar(targetEndpoint);
//		ElementGroup termInfo = new ElementGroup();
//		ElementTriplesBlock externalTermEndpoint = new ElementTriplesBlock();
//		externalTermEndpoint.addTriple(new Triple(node.asNode(), RDFS.range.asNode(), DatasourceVocabulary.externalTerm));
//		externalTermEndpoint.addTriple(new Triple(node.asNode(), DatasourceVocabulary.targetEndpoint, targetEndpoint));
//		ElementOptional optionalExternalTermEndpoint= new ElementOptional(externalTermEndpoint);
//		ElementTriplesBlock externalTermGraph = new ElementTriplesBlock();
//		externalTermGraph.addTriple(new Triple(node.asNode(), RDFS.range.asNode(), DatasourceVocabulary.externalTerm));
//		externalTermGraph.addTriple(new Triple(node.asNode(), DatasourceVocabulary.targetGraph, targetGraph));
//		ElementOptional optionalExternalTermGraph = new ElementOptional(externalTermGraph);
//		termInfo.addElement(optionalExternalTermEndpoint);
//		termInfo.addElement(optionalExternalTermGraph);
//		query.setQueryPattern(termInfo);
//		SPARQLResultSet result = sparqlClient.query(query);
//		if (result.getRecords() == null || result.getRecords().size() != 1) {
//			throw new Exception("Cannot get properties for external term "+node.toString());
//		}
//		Map<String,String> record = result.getRecords().get(0);
//		target.setTargetEndpoint(record.get("targetEndpoint"));
//		target.setTargetGraph(record.get(targetGraph));
//	}
	
	protected Boolean internalNode(RDFNode node) throws Exception {
		Query query = new Query();
		query.setQuerySelectType();
		Expr countDistinct = new ExprAggregator(null, new AggCountDistinct());
		query.addResultVar(countDistinct);
		ElementTriplesBlock mainElement = new ElementTriplesBlock();
		Node p = Node.createVariable("p");
		Node o = Node.createVariable("o");
		mainElement.addTriple(new Triple(node.asNode(), p, o));
		query.setQueryPattern(mainElement);
		RawSPARQLResultSet res = sparqlClient.rawQuery(query);
		if (res.getRecords() == null || res.getRecords().size() != 1 ) {
			throw new Exception("Could not check for internal node "+node.toString());
		}
		Map<String,RDFNode> result = res.getRecords().get(0);
		RDFNode countNode = result.get(res.getVariableNames().get(0));
		if (!countNode.isLiteral()) {
			throw new Exception("Count not literal while checking for internal node "+node.toString());
		}
		Literal countLiteral = countNode.asLiteral();
		Integer count = Integer.parseInt(countLiteral.getValue().toString());
		return count >= 1;
	}
	
	
//	protected Boolean externalTerm(RDFNode node) throws Exception {
//		Query query = new Query();
//		query.setQuerySelectType();		
//		Expr countDistinct = new ExprAggregator(null, new AggCountDistinct());
//		query.addResultVar(countDistinct);
//		ElementPathBlock supertypeMatch = new ElementPathBlock();
//		Path sub_or_equiv = new P_Alt(new P_Link(RDFS.subClassOf.asNode()), new P_Link(OWL.equivalentClass.asNode()));
//		Path substar = new P_ZeroOrMore1(sub_or_equiv);
//		supertypeMatch.addTriplePath(new TriplePath(node.asNode(), substar, DatasourceVocabulary.externalTerm));
//		query.setQueryPattern(supertypeMatch);
//		RawSPARQLResultSet res = sparqlClient.rawQuery(query);
//		if (res.getRecords() == null || res.getRecords().size() != 1 ) {
//			throw new Exception("Could not check for internal node "+node.toString());
//		}
//		Map<String,RDFNode> result = res.getRecords().get(0);
//		RDFNode countNode = result.get(res.getVariableNames().get(0));
//		if (!countNode.isLiteral()) {
//			throw new Exception("Count not literal while checking for internal node "+node.toString());
//		}
//		Literal countLiteral = countNode.asLiteral();
//		Integer count = Integer.parseInt(countLiteral.getValue().toString());
//		return count >= 1;
//	}
	
	public List<Term> getRanges(String featureTypeString, String propertyString) {
		Query query = new Query();
		query.setQuerySelectType();
		query.setDistinct(true);
		Node anonymousRestriction = Node.createVariable("restriction");
		Node superrange = Node.createVariable("superrange");
		Node range = Node.createVariable("range");
		Node label = Node.createVariable("rangeLabel");
		Node featureType = Node.createURI(featureTypeString);
		Node property = Node.createURI(propertyString);
		query.addResultVar(superrange);
		query.addResultVar(label);

		Path some_or_all = new P_Alt(new P_Link(OWL.someValuesFrom.asNode()), new P_Link(OWL.allValuesFrom.asNode()));
		ElementPathBlock valuesFromMatch = new ElementPathBlock();
		valuesFromMatch.addTriple(new Triple(featureType, RDFS.subClassOf.asNode(), anonymousRestriction));
		valuesFromMatch.addTriple(new Triple(anonymousRestriction, RDF.type.asNode(), OWL.Restriction.asNode()));
		valuesFromMatch.addTriple(new Triple(anonymousRestriction, OWL.onProperty.asNode(), property));
		valuesFromMatch.addTriplePath(new TriplePath(anonymousRestriction, some_or_all, superrange));
		ElementTriplesBlock rangeMatch = new ElementTriplesBlock();
		rangeMatch.addTriple(new Triple(property, RDFS.range.asNode(), superrange));
		ElementUnion union = new ElementUnion();
		union.addElement(valuesFromMatch);
		union.addElement(rangeMatch);
		ElementPathBlock subRangeMatch = new ElementPathBlock();
		Path sub_or_equiv = new P_Alt(new P_Link(RDFS.subClassOf.asNode()), new P_Link(OWL.equivalentClass.asNode()));
		Path substar = new P_ZeroOrMore1(sub_or_equiv);
		subRangeMatch.addTriplePath(new TriplePath(range, substar, superrange));
		ElementTriplesBlock labelMatch = new ElementTriplesBlock();
		labelMatch.addTriple(new Triple(property,RDFS.label.asNode(),label));
		ElementOptional optionalLabel = new ElementOptional(labelMatch);
		ElementGroup elements = new ElementGroup();
		elements.addElement(union);
		elements.addElement(subRangeMatch);
		elements.addElement(optionalLabel);
		ElementNamedGraph graphElement = new ElementNamedGraph(DatasourceVocabulary.datasourceGraph, elements);
		query.setQueryPattern(graphElement);
		List<Term> resultTerms = new LinkedList<Term>();
		try {
			SPARQLResultSet result = sparqlClient.query(query);
			for (Map<String,String> record : result.getRecords()) {
				resultTerms.add(new Term(record.get("range"), record.get("rangeLabel")));
			}
		} catch (Exception e) {
			log.error("Could not launch query",e);
		}
		return resultTerms;

	}
	
	
	protected boolean internalTerm(RDFNode propertyrange) {
		
		//	TODO: write query to see if the node appears as a subject
		//  or see if it is part of the imported ontologies
		
		String baseURI = propertyrange.asNode().getNameSpace();
		String localName = propertyrange.asNode().getLocalName();
		if (DatasourceVocabulary.baseURI.equals(baseURI) || DatasourceVocabulary.ensemblURI.equals(baseURI) || DatasourceVocabulary.faldoURI.equals(baseURI) ) {
			return !"externalTerm".equals(localName);
		}
		return false;
	}
	
	protected boolean equalsType(RDFNode propertyRange, String type) {
		return NodeUtils.compareRDFTerms(propertyRange.asNode(), Node.createURI(CommonVocabulary.xmlSchemaURI+type)) == 0 
				|| NodeUtils.compareRDFTerms(propertyRange.asNode(), Node.createURI(DatasourceVocabulary.baseURI + type)) == 0;
	}
	
	
	public SPARQLClient getSparqlClient() {
		return sparqlClient;
	}

	public void setSparqlClient(SPARQLClient sparqlClient) {
		this.sparqlClient = sparqlClient;
	}
	
}
