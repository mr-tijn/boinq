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

import com.genohm.viewsGWT.client.ViewsGWT;
import com.genohm.viewsGWT.client.components.DynamicMatchTree;
import com.genohm.viewsGWT.client.eventbus.Event;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.client.records.MatchRecord;
import com.genohm.viewsGWT.client.util.ExternalHandler;
import com.genohm.viewsGWT.shared.query.Match;
import com.genohm.viewsGWT.shared.query.MatchAll;
import com.genohm.viewsGWT.shared.query.MatchType;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;

public class SPARQLBuilder extends Window {

	protected TreeGrid criteriaTree;
	protected Button generateButton;
	protected VLayout mainLayout;
	protected DynamicMatchTree matchTree = null;
	protected String featureTypeIRI = null;
	protected Tree dataTree;
	
	public String getFeatureTypeIRI() {
		return featureTypeIRI;
	}

	public void setFeatureTypeIRI(String featureTypeIRI) {
		this.featureTypeIRI = featureTypeIRI;
		if (matchTree != null) {
			matchTree.setFeatureTypeIRI(featureTypeIRI);
			RecordList rootNodes = matchTree.getData().getRoot().getAttributeAsRecordList("children");
			if (rootNodes.getLength() == 1) {
				MatchRecord root = (MatchRecord) rootNodes.get(0);
				matchTree.getData().add(new MatchRecord(new MatchType(featureTypeIRI)), root);
			}
		}
		
	}
	
	public SPARQLBuilder(String featureTypeIRI) {
		super();
		this.featureTypeIRI = featureTypeIRI;
		setTitle("Build filter expression");
		setCanDragResize(true);
		addHandlers();
		addMainLayout();
		addMatchFilter();
		addButtons();
	}
		
	protected void addMainLayout() {
		mainLayout = new VLayout();
		addItem(mainLayout);
	}

	public void addExternalResultHandler(final ExternalHandler handler) {
		
		generateButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				RecordList rootNodes = matchTree.getData().getRoot().getAttributeAsRecordList("children");
				if (rootNodes.getLength() == 1) {
					MatchRecord root = (MatchRecord) rootNodes.get(0);
					Match rootMatch = root.getMatch();
					ViewsGWT.getViewsServer().generateQuery(rootMatch, new AsyncCallback<String>() {
						@Override
						public void onSuccess(String result) {
							handler.onSuccess(result);
							close();
						}
						@Override
						public void onFailure(Throwable caught) {
							handler.onFail(caught);
						}
					});
				} else {
					EventBus.publish(new Event(EventbusTopic.ERROR), "You need exactly one root criterion");
					close();
				}
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
	
	protected class LocalMatchDatasource extends DataSource {
		public LocalMatchDatasource(String featureDatasource) {
			setClientOnly(true);
		}
	}
	
	
	protected void addMatchFilter() {
		matchTree = new DynamicMatchTree(featureTypeIRI);
		matchTree.setWidth100();
		matchTree.setHeight100();
		matchTree.setData(createInitialTree());
		mainLayout.addMember(matchTree);
	}

	protected Tree createInitialTree() {
		MatchAll root = new MatchAll();
		if (featureTypeIRI != null) {
			root.addMatch(new MatchType(featureTypeIRI));
		}
		dataTree = new Tree();
		dataTree.setModelType(TreeModelType.PARENT);
		dataTree.setData(new MatchRecord[]{root.createRecord()});
		return dataTree;
	}
	
	
	
	protected void addButtons() {
		HLayout hLayout = new HLayout();
		generateButton = new Button("Confirm");
		hLayout.setAlign(Alignment.RIGHT);
		hLayout.addChild(generateButton);
		mainLayout.addMember(hLayout);
		generateButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				RecordList rootNodes = matchTree.getData().getRoot().getAttributeAsRecordList("children");
					MatchRecord root = (MatchRecord) rootNodes.get(0);
					Match rootMatch = root.getMatch();
					ViewsGWT.getViewsServer().generateQuery(rootMatch, new AsyncCallback<String>() {
						@Override
						public void onSuccess(String result) {
//							SC.say(result);
						}
						@Override
						public void onFailure(Throwable caught) {
							EventBus.publish(new Event(EventbusTopic.ERROR), caught.getMessage());						
						}
					});
			}
		});
	}
	
	
	
	
}
