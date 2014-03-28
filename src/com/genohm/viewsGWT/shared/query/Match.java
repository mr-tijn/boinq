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
import com.google.gwt.user.client.rpc.IsSerializable;

//@Entity
//@Table(name="match")
public interface Match extends IsSerializable {
	public MatchRecord createRecord();
	public void acceptGenerator(SPARQLGenerator generator, String subjectIdentifier);
	public CriterionTreeNode toTreeNode();
	public String getSourceGraph();
	public void setSourceGraph(String sourceGraph);
}
