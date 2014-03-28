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
package com.genohm.viewsGWT.client;



import com.genohm.viewsGWT.client.components.Browser;
import com.genohm.viewsGWT.client.components.MainToolBar;
import com.genohm.viewsGWT.client.components.Navigator;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class ViewsGWT extends VLayout implements EntryPoint {
	
	private static ViewsServerAsync VIEWSSERVER = null;
	public static ViewsServerAsync getViewsServer() {
		if (VIEWSSERVER == null) {
			VIEWSSERVER = GWT.create(ViewsServer.class);
		}
		return VIEWSSERVER;
	}
	private static FeatureServerAsync FEATURESERVER = null;
	public static FeatureServerAsync getFeatureServer() {
		if (FEATURESERVER == null) {
			FEATURESERVER = GWT.create(FeatureServer.class);
		}
		return FEATURESERVER;
	}

	public void onModuleLoad() {
		setWidth100();
		setHeight100();
		setBackgroundColor("grey");

		MainToolBar main = new MainToolBar();
		main.setWidth100();
		
		Navigator roiBrowser = new Navigator();
		roiBrowser.setWidth(300);
		roiBrowser.setHeight100();

		Browser genomeBrowser = new Browser();
		genomeBrowser.setWidth("*");
		genomeBrowser.setHeight100();
		
		HLayout hLayout = new HLayout();
		hLayout.setWidth100();
		hLayout.setHeight100();
		hLayout.setMembers(roiBrowser,genomeBrowser);

		hideLoading();
		setMembers(main,hLayout);
		draw();

	}
	
	public native void hideLoading() /*-{
		var loading = $wnd.document.getElementById('loadingWrapper'); 
		if (loading != null) loading.parentNode.removeChild(loading);
	}-*/;
	
}
