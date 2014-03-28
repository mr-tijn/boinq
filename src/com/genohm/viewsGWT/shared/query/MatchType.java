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
import com.genohm.viewsGWT.shared.data.FeatureDatasource;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;

public class MatchType implements Match {

	//protected FeatureDatasource subjectDatasource;
	protected String sourceGraph;
	protected String typeIRI;

	public MatchType() {}
	
	public MatchType(String typeIRI) {
		setTypeIRI(typeIRI);
	}
	
	@Override
	public CriterionTreeNode toTreeNode() {
		// TODO Auto-generated method stub
		return null;
	}


//	public FeatureDatasource getSubjectDatasource() {
//		return subjectDatasource;
//	}
//
//	public void setSubjectDatasource(FeatureDatasource subjectDatasource) {
//		this.subjectDatasource = subjectDatasource;
//	}

	public String getSourceGraph() {
		return sourceGraph;
	}

	public void setSourceGraph(String sourceGraph) {
		this.sourceGraph = sourceGraph;
	}

	public String getTypeIRI() {
		return typeIRI;
	}

	public void setTypeIRI(String typeIRI) {
		this.typeIRI = typeIRI;
	}

	@Override
	public MatchRecord createRecord() {
		return new MatchRecord(this);
	}

	@Override
	public String toString() {
		return "MatchType " + getTypeIRI();
	}
	@Override
	public void acceptGenerator(SPARQLGenerator generator, String subjectIdentifier) {
		generator.visitMatch(this, subjectIdentifier);
	}
//	@Override
//	public String generate(SPARQLGenerator generator, String subjectIdentifier) {
//		return generator.generate(this, subjectIdentifier);
//	}
//
//	@Override
//	public Element generateElement(ARQGenerator generator, String subjectIdentifier) {
//		return generator.generateElement(this, subjectIdentifier);
//	}
//
//	@Override
//	public ElementGroup addElements(ARQGenerator generator, String subjectIdentifier, ElementGroup parent) {
//		return generator.addElements(this, subjectIdentifier, parent);
//	}

}
