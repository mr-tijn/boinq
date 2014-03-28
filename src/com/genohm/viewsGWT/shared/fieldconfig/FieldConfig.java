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
package com.genohm.viewsGWT.shared.fieldconfig;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FieldConfig implements IsSerializable {
	
	protected FieldType type;
	protected String name;
	protected String IRI;
	protected String targetGraph;
	protected String targetEndpoint;
	protected String targetFilter;
	protected String range;
	protected String motherTerm;
	protected List<FieldConfig> fields;
	
	
	public FieldType getType() {
		return type;
	}
	public void setType(FieldType type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIRI() {
		return IRI;
	}
	public void setIRI(String iRI) {
		IRI = iRI;
	}
	public String getTargetGraph() {
		return targetGraph;
	}
	public void setTargetGraph(String targetGraph) {
		this.targetGraph = targetGraph;
	}
	public String getTargetEndpoint() {
		return targetEndpoint;
	}
	public void setTargetEndpoint(String targetEndpoint) {
		this.targetEndpoint = targetEndpoint;
	}
	public String getTargetFilter() {
		return targetFilter;
	}
	public void setTargetFilter(String targetFilter) {
		this.targetFilter = targetFilter;
	}
	
	public String getRange() {
		return range;
	}
	public void setRange(String targetTerm) {
		this.range = targetTerm;
	}
	public String getMotherTerm() {
		return motherTerm;
	}
	public void setMotherTerm(String motherTerm) {
		this.motherTerm = motherTerm;
	}
	public List<FieldConfig> getFields() {
		return fields;
	}
	public void setFields(List<FieldConfig> fields) {
		this.fields = fields;
	}
	
}
