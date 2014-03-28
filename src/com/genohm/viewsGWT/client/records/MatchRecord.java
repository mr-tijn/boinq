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
package com.genohm.viewsGWT.client.records;

import com.genohm.viewsGWT.shared.fieldconfig.FieldType;
import com.genohm.viewsGWT.shared.query.Match;
import com.genohm.viewsGWT.shared.query.MatchAll;
import com.genohm.viewsGWT.shared.query.MatchAny;
import com.genohm.viewsGWT.shared.query.MatchField;
import com.genohm.viewsGWT.shared.query.MatchLocation;
import com.genohm.viewsGWT.shared.query.MatchType;
import com.genohm.viewsGWT.shared.query.Overlap;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.widgets.tree.TreeNode;

public class MatchRecord extends TreeNode {
	
	public static final int MATCH_ALL = 0;	
	public static final int MATCH_ANY = 1;
	public static final int MATCH_FIELD = 2;
	public static final int MATCH_TYPE = 3;
	public static final int MATCH_LOCATION = 4;
	public static final int MATCH_OVERLAP = 5;
	
	public static final String[] matchTypes = {
		"Match all",
		"Match any",
		"Match field",
		"Match type",
		"Match location"
//		,"Match overlap"
	};
	
	public MatchRecord() {}
	
	public MatchRecord(int type) {
		setMatchType(type);
	}
	
	public MatchRecord(MatchField match) {
		setMatchType(MATCH_FIELD);
		if (match.getSubMatch() != null) {
			setParameter("subMatch",new MatchRecord(match.getSubMatch()));
		}
		setParameter("typeIRI", match.getTypeIRI());
		setParameter("fieldIRI", match.getFieldIRI());
		setParameter("sourceGraph", match.getSourceGraph());
		setParameter("valueExpression", match.getValueExpression());	
		setParameter("targetFieldType",match.getType().value);
	}
	
	public MatchRecord(MatchAll match) {
		setMatchType(MATCH_ALL);
		MatchRecord[] childMatches = new MatchRecord[match.getSubMatches().size()];
		int i = 0;
		for (Match subMatch: match.getSubMatches()) {
			childMatches[i++] = subMatch.createRecord();
		}
		this.setChildren(childMatches);
	}
	
	public MatchRecord(MatchAny match) {
		setMatchType(MATCH_ANY);
		MatchRecord[] childMatches = new MatchRecord[match.getSubMatches().size()];
		int i = 0;
		for (Match subMatch: match.getSubMatches()) {
			childMatches[i++] = subMatch.createRecord();
		}
		this.setChildren(childMatches);
	}

	public MatchRecord(MatchType match) {
		setMatchType(MATCH_TYPE);
		setParameter("typeIRI", match.getTypeIRI());
		setParameter("sourceGraph", match.getSourceGraph());
	}

	public MatchRecord(MatchLocation matchLocation) {
		setMatchType(MATCH_LOCATION);
		setParameter("start", matchLocation.getStart());
		setParameter("end", matchLocation.getEnd());
		setParameter("contig", matchLocation.getContig());
		setParameter("strand", matchLocation.getStrand());
		setParameter("matchStrand", matchLocation.getMatchStrand());
		setParameter("assembly", matchLocation.getAssembly());
	}

	public MatchRecord(Overlap overlap) {
		// TODO Auto-generated constructor stub
	}

	public Match getMatch() {
		Record parameters = getAttributeAsRecord("matchParameters");
		switch (getAttributeAsInt("matchClass")) {
		case MATCH_FIELD:
			MatchField matchField =  new MatchField();
			if (parameters != null) {
				matchField.setFieldIRI(parameters.getAttributeAsString("fieldIRI"));
				matchField.setSourceGraph(parameters.getAttributeAsString("sourceGraph"));
				matchField.setTypeIRI(parameters.getAttributeAsString("typeIRI"));
				matchField.setValueExpression(parameters.getAttributeAsString("valueExpression"));
				// TODO: working on it 4 oct 2013
				if (parameters.getAttributeAsInt("targetFieldType") != null) {
					matchField.setType(FieldType.parseInt(parameters.getAttributeAsInt("targetFieldType")));
				}
			}
			RecordList matchFieldChildren = getAttributeAsRecordList("children");
			if (matchFieldChildren != null && matchFieldChildren.getLength() > 0) {
				// should only contain a single child
				MatchRecord child = (MatchRecord) matchFieldChildren.get(0);
				matchField.setSubMatch((MatchField) child.getMatch());
			}
			return matchField;
		case MATCH_ALL:
			MatchAll matchAll = new MatchAll();
			RecordList matchAllChildren = getAttributeAsRecordList("children");
			for (int i = 0; i < matchAllChildren.getLength(); i++) {
				MatchRecord child = (MatchRecord) matchAllChildren.get(i);
				matchAll.addMatch(child.getMatch());
			}
			return matchAll;
		case MATCH_ANY:
			MatchAny matchAny = new MatchAny();
			RecordList matchAnyChildren = getAttributeAsRecordList("children");
			for (int i = 0; i < matchAnyChildren.getLength(); i++) {
				MatchRecord child = (MatchRecord) matchAnyChildren.get(i);
				matchAny.addMatch(child.getMatch());
			}
			return matchAny;
		case MATCH_TYPE:
			return new MatchType(parameters.getAttributeAsString("typeIRI"));
		case MATCH_LOCATION :
			String strand = parameters.getAttributeAsString("strand");
			Boolean strandVal = null;
			Boolean strandMatch = null;
			if ("Forward".equals(strand)) {
				strandMatch = true;
				strandVal = true;
			} else if ("Reverse".equals(strand)) {
				strandMatch = true;
				strandVal = false;
			} else if ("Any".equals(strand)) {
				strandMatch = false;
			}
			Long start = null;
			Long end = null;
			try {
				start = Long.parseLong(parameters.getAttributeAsString("start"));
			} catch (NumberFormatException e) {
				// swallow
			}
			try {
				end = Long.parseLong(parameters.getAttributeAsString("end"));
			} catch (NumberFormatException e) {
				// swallow
			}
			return new MatchLocation(start, end, parameters.getAttributeAsString("contig"), strandVal, strandMatch);
		default: 
			return null;
		}
	}
	
	public Boolean canHaveChildren() {
		switch (getMatchType()) {
		case MATCH_ALL:
		case MATCH_ANY:
			return true;
		default:
			return false;
		}
	}
	
	public void setMatchType(Integer matchType) {
		if (getMatchType() == null || matchType != getMatchType()) {
			setAttribute("matchClass", matchType);
			setAttribute("matchParameters", (Record) null);
		}
	}
	public Integer getMatchType() {
		return getAttributeAsInt("matchClass");
	}
	public void setParameter(String property, Object value) {
		getParameterRecord().setAttribute(property,value);
	}
	protected Record getParameterRecord() {
		Record parameterRecord = getAttributeAsRecord("matchParameters");
		if (parameterRecord == null) {
			parameterRecord = new Record();
			setAttribute("matchParameters", parameterRecord);
		}
		return parameterRecord;
	}
	
	@Override
	public String toString() {
		switch (getMatchType()) {
		case MATCH_ALL:
			return "Match all";
		case MATCH_ANY:
			return "Match any";
		default:
			if (getAttributeAsRecord("matchParameters") == null) return "Not configured"; 
			return getMatch().toString();
		}
	}
	
}
