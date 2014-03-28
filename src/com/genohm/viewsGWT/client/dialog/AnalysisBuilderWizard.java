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
package com.genohm.viewsGWT.client.dialog;

import java.util.LinkedHashMap;
import java.util.List;

import com.genohm.viewsGWT.client.ViewsGWT;
import com.genohm.viewsGWT.client.eventbus.Event;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.shared.RegionOfInterest;
import com.genohm.viewsGWT.shared.analysis.CountReadsPerRegion;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class AnalysisBuilderWizard extends Window {
	VLayout mainLayout = new VLayout();
	DynamicForm mainForm = new DynamicForm();
	
	//TODO: select analysis type
	public AnalysisBuilderWizard() {
		setTitle("New Analysis");
		addItem(mainLayout);
		addWarning();
		addRoiSelect();
	}
	
	private void addWarning() {
		Label warningLabel = new Label();
		warningLabel.setContents("Warning: under construction - only read counts supported");
		mainLayout.addMember(warningLabel);
	}
	
	private void addRoiSelect() {
		final SelectItem roiSelect = new SelectItem();
		roiSelect.setTitle("Region of Interest");
		roiSelect.disable();
		roiSelect.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				Long roiId = null;
				try {
					roiId = Long.parseLong((String) event.getValue());
					CountReadsPerRegion cr = new CountReadsPerRegion();
					cr.setRoiId(roiId);
					// TODO: handle trackId
					cr.setTrackId(34);
					ViewsGWT.getViewsServer().saveAnalysis(cr, new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							EventBus.publish(new Event(EventbusTopic.REFRESH_ANALYSIS), null);
						}
						@Override
						public void onFailure(Throwable caught) {
							EventBus.publish(new Event(EventbusTopic.ERROR), "Problem saving analysis");
						}
					});
				} catch (NumberFormatException e) {
					EventBus.publish(new Event(EventbusTopic.ERROR), "Problem referring to region of interest");
				} catch (Exception e) {
					EventBus.publish(new Event(EventbusTopic.ERROR), "Problem saving analysis");
				}
				destroy();
			}
		});
		mainForm.setFields(roiSelect);
		mainLayout.addMember(mainForm);
		ViewsGWT.getViewsServer().getROIs(new AsyncCallback<List<RegionOfInterest>>() {
			@Override
			public void onSuccess(List<RegionOfInterest> result) {
				LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
				for (RegionOfInterest roi: result) {
					valueMap.put(roi.getId().toString(), roi.getName());
				}
				roiSelect.setValueMap(valueMap);
				roiSelect.enable();
			}
			@Override
			public void onFailure(Throwable caught) {
				EventBus.publish(new Event(EventbusTopic.ERROR), "Could not fetch regions of interest");
			}
		});
		
	}
}
