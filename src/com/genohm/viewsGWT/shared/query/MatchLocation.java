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
import com.smartgwt.client.widgets.tree.TreeNode;

public class MatchLocation implements Match {
	
	//protected FeatureDatasource subjectDatasource;
	protected String sourceGraph;

	protected Long start;
	protected Long end;
	protected String contig;
	protected String assembly;
	protected Boolean strand;
	protected Boolean matchStrand = false;

	public MatchLocation() {
	}

	public MatchLocation(Long start, Long end, String contig, Boolean strand, Boolean matchStrand) {
		super();
		setStart(start);
		setEnd(end);
		setContig(contig);
		setStrand(strand);
		setMatchStrand(matchStrand);
	}
	
	public MatchLocation(TreeNode treeNode) {
		setStart(Long.parseLong(treeNode.getAttributeAsRecord("parameters").getAttributeAsString("start")));
		setEnd(Long.parseLong(treeNode.getAttributeAsRecord("parameters").getAttributeAsString("end")));
		// strand should be three possibilities: forward, backward, don't care
		setStrand(treeNode.getAttributeAsRecord("parameters").getAttributeAsBoolean("strand"));
		
		setContig(treeNode.getAttributeAsRecord("parameters").getAttributeAsString("contig"));
		
	}
	
	public CriterionTreeNode toTreeNode() {
		CriterionTreeNode ctr = new CriterionTreeNode("LocationMatch", false, CriterionTreeNode.CRITERION_TYPE_LOCATION);
		ctr.getParameters().setAttribute("start", start);
		ctr.getParameters().setAttribute("end", end);
		ctr.getParameters().setAttribute("contig", contig);
		ctr.getParameters().setAttribute("strand", strand);
		return ctr;
	}

	public String getSourceGraph() {
		return sourceGraph;
	}

	public void setSourceGraph(String sourceGraph) {
		this.sourceGraph = sourceGraph;
	}

	public Long getStart() {
		return start;
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public Long getEnd() {
		return end;
	}

	public void setEnd(Long end) {
		this.end = end;
	}

	public String getContig() {
		return contig;
	}

	public void setContig(String contig) {
		this.contig = contig;
	}

	public Boolean getStrand() {
		return strand;
	}

	public String getAssembly() {
		return assembly;
	}

	public void setAssembly(String assembly) {
		this.assembly = assembly;
	}

	public void setStrand(Boolean strand) {
		this.strand = strand;
	}

	public Boolean getMatchStrand() {
		return matchStrand;
	}

	public void setMatchStrand(Boolean matchStrand) {
		this.matchStrand = matchStrand;
	}

	@Override
	public MatchRecord createRecord() {
		return new MatchRecord(this);
	}
	
	@Override
	public String toString() {
		String contig = (getContig()==null?"":getContig());
		String start = (getStart() == null?"*":getStart().toString());
		String end = (getEnd()==null?"*":getEnd().toString());
		String strand = (getStrand()==null?"":(getStrand()?"(+)":"(-)"));
		return "MatchLocation " + contig + "[" + start + "-" + end +"] " + strand;
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

