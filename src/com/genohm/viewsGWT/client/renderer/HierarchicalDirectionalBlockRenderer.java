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

public class HierarchicalDirectionalBlockRenderer extends FeatureRenderer {
	protected final static int MIN_WIDTH_FOR_LABEL = 20;
	protected final static int MIN_FEATURE_HEIGHT = 1;
	protected VerticalStacker stacker = new VerticalStacker();

	public HierarchicalDirectionalBlockRenderer(DrawPane drawPane, HorizontalScaler scaler,
			Track track, RendererSettings settings) {
		super(drawPane, scaler, track, settings);
	}

	@Override
	public DrawFeature createDrawFeature(final Feature feature, final int height, final int top, DrawFeature drawParentFeature) throws Exception {
		final DrawFeature drawFeature = new DrawFeature(feature);
		drawFeature.setDrawPane(drawPane);

		// draw main feature
		if (feature.getSubFeatures() != null) {		
			DrawPointedRect pointedRect = new DrawPointedRect(); 
			pointedRect.setLeft(scaler.getLeft(feature.getLoc().get(0)));
			pointedRect.setHeight(height);
			pointedRect.setTop(top);	
			pointedRect.setWidth(scaler.getWidth(feature));
			pointedRect.setLineWidth(1);
			pointedRect.setLineColor("black");
			pointedRect.setDirection(scaler.getDirection(feature));
			drawFeature.addVisualItem(pointedRect);
			//		addListeners(drawFeature);

			drawFeature.getStacker().init(feature.getSubFeatures());
			int subHeight = 5*height/(6*drawFeature.stacker.getLevelCount()+1);
			int subGap = subHeight/5;
			if (subHeight > MIN_FEATURE_HEIGHT ) {
				for (Feature subFeature : feature.getSubFeatures()) {
					int subLevel = drawFeature.getStacker().getLevel(subFeature);
					int subTop = top + subGap + subLevel * (subHeight + subGap);
					DrawFeature childFeature = createDrawFeature(subFeature,subHeight,subTop,drawFeature);
					drawFeature.addChild(childFeature);
				}
			}
		} else {
		
			//assume ordered by rank (i.e. in same direction of strand)
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
		}
//			boolean firstLocation = true;
//			while (locationIterator.hasNext()) {
//				Location loc = locationIterator.next();	
//				if ((loc.getStrand() && locationIterator.hasNext()) || (!loc.getStrand() && !firstLocation)) {
//					DrawRect rect = new DrawRect();
//					rect.setLeft(scaler.getLeft(loc));
//					rect.setHeight(height);
//					rect.setTop(top);
//					rect.setWidth(scaler.getWidth(loc));
//					rect.setLineWidth(settings.getLineWidth());
//					rect.setLineColor(settings.getLineColor());
//					rect.setFillColor(settings.getColor(feature.getScore()));
//					drawFeature.addVisualItem(rect);
//					//				addListeners(drawFeature);
//					//				System.err.println(rect.getDrawGroup().getWidth()+""+rect.getDrawGroup().getHeight()+" "+rect.getDrawGroup().getTop()+" "+rect.getDrawGroup().getLeft());
//					// 				rect.draw();
//				} else {
//					DrawPointedRect pointedRect = new DrawPointedRect();
//					pointedRect.setLeft(scaler.getLeft(loc));
////					if (feature.getParent()!=null){
//						pointedRect.setHeight(height);
////					}
////					else {
////						pointedRect.setHeight(7);//for the bed and bigbed features 
////					}
//					pointedRect.setTop(top);
//					pointedRect.setWidth(scaler.getWidth(loc));
//					pointedRect.setLineWidth(settings.getLineWidth());
//					pointedRect.setLineColor(settings.getLineColor());
//					pointedRect.setFillColor(settings.getColor(feature.getScore()));
//					pointedRect.setDirection(scaler.getDirection(feature));
//					//pointedRect.setDrawGroup(drawFeature);
//					drawFeature.addVisualItem(pointedRect);
//					//				addListeners(drawFeature);
//					//				pointedRect.draw();
//				}
//
//				firstLocation = false;
//			}
//			if (feature.getLoc().size()!=1){
//				DrawPointedRect rect = new DrawPointedRect();
//				rect.setLeft(scaler.getLeft(feature)); 
//				rect.setHeight(height);
//				rect.setTop(top);
//				rect.setWidth(scaler.getWidth(feature));
//				rect.setLineWidth(settings.getLineWidth());
//				rect.setLineColor("red");
//				//rect.setDrawGroup(drawFeature);
//				drawFeature.addVisualItem(rect);
//				//			System.err.println(rect.getDrawGroup().getWidth()+""+rect.getDrawGroup().getHeight()+" "+rect.getDrawGroup().getTop()+" "+rect.getDrawGroup().getLeft());
//				//			addListeners(drawFeature);
//				rect.setDirection(scaler.getDirection(feature));
//				// 			rect.draw();
//			}
//			Location previous = feature.getLoc().get(0);
//			for (Location loc: feature.getLoc()){
//				if (loc!=feature.getLoc().get(0)){
//					Point firstPoint = new Point (scaler.getRight(previous),top + (height/2));
//					Point secondPoint = new Point (scaler.getRight(previous)+((scaler.getLeft(loc)-scaler.getRight(previous))/2),top+(height/4));
//					Point thirdPoint = new Point (scaler.getLeft(loc), top+(height/2));
//					DrawLine firstLine = new DrawLine();
//					DrawLine secondLine = new DrawLine();
//					//				firstLine.setDrawPane(drawPane);
//					firstLine.setStartPoint(firstPoint);
//					firstLine.setEndPoint(secondPoint);
//					firstLine.setLineWidth(1);
//					firstLine.setLineColor("blue");
//					//firstLine.setDrawGroup(drawFeature);
//					drawFeature.addVisualItem(firstLine);
//					// 				firstLine.draw();
//					//				secondLine.setDrawPane(drawPane);
//					secondLine.setStartPoint(secondPoint);
//					secondLine.setEndPoint(thirdPoint);
//					secondLine.setLineWidth(1);
//					secondLine.setLineColor("blue");
//					//secondLine.setDrawGroup(drawFeature);
//					drawFeature.addVisualItem(secondLine);
//					// 				secondLine.draw();
//					previous = loc;
//				}
//			}
//		}
		addListeners(drawFeature);
		//drawFeature.draw();
		return drawFeature;
	}

	@Override
	public List<DrawFeature> drawFeatures(List<Feature> features)
	throws Exception {
		stacker.init(features);
		int levelHeight = settings.getFeatureHeight() + settings.getGapWidth();
		int totalHeight = stacker.getLevelCount() * levelHeight + settings.getGapWidth();
		drawPane.setHeight(totalHeight);
		List<DrawFeature> drawFeatures = new LinkedList<DrawFeature>();
		for (Feature feature : features) {
			if (scaler.withinRegion(feature)) {
				int level = stacker.getLevel(feature);	
				int top = 0;
				top = settings.getGapWidth() + level * levelHeight;
//				if (feature.getParent()!=null){
//					top = settings.getGapWidth() + level * levelHeight;
//				}
//				else {
//					top = settings.getGapWidth() + level * (7 + settings.getGapWidth());//for bed and bigbed features
//				}
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
	public void onFeatureSelect(FeatureSelection selection, Feature detailFeature) {
		selection.getDrawFeature().setFeature(detailFeature);

	}

}
