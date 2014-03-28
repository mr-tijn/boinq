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

import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.tree.TreeNode;

public class CriterionTreeNode extends TreeNode {
	public static final String NAME_FIELD = "display_name";
	public static final String ADD_BUTTON = "add_button";
	public static final String TYPE_FIELD = "criterion_type";
	public static final String PARAMETERS_FIELD = "parameters";
	
	public static String[] criterionNames = {"MatchLocation","MatchType","MatchAll","MatchAny","MatchField"};
	public static final String CRITERION_TYPE_LOCATION = criterionNames[0];
	public static final String CRITERION_TYPE_TYPE = criterionNames[1];
	public static final String CRITERION_TYPE_MULTI = criterionNames[2];
	public static final String CRITERION_TYPE_UNION = criterionNames[3];
	public static final String CRITERION_TYPE_FIELD = criterionNames[4];

	
	private CriterionTreeNode() {}
	
	public CriterionTreeNode(String name, Boolean canAddChildren, String criterionType) {
		setDisplayName(name);
		setCanAddChildren(canAddChildren);
		setCriterionType(criterionType);
	}
	
	public void setDisplayName(String name) {
		setAttribute(NAME_FIELD, name);
	}
	
	public void setCanAddChildren(Boolean canAddChildren) {
		setAttribute(ADD_BUTTON, canAddChildren);
	}
	
	public void setCriterionType(String criterionType) {
		setAttribute(TYPE_FIELD, criterionType);
	}
	
	public Boolean canHaveChildren() {
		return getAttributeAsBoolean(ADD_BUTTON);
	}

	protected Record getParameters() {
		Record parameters = getAttributeAsRecord(PARAMETERS_FIELD);
		if (parameters == null) {
			parameters = new Record();
			setAttribute(PARAMETERS_FIELD, parameters);
		}
		return parameters;
	}
	
}
