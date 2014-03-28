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
package com.genohm.viewsGWT.shared.data;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class RawSPARQLResultSet {
		protected List<Map<String,RDFNode>> records;
		protected List<String> variableNames;
		public List<Map<String, RDFNode>> getRecords() {
			return records;
		}
		public void setRecords(List<Map<String, RDFNode>> records) {
			this.records = records;
		}
		public List<String> getVariableNames() {
			return variableNames;
		}
		public void setVariableNames(List<String> variableNames) {
			this.variableNames = variableNames;
		}
		

}
