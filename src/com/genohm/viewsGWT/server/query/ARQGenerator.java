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
package com.genohm.viewsGWT.server.query;

import java.util.LinkedList;
import java.util.List;

import com.genohm.viewsGWT.server.ontologyclient.CommonVocabulary;
import com.genohm.viewsGWT.server.ontologyclient.DatasourceVocabulary;
import com.genohm.viewsGWT.server.ontologyclient.FaldoVocabulary;
import com.genohm.viewsGWT.server.ontologyclient.TrackVocabulary;
import com.genohm.viewsGWT.shared.query.Match;
import com.genohm.viewsGWT.shared.query.MatchAll;
import com.genohm.viewsGWT.shared.query.MatchAny;
import com.genohm.viewsGWT.shared.query.MatchField;
import com.genohm.viewsGWT.shared.query.MatchLocation;
import com.genohm.viewsGWT.shared.query.MatchType;
import com.genohm.viewsGWT.shared.query.Overlap;
import com.genohm.viewsGWT.shared.query.SPARQLGenerator;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.expr.E_Equals;
import com.hp.hpl.jena.sparql.expr.E_GreaterThanOrEqual;
import com.hp.hpl.jena.sparql.expr.E_IRI;
import com.hp.hpl.jena.sparql.expr.E_LessThanOrEqual;
import com.hp.hpl.jena.sparql.expr.E_OneOf;
import com.hp.hpl.jena.sparql.expr.E_Regex;
import com.hp.hpl.jena.sparql.expr.E_Str;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueNode;
import com.hp.hpl.jena.sparql.path.P_Alt;
import com.hp.hpl.jena.sparql.path.P_Link;
import com.hp.hpl.jena.sparql.path.P_OneOrMoreN;
import com.hp.hpl.jena.sparql.path.P_Seq;
import com.hp.hpl.jena.sparql.path.P_ZeroOrMoreN;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.sparql.util.ExprUtils;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ARQGenerator implements SPARQLGenerator {
	protected int featureCounter = 1;
	protected int entityCounter = 1;
	protected ElementTriplesBlock triples = new ElementTriplesBlock();
	protected List<ElementFilter> filters = new LinkedList<ElementFilter>();
	protected List<Element> subElements = new LinkedList<Element>();
	
	public ARQGenerator() {
	}

	public ElementGroup getElement() {
		//return parent;
		ElementGroup result = new ElementGroup();
		result.addElement(triples);
		for (Element subElement: subElements) {
			result.addElement(subElement);
		}
		for (ElementFilter filter: filters) {
			result.addElement(filter);
		}
		return result;
	}
	
	protected String quote(String input) {
		if (input.startsWith("'")) return input;
		return String.format("'%s'", input);
	}

	protected String getFeatureIdentifier() {
		return "feature" + featureCounter++;
	}

	protected String getEntityIdentifier() {
		return "entity" + entityCounter++;
	}

	public void visitMatch(MatchField match, String subjectIdentifier) {
		String objectIdentifier = getEntityIdentifier();
		if (match.getSubMatch() != null) {
			triples.addTriple(new Triple(Node.createVariable(subjectIdentifier), Node.createURI(match.getFieldIRI()), Node.createVariable(objectIdentifier)));
			visitMatch(match.getSubMatch(), objectIdentifier);
		} else {
			switch (match.getType()) {
			case TERM_TYPE:
				// split valueExpression in separate terms
				triples.addTriple(new Triple(Node.createVariable(subjectIdentifier), Node.createURI(match.getFieldIRI()), Node.createVariable(objectIdentifier)));
				String[] targetURIs = match.getValueExpression().split(",");
				ExprList targetExpressions = new ExprList();
				for (String targetURI : targetURIs) {
					targetExpressions.add(new E_IRI(new NodeValueNode(Node.createURI(targetURI))));
				}
				E_OneOf oneOf = new E_OneOf(new E_IRI(new ExprVar(Node.createVariable(objectIdentifier))), targetExpressions);
				filters.add(new ElementFilter(oneOf));
				break;
			case STRING_TYPE:
			//TODO: sane default
			default:
				String targetString = match.getValueExpression();
				triples.addTriple(new Triple(Node.createVariable(subjectIdentifier), Node.createURI(match.getFieldIRI()), Node.createVariable(objectIdentifier)));
				E_Regex regex = new E_Regex(new E_Str(new ExprVar(Node.createVariable(objectIdentifier))),match.getValueExpression(), "i");
				filters.add(new ElementFilter(regex));
				break;
			case INTEGER_TYPE:
				triples.addTriple(new Triple(Node.createVariable(subjectIdentifier), Node.createURI(match.getFieldIRI()), Node.createVariable(objectIdentifier)));
				filters.add(new ElementFilter(ExprUtils.parse(match.getValueExpression().replace("val", objectIdentifier))));
				break;
			}
		}
	}

	
	public void visitMatch(MatchAll match, String subjectIdentifier) {
		for (Match subMatch : match.getSubMatches()) {
			subMatch.acceptGenerator(this, subjectIdentifier);
		}
	}

	public void visitMatch(MatchAny match, String subjectIdentifier) {
		ARQGenerator subGenerator = new ARQGenerator();
		for (Match subMatch : match.getSubMatches()) {
			subMatch.acceptGenerator(subGenerator, subjectIdentifier);
		}
		ElementGroup elements = subGenerator.getElement();
		ElementUnion union = new ElementUnion();
		for (Element subElement: elements.getElements()) {
			union.addElement(subElement);
		}
		subElements.add(union);
	}

	public void visitMatch(MatchLocation match, String subjectIdentifier) {
		Node subject = Node.createVariable(subjectIdentifier);
		Node begin = Node.createVariable(subjectIdentifier + "Begin");
		Node end = Node.createVariable(subjectIdentifier + "End");
		Node beginPos = Node.createVariable(subjectIdentifier + "BeginPos");
		Node endPos = Node.createVariable(subjectIdentifier + "EndPos");
		Node featureReference = Node.createVariable(subjectIdentifier + "Reference");
		Node featureReferenceName = Node.createVariable(subjectIdentifier + "ReferenceName");
		Boolean addTriples = !"feature".equals(subjectIdentifier);
		if (addTriples) {
			triples.addTriple(new Triple(subject, FaldoVocabulary.begin, begin));
			triples.addTriple(new Triple(subject, FaldoVocabulary.end, end));
		}
		if (match.getContig() != null) {
			if (addTriples) {
				triples.addTriple(new Triple(begin, FaldoVocabulary.reference,	featureReference));
				triples.addTriple(new Triple(featureReference, RDFS.label.asNode(), featureReferenceName));
			}
			filters.add(new ElementFilter(new E_Equals(new E_Str(new ExprVar(featureReferenceName)), ExprUtils.parse(quote(match.getContig())))));
		}
		if (match.getEnd() != null) {
			if (addTriples) triples.addTriple(new Triple(begin, FaldoVocabulary.position, beginPos));
			filters.add(new ElementFilter(new E_LessThanOrEqual(new ExprVar(beginPos), ExprUtils.parse(match.getEnd().toString()))));
		}
		if (match.getStart() != null) {
			if (addTriples) triples.addTriple(new Triple(end, FaldoVocabulary.position, endPos));
			filters.add(new ElementFilter(new E_GreaterThanOrEqual(new ExprVar(endPos), ExprUtils.parse(match.getStart().toString()))));
		}
		if (match.getMatchStrand()) {
			if (match.getStrand()) {
				triples.addTriple(new Triple(begin, RDF.type.asNode(),FaldoVocabulary.ForwardStrandPosition));
			} else {
				triples.addTriple(new Triple(begin, RDF.type.asNode(),FaldoVocabulary.ReverseStrandPosition));
			}
		}
	}

	public void visitMatch(MatchType match, String subjectIdentifier) {
		Node subject = Node.createVariable(subjectIdentifier);
		//TODO: check if this works in D2RQ: not using filter		
		//ElementPathBlock path = new ElementPathBlock();
		//path.addTriplePath(new TriplePath(subject, new P_Seq(new P_ZeroOrMoreN(new P_Link(RDFS.subClassOf.asNode())), new P_Link(RDF.type.asNode())), Node.createURI(match.getTypeIRI())));
		triples.addTriple(new Triple(subject, RDF.type.asNode(), Node.createURI(match.getTypeIRI())));
	}

	public String generateQuery(Match mainMatch) {
		Query mainQuery = new Query();
		mainQuery.setQuerySelectType();
		PrefixMapping prefixMap = new PrefixMappingImpl();
		prefixMap.setNsPrefix("rdf",CommonVocabulary.rdfBaseURI);
		prefixMap.setNsPrefix("rdfs", CommonVocabulary.rdfsBaseURI);
		prefixMap.setNsPrefix("ensembl", DatasourceVocabulary.ensemblURI);
		prefixMap.setNsPrefix("ds", DatasourceVocabulary.baseURI);
		prefixMap.setNsPrefix("prop", DatasourceVocabulary.propertyHolderBaseURI);
		prefixMap.setNsPrefix("obo", CommonVocabulary.oboBaseURI);
		prefixMap.setNsPrefix("xsd", CommonVocabulary.xmlSchemaURI);
		prefixMap.setNsPrefix("owl", CommonVocabulary.owlBaseURI);
		prefixMap.setNsPrefix("faldo", FaldoVocabulary.baseURI);
		mainQuery.setPrefixMapping(prefixMap);
		Node featureId = Node.createVariable("featureId");
		Node featureBeginPos = Node.createVariable("featureBeginPos");
		Node featureEndPos = Node.createVariable("featureEndPos");
		Node featureReference = Node.createVariable("featureReference");
		Node featureReferenceName = Node.createVariable("featureReferenceName");
		Node featurePositionType = Node.createVariable("featurePositionType");
		
		Node feature = Node.createVariable("feature");
		
		mainQuery.addResultVar(featureId);
		mainQuery.addResultVar(featureBeginPos);
		mainQuery.addResultVar(featureEndPos);
		mainQuery.addResultVar(featureReferenceName);
		mainQuery.addResultVar("featureStrand", new E_Equals(new ExprVar(featurePositionType), ExprUtils.nodeToExpr(FaldoVocabulary.ForwardStrandPosition)));
		
		ElementGroup mainSelect = new ElementGroup();

		triples.addTriple(new Triple(feature, RDFS.label.asNode(), featureId));
		Node featureBegin = Node.createVariable("featureBegin");
		triples.addTriple(new Triple(feature, FaldoVocabulary.begin, featureBegin));
		triples.addTriple(new Triple(featureBegin, FaldoVocabulary.position, featureBeginPos));
		Node featureEnd = Node.createVariable("featureEnd");
		triples.addTriple(new Triple(feature, FaldoVocabulary.end, featureEnd));
		triples.addTriple(new Triple(featureEnd, FaldoVocabulary.position, featureEndPos));
		triples.addTriple(new Triple(featureBegin, FaldoVocabulary.reference, featureReference));
		triples.addTriple(new Triple(featureReference, RDFS.label.asNode(), featureReferenceName));
		triples.addTriple(new Triple(featureBegin, RDF.type.asNode(), featurePositionType));
	
		
		mainMatch.acceptGenerator(this, "feature");
		mainSelect.addElement(triples);
		for (Element subElement: subElements) {
			mainSelect.addElement(subElement);
		}
		for (ElementFilter filter: filters) {
			mainSelect.addElement(filter);
		}
		
		mainQuery.setQueryPattern(mainSelect);
		mainQuery.addOrderBy(featureBeginPos, 1); //asc
		
		
		return mainQuery.toString(Syntax.syntaxSPARQL_11);
	}

}
