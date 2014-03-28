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

import java.util.Map;

import com.genohm.viewsGWT.client.ViewsGWT;
import com.genohm.viewsGWT.client.eventbus.Event;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.shared.data.SPARQLResultSet;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;

public class SPARQLBrowser extends Window {

	public static final String defaultPrefixes = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
			"PREFIX track: <http://www.boinq.org/track/>\n"+
			"PREFIX obo: <http://purl.obolibrary.org/obo/>";

	public static final String getGraphQuery = defaultPrefixes +
			"\n" +
			"SELECT ?g WHERE {\n" +
			"	GRAPH ?g {}\n" +
			"}";
	
	public static final String getFeatureTypePropertiesQuery = defaultPrefixes +
			"\n" +
			"SELECT DISTINCT ?sub ?prop WHERE {\n" +
			"	GRAPH <http://www.boinq.org/tracks> {\n" +
			"		?prop rdfs:domain ?p.\n" +
			"		?sub rdfs:subClassOf* ?p.\n" + // expand to all superclasses as well. (mimick extensional rdfs entailment rule ext1)
			"		?sub rdfs:subClassOf track:feature\n" +
			"	}\n" +
			"}";
	
	public static final String getGeneProperties = defaultPrefixes +
			"\n" +
			"SELECT ?prop ?val WHERE {\n" +
			"	obo:SO_0000704 ?prop ?val .\n" +
			"}";
	
	protected String initExpression = getGeneProperties; 
	protected VLayout mainLayout;
	protected DynamicForm inputForm;
	protected ListGrid outputGrid;
	
	public SPARQLBrowser() {
		setTitle("SPARQL Querier");
		addMainLayout();
		addInputForm();
		addOutputGrid();
	}

	protected void addMainLayout() {
		mainLayout = new VLayout();
		addItem(mainLayout);
	}
	
	protected void addInputForm() {
		inputForm = new DynamicForm();
		inputForm.setWidth100();
		inputForm.setNumCols(1);
		final TextAreaItem inputTextArea = new TextAreaItem();
		inputTextArea.setShowTitle(false);
		inputTextArea.setWidth("100%");
		inputTextArea.setColSpan(1);
		inputTextArea.setValue(initExpression);
		ButtonItem submitButton = new ButtonItem();
		submitButton.setTitle("submit");
		submitButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				submit(inputTextArea.getValueAsString());
			}
		});
		submitButton.setWidth(150);
		inputForm.setItems(inputTextArea,submitButton);
		mainLayout.addMember(inputForm);
	}
	
	protected void addOutputGrid() {
		outputGrid = new ListGrid();
		mainLayout.addMember(outputGrid);
	}
	
	protected void submit(String query) {
		ViewsGWT.getViewsServer().sparqlQuery(query, new AsyncCallback<SPARQLResultSet>() {
			
			@Override
			public void onSuccess(SPARQLResultSet results) {
				ListGridField[] fields;
				if (results.getVariableNames() != null) {
					fields = new ListGridField[results.getVariableNames().size()];
					int i = 0;
					for (String varName: results.getVariableNames()) {
						fields[i] = new ListGridField(varName);
						fields[i++].setTitle("?"+varName);
					}
				} else {
					fields = new ListGridField[0];
				}
				outputGrid.setFields(fields);
				
				RecordList recordListResult = new RecordList();
				if (results.getRecords() != null) {
					for (Map<String,String> result: results.getRecords()) {
						recordListResult.add(new Record(result));
					}
				}
				outputGrid.setData(recordListResult);
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Could net get query result", caught);
				EventBus.publish(new Event(EventbusTopic.ERROR), caught.getLocalizedMessage());	
			}
		});
	}
	
	
	
}
