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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.genohm.viewsGWT.client.eventbus.FeatureSelection;
import com.genohm.viewsGWT.client.track.Track;
import com.genohm.viewsGWT.client.util.HorizontalScaler;
import com.genohm.viewsGWT.client.util.VerticalStacker;
import com.genohm.viewsGWT.shared.Location;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.genohm.viewsGWT.shared.renderer.RendererSettings;
import com.smartgwt.client.widgets.drawing.DrawLine;
import com.smartgwt.client.widgets.drawing.DrawPane;
import com.smartgwt.client.widgets.drawing.DrawRect;
import com.smartgwt.client.widgets.drawing.Point;

public class DirectionalBlockRenderer extends FeatureRenderer {

	protected VerticalStacker stacker = new VerticalStacker();

	public DirectionalBlockRenderer(DrawPane drawPane, HorizontalScaler scaler, Track track, RendererSettings settings) {
		super(drawPane, scaler, track, settings);
	}

	@Override
	public List<DrawFeature> drawFeatures(List<Feature> features) throws Exception {
		stacker.init(features);
		int levelHeight = settings.getFeatureHeight() + settings.getGapWidth();
		int totalHeight = stacker.getLevelCount() * levelHeight + settings.getGapWidth();
		drawPane.setHeight(totalHeight);
		List<DrawFeature> drawFeatures = new LinkedList<DrawFeature>();
		for (Feature feature : features) {
			if (scaler.withinRegion(feature)) {
				int level = stacker.getLevel(feature);	
				int top = settings.getGapWidth() + level * levelHeight;
				DrawFeature drawFeature = createDrawFeature(feature,settings.getFeatureHeight(),top,null);

				addListeners(drawFeature);
				if (drawFeature != null) {
					drawFeature.draw();
					drawFeatures.add(drawFeature);
				}
			}
		}
		return drawFeatures;
	}

	@Override
	public DrawFeature createDrawFeature(Feature feature, int height, int top, DrawFeature parentFeature) throws Exception {
		DrawFeature drawFeature = new DrawFeature(feature);
		drawFeature.setDrawPane(drawPane);
// 		int level = drawFeature.stacker.getLevel(feature);
		Iterator<Location> locationIterator = feature.getLoc().iterator();
		Integer lastEnd = null;
		while (locationIterator.hasNext()) {
			Location loc = locationIterator.next();
			int left = scaler.getLeft(loc);
			int right = scaler.getRight(loc);
			Boolean direction = scaler.getDirection(feature);
			if (lastEnd != null) {
				//	draw arc
				DrawLine arc = new DrawLine();
				if (direction) {
					arc.setStartPoint(new Point(lastEnd,top - height / 2));
					arc.setEndPoint(new Point(left,top - height / 2));
				} else {
					arc.setStartPoint(new Point(lastEnd,top - height / 2));
					arc.setEndPoint(new Point(right,top - height / 2));	
				}
				drawFeature.addVisualItem(arc);
				//arc.draw();
			}

			if (locationIterator.hasNext()) {
				DrawRect rect = new DrawRect();
				rect.setLeft(left);
				rect.setTop(top);
				rect.setWidth(scaler.getWidth(loc));
				rect.setHeight(height);
				rect.setFillColor(settings.getColor(feature.getScore()));
				drawFeature.addVisualItem(rect);
			} else {
				DrawPointedRect pointedRect = new DrawPointedRect();
				pointedRect.setLeft(left);
				pointedRect.setTop(top);
				pointedRect.setLineWidth(settings.getLineWidth());
				pointedRect.setLineColor(settings.getLineColor());				
				pointedRect.setWidth(scaler.getWidth(loc));
				pointedRect.setHeight(height);
				pointedRect.setDirection(direction);
				pointedRect.setFillColor(settings.getColor(feature.getScore()));
				drawFeature.addVisualItem(pointedRect);
			}
			if (direction) {
				lastEnd = right;
			} else {
				lastEnd = left;
			}
		}
		return drawFeature;
	}

	@Override
	public void onFeatureSelect(FeatureSelection selection, Feature feature) {
	}


 

	 

}
