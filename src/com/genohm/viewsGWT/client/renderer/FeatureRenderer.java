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

import java.util.List;

import com.genohm.viewsGWT.client.eventbus.Event;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.client.eventbus.FeatureSelection;
import com.genohm.viewsGWT.client.track.Track;
import com.genohm.viewsGWT.client.util.HorizontalScaler;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.genohm.viewsGWT.shared.renderer.RendererSettings;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.StringUtil;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.drawing.DrawItem;
import com.smartgwt.client.widgets.drawing.DrawPane;
import com.smartgwt.client.widgets.drawing.events.ClickEvent;
import com.smartgwt.client.widgets.drawing.events.ClickHandler;
import com.smartgwt.client.widgets.drawing.events.MouseOutEvent;
import com.smartgwt.client.widgets.drawing.events.MouseOutHandler;
import com.smartgwt.client.widgets.drawing.events.MouseOverEvent;
import com.smartgwt.client.widgets.drawing.events.MouseOverHandler;

public abstract class FeatureRenderer {
	protected RendererSettings settings;
 	protected DrawPane drawPane;
 	protected HorizontalScaler scaler;
	protected HTMLFlow toolTip;
	protected Track track;
	protected final int TOOLTIP_OFFSET = 10;
	int linewidth=1;


	FeatureRenderer(DrawPane drawPane, HorizontalScaler scaler, Track track, RendererSettings settings) {
		this.drawPane = drawPane;
		this.scaler = scaler;
		this.track = track;
		this.settings = settings;
	}
	public static FeatureRenderer getRenderer(RendererSettings settings, DrawPane drawPane, HorizontalScaler scaler, Track track) {
		if (settings.getRendererName().equalsIgnoreCase("block")) return new HierarchicalDirectionalBlockRenderer(drawPane, scaler, track, settings);
		if (settings.getRendererName().equalsIgnoreCase("transcript")) return new TranscriptRenderer(drawPane, scaler, track, settings);
		if (settings.getRendererName().equalsIgnoreCase("mappedread")) return new MappedReadRenderer(drawPane, scaler, track, settings);
		if (settings.getRendererName().equalsIgnoreCase("pairedend")) return new PairedEndRenderer(drawPane, scaler, track, settings);
		if (settings.getRendererName().equalsIgnoreCase("directionalblock")) return new DirectionalBlockRenderer(drawPane, scaler, track, settings);
		if (settings.getRendererName().equalsIgnoreCase("gene")) return new GeneRenderer(drawPane, scaler, track, settings);
		if (settings.getRendererName().equalsIgnoreCase("wigCurve")) return new GraphCurveRenderer(drawPane, scaler, track, settings);
		if (settings.getRendererName().equalsIgnoreCase("wigBar")) return new GraphBarRenderer(drawPane, scaler, track, settings);
		if (settings.getRendererName().equalsIgnoreCase("bigWigBar")) return new GraphBarRenderer(drawPane, scaler, track, settings);
		if (settings.getRendererName().equalsIgnoreCase("bigWigCurve")) return new GraphCurveRenderer(drawPane, scaler, track, settings);
		if (settings.getRendererName().equalsIgnoreCase("bigBed")) return new HierarchicalDirectionalBlockRenderer(drawPane, scaler, track, settings);
		if (settings.getRendererName().equalsIgnoreCase("Bed")) return new HierarchicalDirectionalBlockRenderer(drawPane, scaler, track, settings);
		return null;
	}
 

	public abstract List<DrawFeature> drawFeatures(List<Feature> features) throws Exception;
 	public abstract DrawFeature createDrawFeature(Feature feature, int height, int top, DrawFeature parentFeature) throws Exception;
 	public DrawFeature createDrawFeature(Feature feature, int height, int top) throws Exception {
		return createDrawFeature(feature,height,top,null);
	}
	
	public abstract void onFeatureSelect(FeatureSelection selection, Feature fullFeature);
	public void addListeners(final DrawFeature drawFeature) {
		if (drawFeature == null || drawFeature.feature == null || drawFeature.getVisualItems() == null) return;
		String htmlText = "<H3>"+StringUtil.asHTML(drawFeature.feature.getName())+"</H3>";
		if (drawFeature.feature.getDescription() != null) htmlText += "\n<p>"+StringUtil.asHTML(drawFeature.feature.getDescription())+"</p>";
		final String toolTipText = htmlText;
		
		for (DrawItem visualItem: drawFeature.getVisualItems()) {
			final DrawItem theItem = visualItem;
			visualItem.addMouseOverHandler(new MouseOverHandler() {
				
				@Override
				public void onMouseOver(MouseOverEvent event) {
					if (toolTip == null) { 
						toolTip = new HTMLFlow();
						toolTip.setBackgroundColor("lightblue");
						toolTip.setOpacity(80);
						toolTip.setWidth(250);
						toolTip.setAutoHeight();
						toolTip.setOverflow(Overflow.VISIBLE);
					}
					int[] bounds = theItem.getBoundingBox();
					int height = bounds[3] - bounds[1];
//					toolTip.moveTo(drawPane.getAbsoluteLeft() + drawFeature.getPageLeft() + TOOLTIP_OFFSET,drawPane.getAbsoluteTop() + drawFeature.getPageTop() + height + TOOLTIP_OFFSET);
					int left = Math.max(0, drawPane.getAbsoluteLeft() + theItem.getPageLeft()) + TOOLTIP_OFFSET;
					int top = drawPane.getAbsoluteTop() + theItem.getPageTop() + height + TOOLTIP_OFFSET;
					toolTip.moveTo(left,top);
					toolTip.setContents(toolTipText);
					toolTip.show();
				}
			});
			visualItem.addMouseOutHandler(new MouseOutHandler() {
				@Override
				public void onMouseOut(MouseOutEvent event) {
					if (toolTip != null) {
						toolTip.hide();
					}
				}
			});
			visualItem.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (toolTip != null) {
						toolTip.hide();
					}
					EventBus.publish(new Event(EventbusTopic.FEATURE_SELECTED), new FeatureSelection(track, drawFeature));
				}
			});
		}
	}

}
