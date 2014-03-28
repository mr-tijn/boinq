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
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;

public class MatchAny implements Match {

	protected List<Match> subMatches = new LinkedList<Match>();

	public MatchAny() {}
	public MatchAny(Match... subMatches) {
		for (Match subMatch: subMatches) {
			this.subMatches.add(subMatch);
		}
	}
	
	public void addMatch(Match match) {
		this.subMatches.add(match);
	}
	
	@Override
	public MatchRecord createRecord() {
		return new MatchRecord(this);
	}

	public List<Match> getSubMatches() {
		return subMatches;
	}

	public void setSubMatches(List<Match> subMatches) {
		this.subMatches = subMatches;
	}

	@Override
	public CriterionTreeNode toTreeNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSourceGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSourceGraph(String sourceGraph) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void acceptGenerator(SPARQLGenerator generator, String subjectIdentifier) {
		generator.visitMatch(this, subjectIdentifier);
	}

	
}
