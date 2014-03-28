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
package com.genohm.viewsGWT.shared.query;

import com.genohm.viewsGWT.client.records.MatchRecord;
import com.genohm.viewsGWT.server.query.ARQGenerator;
import com.genohm.viewsGWT.shared.fieldconfig.FieldType;
import com.genohm.viewsGWT.shared.fieldconfig.Target;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;

public class MatchField implements Match {

	protected String fieldIRI;
	protected FieldType type;
	protected String valueExpression;
	protected String sourceGraph; //FIXME: maybe not needed - this is metainformation for the term selection
	protected String typeIRI; //FIXMI: also
	protected MatchField subMatch;
	
	public MatchField() {
	}
	
	public MatchField(String fieldIRI, String valueExpression, String sourceGraph, String typeIRI) {
		setFieldIRI(fieldIRI);
		setValueExpression(valueExpression);
		setSourceGraph(sourceGraph);
		setTypeIRI(typeIRI);
	}
	

	@Override
	public CriterionTreeNode toTreeNode() {
		CriterionTreeNode treeNode = new CriterionTreeNode("MatchField", false, "MatchField");
		return null;
	}

	@Override
	public String getSourceGraph() {
		return this.sourceGraph;
	}

	@Override
	public void setSourceGraph(String sourceGraph) {
		this.sourceGraph = sourceGraph;
	}

	public String getFieldIRI() {
		return fieldIRI;
	}

	public void setFieldIRI(String fieldIRI) {
		this.fieldIRI = fieldIRI;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	public String getValueExpression() {
		return valueExpression;
	}

	public void setValueExpression(String valueExpression) {
		this.valueExpression = valueExpression;
	}

	public String getTypeIRI() {
		return typeIRI;
	}

	public void setTypeIRI(String typeIRI) {
		this.typeIRI = typeIRI;
	}

	public MatchField getSubMatch() {
		return subMatch;
	}

	public void setSubMatch(MatchField subMatch) {
		this.subMatch = subMatch;
	}

	@Override
	public MatchRecord createRecord() {
		return new MatchRecord(this);
	}

	@Override
	public String toString() {
		return "MatchField(" + (getFieldIRI()==null?"":getFieldIRI()) + (getValueExpression()==null?"":getValueExpression()) + ")";
	}
	@Override
	public void acceptGenerator(SPARQLGenerator generator, String subjectIdentifier) {
		generator.visitMatch(this, subjectIdentifier);
	}
//	@Override
//	public String generate(SPARQLGenerator generator, String subjectIdentifier) {
//		return generator.generate(this, subjectIdentifier);
//	}
//	@Override
//	public Element generateElement(ARQGenerator generator, String subjectIdentifier) {
//		return generator.generateElement(this, subjectIdentifier);
//	}
//	@Override
//	public ElementGroup addElements(ARQGenerator generator, String subjectIdentifier, ElementGroup parent) {
//		return generator.addElements(this, subjectIdentifier, parent);
//	}

}
