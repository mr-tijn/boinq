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
package com.genohm.viewsGWT.client.renderer;

import java.util.LinkedList;
import java.util.List;

import com.genohm.viewsGWT.client.util.VerticalStacker;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.smartgwt.client.widgets.drawing.DrawItem;
import com.smartgwt.client.widgets.drawing.DrawPane;

public class DrawFeature /*extends DrawGroup*/ {
	
	
	// old version used to just extend drawgroup
	// and visual items needed to just setdrawgroup to this,
	// but handlers were not triggered
	
	protected Feature feature;
	protected VerticalStacker stacker = null;
	protected List<DrawItem> visualItems;
	protected List<DrawFeature> childFeatures;
	protected DrawPane drawPane;
	
	public DrawFeature(Feature feature) {
		super();
		this.feature = feature;
		//this.stacker = new VerticalStacker();
		this.visualItems = new LinkedList<DrawItem>();
		this.childFeatures = new LinkedList<DrawFeature>();
	}
	public Feature getFeature() {
		return feature;
	}
	public void setFeature(Feature feature) {
		this.feature = feature;
	}
	public VerticalStacker getStacker() {
		if (stacker == null) stacker = new VerticalStacker();
		return stacker;
	}
	public void addVisualItem(DrawItem item) {
		item.setDrawPane(drawPane);
		visualItems.add(item);
		//item.setDrawGroup(this);
	}
	public List<DrawItem> getVisualItems() {
		return visualItems;
	}
	public void destroy() {
		for (DrawFeature childFeature: childFeatures) {
			childFeature.destroy();
		}
		for (DrawItem visualItem: visualItems) {
			visualItem.destroy();
		}
	}
	public void moveBy(Integer offset, int i) {
		for (DrawFeature childFeature: childFeatures) {
			childFeature.moveBy(offset, i);
		}
		for (DrawItem visualItem: visualItems) {
			visualItem.moveBy(offset, i);
		}
	}
	public void draw() {
		
		for (DrawFeature childFeature: childFeatures) {
			childFeature.draw();
		}
		for (DrawItem visualItem: visualItems) {
			visualItem.draw();
		}
	}
	public int count() {
		int count = 0;
		for (DrawFeature childFeature: childFeatures) count += childFeature.count();
		count+= visualItems.size();
		return count;
	}
	public void setDrawPane(DrawPane drawPane) {
		this.drawPane = drawPane;
		for (DrawFeature childFeature: childFeatures) {
			childFeature.setDrawPane(drawPane);
		}
		for (DrawItem visualItem: visualItems) {
			visualItem.setDrawPane(drawPane);
		}
	}
	public void addChild(DrawFeature childFeature) {
		childFeatures.add(childFeature);
	}
}
