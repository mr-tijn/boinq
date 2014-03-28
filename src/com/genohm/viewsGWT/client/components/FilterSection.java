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
package com.genohm.viewsGWT.client.components;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.gargoylesoftware.htmlunit.protocol.about.Handler;
import com.genohm.viewsGWT.client.dialog.SPARQLBuilder;
import com.genohm.viewsGWT.client.eventbus.Event;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.client.util.ExternalHandler;
import com.genohm.viewsGWT.shared.data.FeatureDatasource;
import com.genohm.viewsGWT.shared.data.GFFDatasource;
import com.genohm.viewsGWT.shared.data.TrackSpecification;
import com.genohm.viewsGWT.shared.renderer.RendererSettings;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.layout.HLayout;

public class FilterSection extends HLayout {
	
	protected Button buildButton;
	protected DynamicForm textForm;
	protected Button filterButton;
	protected List<ExternalHandler> externalHandlers = new LinkedList<ExternalHandler>();
	protected SPARQLBuilder sparqlBuilder;
	protected String featureTypeIRI = null;
	
	public String getFeatureTypeIRI() {
		return featureTypeIRI;
	}

	public void setFeatureTypeIRI(String featureTypeIRI) {
		this.featureTypeIRI = featureTypeIRI;
		if (sparqlBuilder != null) sparqlBuilder.setFeatureTypeIRI(featureTypeIRI);
		buildButton.enable();
	}

	public FilterSection() {
		addBuildButton();
		addTextForm();
		addFilterButton();
	}
	
	protected void addBuildButton() {
		buildButton = new Button("Generate expression");
		buildButton.setWidth(150);
		buildButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				sparqlBuilder = new SPARQLBuilder(featureTypeIRI);
				sparqlBuilder.setWidth("60%");
				sparqlBuilder.setHeight("60%");
				sparqlBuilder.setTop("20%");
				sparqlBuilder.setLeft("20%");
				sparqlBuilder.addExternalResultHandler(new ExternalHandler() {
					@Override
					public void onSuccess(Object argument) {
						textForm.setValue("query", (String) argument);
					}
					@Override
					public void onFail(Throwable t) {
						EventBus.publish(new Event(EventbusTopic.ERROR), t.getMessage());
					}
				});
				sparqlBuilder.show();
			}
		});
		buildButton.disable();
		addMember(buildButton);
	}
	
	protected void addTextForm() {
		textForm = new DynamicForm();
		textForm.setBackgroundColor("green");
		textForm.setWidth("*");
		textForm.setHeight("100%");
		textForm.setNumCols(2);
		
		DataSourceTextField queryText = new DataSourceTextField("query","SPARQL Expression");
		
		DataSource textDS = new DataSource();
		textDS.setID("local_textDS");
		textDS.setFields(queryText);
		textDS.setClientOnly(true);
	
		TextAreaItem queryTextItem = new TextAreaItem("query");
		queryTextItem.setColSpan(2);
		queryTextItem.setWidth("*");
		queryTextItem.setHeight("100%");
		queryTextItem.setTextBoxStyle("fixedFontTextAreaItem");
		queryTextItem.setShowTitle(false);
	
		//textForm.setDataSource(textDS);
		textForm.setFields(queryTextItem);
		addMember(textForm);
	}
	
	protected void addFilterButton() {
		filterButton = new Button("Create");
		filterButton.setWidth(150);
		filterButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String query = textForm.getValueAsString("query");
				for (ExternalHandler handler: externalHandlers) {
					handler.onSuccess(query);
				}
			}
		});
		addMember(filterButton);

	}
	
	protected void addTrackFilterButton() {
		filterButton = new Button("Create track");
		filterButton.setWidth(150);
		filterButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String query = textForm.getValueAsString("query");
				TrackSpecification trackSpec = new TrackSpecification();
				trackSpec.setTitle("Generated Track");
				FeatureDatasource featureDS = new GFFDatasource();
				featureDS.setCanBeFiltered(true);
				featureDS.setChromosomePrefix("chr");
				featureDS.setDescription("Example generated track");
				featureDS.setIsPublic(false);
				featureDS.setOwner("test user");
				//trackSpec.setFeatureSourceName("DS_SPARQL");
				trackSpec.setFeatureDatasource(featureDS);
				trackSpec.setFilterExpression(query);
				trackSpec.setIsPublic(false);
				trackSpec.setOwner("test user");
				RendererSettings rs = new RendererSettings();
				rs.setRendererName("block");
				trackSpec.setRendererSettings(rs);
				EventBus.publish(new Event(EventbusTopic.ADD_TRACK), trackSpec);
				for (ExternalHandler handler: externalHandlers) {
					handler.onSuccess(null);
				}
			}
		});
		addMember(filterButton);
	}
	
	public void addExternalHandler(ExternalHandler handler) {
		externalHandlers.add(handler);
	}

}
