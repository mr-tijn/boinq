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
package com.genohm.viewsGWT.client.analysis;

import com.genohm.viewsGWT.server.analysis.Computation;
import com.genohm.viewsGWT.server.analysis.ROIComputation;
import com.genohm.viewsGWT.shared.analysis.Analysis;
import com.genohm.viewsGWT.shared.analysis.AnalysisVisualizer;
import com.genohm.viewsGWT.shared.analysis.CountReadsPerRegion;
import com.genohm.viewsGWT.shared.analysis.RegionCount;
import com.google.gwt.user.client.ui.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;

public class GraphicalAnalysisVisualizer extends Window implements AnalysisVisualizer {
	
	public GraphicalAnalysisVisualizer() {
		// some sane defaults
		setIsModal(true);
		setHeight(200);
		setWidth(300);
	}
	
	public void showResult(Analysis analysis) {
		//comp.showResult(this);
		Label warning = new Label();
		warning.setText("Under Construction");
		Label theLabel = new Label();
		theLabel.setText("This computation has status: "+analysis.getStatus());
		VLayout mainLayout = new VLayout();
		mainLayout.addMember(warning);
		mainLayout.addMember(theLabel);
		addChild(mainLayout);
	}
	
	public void showResult(CountReadsPerRegion rc) {
		clear();
		VLayout mainLayout = new VLayout();
		DynamicForm form =  new DynamicForm();
		form.setFields(new TextItem());
		mainLayout.addMember(form);
		addChild(mainLayout);
	}

	@Override
	public void visitAnalysis(Analysis analysis) {
		Label warning = new Label();
		warning.setText("Under Construction");
		Label theLabel = new Label();
		theLabel.setText("This computation has status: "+analysis.getStatus());
		VLayout mainLayout = new VLayout();
		mainLayout.addMember(warning);
		mainLayout.addMember(theLabel);
		addItem(mainLayout);
	}
	
	@Override
	public void visitAnalysis(CountReadsPerRegion analysis) {
		setTitle(analysis.getName());
		Label warning = new Label();
		warning.setText("Under Construction");
		Label theLabel = new Label();
		switch(analysis.getStatus()) {
		case Analysis.STATUS_COMPUTING:
			theLabel.setText("This analysis is still running");
			break;
		case Analysis.STATUS_ERROR:
			theLabel.setText("An error occurred: "+analysis.getResultSummary());
			break;
		case Analysis.STATUS_SUCCESS:
			String res = "Read counts: [";
			for (RegionCount rc: analysis.getRegionCounts()) {
				res += rc.getCount() + ",";
			}
			res = res.substring(0, res.length()-1) + "]";
			theLabel.setText(res);
			break;
		}
		VLayout mainLayout = new VLayout();
		mainLayout.addMember(warning);
		mainLayout.addMember(theLabel);
		addItem(mainLayout);
	}


}
