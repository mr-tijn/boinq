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

import java.util.LinkedList;
import java.util.List;

import com.genohm.viewsGWT.client.util.ExternalHandler;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

public class TermSelectPopup extends Window {
	protected String baseIRI;
	protected String gffFile;
	protected String parentPropertyID;
	
	protected TreeGrid termTree;
	protected Button confirmButton;
	protected VLayout mainLayout;

	
	public TermSelectPopup() {
		super();
		setTitle("Select Term");
		setCanDragResize(true);
		addHandlers();
		addMainLayout();
		//addTermTree();
		addOntologyHierarchy( "http://bioportal.bioontology.org/ontologies/SO");
		addButtons();
	}

	
	public void addExternalSelectHandler(final ExternalHandler handler) {
		confirmButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				List<String> selectedTerms = new LinkedList<String>();
				ListGridRecord[] selection = termTree.getSelectedRecords();
				for (ListGridRecord selectedItem: selection) {
					selectedTerms.add("<"+selectedItem.getAttribute("term")+">");
				}
				handler.onSuccess(selectedTerms);
				close();
			}
		});
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

	protected void addTermTree() {
		termTree = new TreeGrid();
		DataSource ds = DataSource.getDataSource("RDFTermTree");
		Criteria criteria = new Criteria("rdfFile", "/ontologies/so.rdf");
		criteria.addCriteria(new Criteria("baseIRI","http://www.semantic-systems-biology.org/SSB#"));
		criteria.addCriteria(new Criteria("parentProperty","http://www.semantic-systems-biology.org/SSB#is_a"));
		termTree.setDataSource(ds);
		termTree.setShowFilterEditor(true);
		termTree.setCriteria(criteria);
		termTree.setAutoFetchData(true);
		termTree.setWidth("100%");
		termTree.setHeight("100%");
		termTree.setSelectionAppearance(SelectionAppearance.CHECKBOX);
		ListGridField nameField = new ListGridField("term_name");
		nameField.setTitle("Name");
		nameField.setCanFilter(true);
		ListGridField iriField = new ListGridField("term_IRI");
		iriField.setTitle("Full IRI");
		iriField.setCanFilter(false);
		termTree.setFields(nameField, iriField);
		mainLayout.addMember(termTree);
	}
	
	
	protected void addOntologyHierarchy(String ontologyGraph) {
		TreeGrid ontologyHierarchy = new TreeGrid();
		DataSource ds = DataSource.getDataSource("OntologyHierarchy");
		Criteria criteria = new Criteria("ontologyGraph", ontologyGraph);
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
		hlayout.setAlign(Alignment.RIGHT);
		hlayout.addChild(confirmButton);
		mainLayout.addMember(hlayout);	
	}
}
