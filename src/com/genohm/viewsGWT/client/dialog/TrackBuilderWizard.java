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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.genohm.viewsGWT.client.ViewsServer;
import com.genohm.viewsGWT.client.ViewsServerAsync;
import com.genohm.viewsGWT.client.components.FilterSection;
import com.genohm.viewsGWT.client.eventbus.Event;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.client.util.ExternalHandler;
import com.genohm.viewsGWT.shared.data.FeatureDatasource;
import com.genohm.viewsGWT.shared.query.MatchField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class TrackBuilderWizard extends Window {
	
	Map<String,FeatureDatasource> dsMap = new HashMap<String, FeatureDatasource>();
	VLayout mainLayout;
	SelectItem datasourceSelect;
	ButtonItem dataUpload;
	
	public TrackBuilderWizard() {
		setTitle("Build your track");
		addHandlers();
		fetchDataSources();
		addMainLayout();
		addDatasourceSelect();
		addFilterSection();
	}
	
	protected void addMainLayout() {
		mainLayout = new VLayout();
		addItem(mainLayout);
	}

	
	public void fetchDataSources() {
		ViewsServerAsync viewsServer = GWT.create(ViewsServer.class);
		viewsServer.getFeatureDatasources(new AsyncCallback<List<FeatureDatasource>>() {
			@Override
			public void onSuccess(List<FeatureDatasource> result) {
				for (FeatureDatasource ds: result) {
					if (ds.getCanBeFiltered()) {
						dsMap.put(ds.getName(), ds);
					}
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				EventBus.publish(new Event(EventbusTopic.ERROR), "Could not get feature data sources");
			}
		});
	}
	
	public void addDatasourceSelect() {
		DynamicForm theForm = new DynamicForm();
		datasourceSelect = new SelectItem();
		datasourceSelect.setTitle("Select datasource");
		int length = dsMap.keySet().size();
		String[] dsNames = new String[length];
		for (String name: dsMap.keySet()) {dsNames[--length] = name;}
		//datasourceSelect.setValueMap(dsNames);
		datasourceSelect.setValueMap("Not yet implemented");		
		dataUpload = new ButtonItem();
		dataUpload.setTitle("Upload");
		dataUpload.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				UploadDialog uploader = new UploadDialog();
				uploader.setCanDragResize(true);
				uploader.show();
			}
		});
		theForm.setFields(datasourceSelect,dataUpload);
		mainLayout.addMember(theForm);
	}
	
	public void addFilterSection() {
		FilterSection filterSection = new FilterSection();
		filterSection.addExternalHandler(new ExternalHandler() {
			@Override
			public void onSuccess(Object argument) {
				close();
			}
			@Override
			public void onFail(Throwable t) {}
		});
		mainLayout.addMember(filterSection);
	}
	
	protected void addHandlers() {
		addCloseClickHandler(new CloseClickHandler() {
			
			@Override
			public void onCloseClick(CloseClickEvent event) {
				close();
				
			}
		});
	}
	
	protected void close() {
		destroy();
	}

	
}
