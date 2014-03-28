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
package com.genohm.viewsGWT.client.formitem;

import java.util.LinkedList;
import java.util.List;

import com.genohm.viewsGWT.client.util.ExternalHandler;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

public class OntologyTermPicker extends Window {
	
	protected VLayout mainLayout;
	protected Button confirmButton;
	protected CheckboxItem fetchChildrenCheckbox;
	protected TreeGrid ontologyHierarchy;
	protected String targetGraph;
	protected String targetEndpoint;
	protected String targetFilter;
	protected String motherTerm;
	protected FormItem item;
	
	public OntologyTermPicker() {
	}
	
	public OntologyTermPicker(String targetGraph, String targetEndpoint,
			String targetFilter, String motherTerm) {
		this();
		setTitle("Select Term");
		this.targetGraph = targetGraph;
		this.targetEndpoint = targetEndpoint;
		this.targetFilter = targetFilter;
		this.motherTerm = motherTerm;
		setCanDragResize(true);
		addMainLayout();
		addOntologyHierarchy();
		addButtons();		
		addHandlers();
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
	
	protected void addMainLayout() {
		mainLayout = new VLayout();
		addItem(mainLayout);
	}

	public void setResultFormItem(final FormItem item) {
		this.item = item;
		confirmButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String result = "";
				ListGridRecord[] selection = ontologyHierarchy.getSelectedRecords();
				for (ListGridRecord selectedItem: selection) {
					result += selectedItem.getAttribute("term")+";";
				}
				try {
					result = result.substring(0, result.length()-1);
				} catch (Exception e) {
					// swallow
				}
				item.setValue(result);
				close();
			}
		});

	}
	
	public void addExternalSelectHandler(final ExternalHandler handler) {
		confirmButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				List<String> selectedTerms = new LinkedList<String>();
				ListGridRecord[] selection = ontologyHierarchy.getSelectedRecords();
				for (ListGridRecord selectedItem: selection) {
					selectedTerms.add(selectedItem.getAttribute("term"));
				}
				if (fetchChildrenCheckbox.getValueAsBoolean()) {
					Criteria criteria = new Criteria("ontologyGraph", targetGraph);
					criteria.addCriteria(new Criteria("targetEndpoint", targetEndpoint));
					criteria.addCriteria(new Criteria("targetFilter", targetFilter));
					criteria.addCriteria(new Criteria("allChildren", "1"));
					// TODO: find way to express selection as string and fetch children for multiple parents
					final String parentTerm = selectedTerms.get(0);
					criteria.addCriteria(new Criteria("parent",parentTerm));
					//TODO: indicate we are fetching children
					ontologyHierarchy.getDataSource().fetchData(criteria, new DSCallback() {
						
						@Override
						public void execute(DSResponse response, Object rawData, DSRequest request) {
							// TODO 
							List<String> childTerms = new LinkedList<String>();
							childTerms.add(parentTerm);
							Record[] allChildren = response.getData();
							for (Record child: allChildren) {
								childTerms.add(child.getAttribute("term"));
							}
							handler.onSuccess(childTerms);
						}
					});
				} else {
					handler.onSuccess(selectedTerms);
				}
				close();
			}
		});
	}
	protected void addOntologyHierarchy() {
		ontologyHierarchy = new TreeGrid();

		DataSource ds = DataSource.getDataSource("OntologyHierarchy");
		
		// TODO: stuff to provide: targetFilter
		// targetGraph => ontologyGraph
		// targetEndpoint => ontologyEndpoint
		// motherTerm => parent
		Criteria criteria = new Criteria("ontologyGraph", targetGraph);
		criteria.addCriteria(new Criteria("targetEndpoint", targetEndpoint));
		criteria.addCriteria(new Criteria("targetFilter", targetFilter));
		criteria.addCriteria(new Criteria("parent", motherTerm));
		//criteria.addCriteria(new Criteria("targetGraph", targetGraph));
		ontologyHierarchy.setDataSource(ds);
		ontologyHierarchy.setShowFilterEditor(true);
		ontologyHierarchy.setCriteria(criteria);
		ontologyHierarchy.setAutoFetchData(true);
		ontologyHierarchy.setWidth("100%");
		ontologyHierarchy.setHeight("100%");
		ontologyHierarchy.setSelectionAppearance(SelectionAppearance.CHECKBOX);
		ListGridField nameField = new ListGridField("label");
		nameField.setTitle("Name");
		nameField.setCanFilter(true);
		ListGridField iriField = new ListGridField("term");
		iriField.setTitle("Full IRI");
		iriField.setCanFilter(false);
		ontologyHierarchy.setFields(nameField, iriField);
		mainLayout.addMember(ontologyHierarchy);
		
	}
	
	protected void addButtons() {
		HLayout hlayout = new HLayout();
		confirmButton = new Button("Select");
		hlayout.addMember(confirmButton);
		DynamicForm form = new DynamicForm();
		fetchChildrenCheckbox = new CheckboxItem("IncludeChildTerms");
		fetchChildrenCheckbox.setTitle("Include subClassOf terms in selection");
		fetchChildrenCheckbox.setValue(true);
		form.setFields(fetchChildrenCheckbox);
		hlayout.addMember(form);
		mainLayout.addMember(hlayout);	
	}

}
