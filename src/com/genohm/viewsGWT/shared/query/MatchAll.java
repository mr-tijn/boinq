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

import java.util.LinkedList;
import java.util.List;

import com.genohm.viewsGWT.client.records.MatchRecord;
import com.genohm.viewsGWT.server.query.ARQGenerator;
import com.genohm.viewsGWT.shared.data.FeatureDatasource;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.smartgwt.client.data.RecordList;

//@Entity
//@DiscriminatorValue("MATCH_ALL")
public class MatchAll implements Match {
	
	protected List<Match> subMatches = new LinkedList<Match>();
	//protected FeatureDatasource subjectDatasource;
	protected String sourceGraph;

	public MatchAll() {}
	public MatchAll(Match... subMatches) {
		for (Match subMatch: subMatches) {
			this.subMatches.add(subMatch);
		}
	}

	public void addMatch(Match match) {
		this.subMatches.add(match);
	}
	
	@Override
	public CriterionTreeNode toTreeNode() {
		CriterionTreeNode ctr = new CriterionTreeNode("MatchAll", true, CriterionTreeNode.CRITERION_TYPE_MULTI);
		RecordList childRecords = new RecordList();
		for (Match subMatch: subMatches) {
			childRecords.add(subMatch.toTreeNode());
		}
		ctr.setAttribute("children", childRecords);
		return ctr;
	}

	public List<Match> getSubMatches() {
		return subMatches;
	}
	public void setSubMatches(List<Match> subMatches) {
		this.subMatches = subMatches;
	}
	public String getSourceGraph() {
		return sourceGraph;
	}
	public void setSourceGraph(String sourceGraph) {
		this.sourceGraph = sourceGraph;
	}
	@Override
	public MatchRecord createRecord() {
		return new MatchRecord(this);
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
