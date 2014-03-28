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

import java.util.List;

import com.genohm.viewsGWT.client.ViewsGWT;
import com.genohm.viewsGWT.client.ViewsServerAsync;
import com.genohm.viewsGWT.client.dialog.SPARQLBrowser;
import com.genohm.viewsGWT.client.dialog.TermSelectPopup;
import com.genohm.viewsGWT.client.dialog.TrackBuilderWizard;
import com.genohm.viewsGWT.client.eventbus.Event;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.client.util.ExternalHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class MainToolBar extends ToolStrip {
	private ToolStripButton trackWizardButton;
	private ToolStripButton logoutButton;
	private Label userLabel;
	private ToolStripButton addSPARQLBRowserButton;

	ViewsServerAsync viewsServer;

	
	public MainToolBar() {
		viewsServer = ViewsGWT.getViewsServer();
//		addTrackWizardButton();
//		addSPARQLBrowserButton();
		addFill();
		addUserLabel();
		addLogoutButton();
	}
	

	protected void addTrackWizardButton() {
		ToolStripButton trackWizardButton = new ToolStripButton();
		trackWizardButton.setIcon("add_filter.png");
		trackWizardButton.setActionType(SelectionType.BUTTON);
		trackWizardButton.setTooltip("Start track creation wizard");
		trackWizardButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				TrackBuilderWizard wizard = new TrackBuilderWizard();
				wizard.setIsModal(true);
				wizard.setTop("10%");
				wizard.setLeft("10%");
				wizard.setWidth("80%");
				wizard.setHeight("80%");
				wizard.show();
			}
		});
		this.addButton(trackWizardButton);
	}
	
	protected void addSPARQLBrowserButton() {
		addSPARQLBRowserButton = new ToolStripButton();
		addSPARQLBRowserButton.setTitle("SPARQL EXplorer");
		addSPARQLBRowserButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SPARQLBrowser browser = new SPARQLBrowser();
				browser.setIsModal(true);
				browser.setCanDragResize(true);
				browser.setTop("10%");
				browser.setLeft("10%");
				browser.setWidth("80%");
				browser.setHeight("80%");
				browser.show();				
			}
		});
		addButton(addSPARQLBRowserButton);
	}
	
	protected void addUserLabel() {
		userLabel = new Label();
		viewsServer.getCurrentUserName(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				userLabel.setContents("logged in as : <b>"+result+"</b>");
			}
			@Override
			public void onFailure(Throwable caught) {
				EventBus.publish(new Event(EventbusTopic.ERROR), "could not get username");				
			}
		});
		addMember(userLabel);
	}
	
	
	protected void addLogoutButton() {
		ButtonItem logoutButton = new ButtonItem();
		logoutButton.setTitle("logout");
		logoutButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			
			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				viewsServer.logout(new AsyncCallback<Void>() {
					@Override
					public native void onSuccess(Void result) /*-{
						$wnd.location.reload(true);
					}-*/;
					@Override
					public void onFailure(Throwable caught) {
						EventBus.publish(new Event(EventbusTopic.ERROR), "Could not log out");
					}
				});
				
			}
		});		
		addFormItem(logoutButton);
	}
	
}
