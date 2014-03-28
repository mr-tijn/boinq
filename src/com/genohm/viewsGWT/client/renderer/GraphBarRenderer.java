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

import com.genohm.viewsGWT.client.eventbus.FeatureSelection;
import com.genohm.viewsGWT.client.track.Track;
import com.genohm.viewsGWT.client.util.GroupData;
import com.genohm.viewsGWT.client.util.HorizontalScaler;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.genohm.viewsGWT.shared.data.feature.GraphFeature;
import com.genohm.viewsGWT.shared.renderer.RendererGraphSettings;
import com.genohm.viewsGWT.shared.renderer.RendererSettings;
import com.smartgwt.client.widgets.drawing.DrawLabel;
import com.smartgwt.client.widgets.drawing.DrawLine;
import com.smartgwt.client.widgets.drawing.DrawPane;
import com.smartgwt.client.widgets.drawing.DrawRect;
import com.smartgwt.client.widgets.drawing.Point;

public class GraphBarRenderer extends FeatureRenderer{
	
	GraphBarRenderer(DrawPane drawPane, HorizontalScaler scaler, Track track,
			RendererSettings settings) {
		super(drawPane, scaler, track, settings);
		// TODO Auto-generated constructor stub
	}

	protected int defaultGraphTop = 10;
	protected RendererSettings settings = new RendererSettings();
	protected RendererGraphSettings graphSettings = new RendererGraphSettings();
	GraphCurveRenderer graphRenderer = new GraphCurveRenderer(drawPane, scaler, track, settings);
	@Override
	public List<DrawFeature> drawFeatures(List<Feature> features)
			throws Exception {
		List<DrawFeature> drawFeatures = new LinkedList<DrawFeature>();
		for (Feature feature:features){
			if (scaler.withinRegion(feature)) {
				DrawFeature drawFeature = createDrawFeature(feature,0,0,null);
				if (drawFeature != null) {
					drawFeature.draw();
					drawFeatures.add(drawFeature);
				}
			}
		}
		return drawFeatures;
	}
	
	@Override
   	public DrawFeature createDrawFeature(Feature feature, int height, int top, DrawFeature parentFeature)
			throws Exception {

		DrawFeature drawFeature = new DrawFeature(feature);
		drawFeature.setDrawPane(drawPane);
		List<GroupData> widthList=graphRenderer.getGroupWidth((GraphFeature)feature);
		int graphHeight = graphSettings.getGraphHeight(); 
		int maxTop = graphHeight + defaultGraphTop;
		float max = ((GraphFeature) feature).getMaxData();
		for (int i=0; i<widthList.size();i++){
			int theHeight = ((graphHeight*(int)widthList.get(i).getVar())/(int)max-1);
			GroupData group =  widthList.get(i);
			int theTop = maxTop-theHeight;
			DrawRect recto = new DrawRect(); 
			recto.setLeft(scaler.getLeft(group));
			recto.setHeight(theHeight);
			recto.setTop(theTop); 
			recto.setWidth(scaler.getWidth(group));
			recto.setLineWidth(graphSettings.getGraphLineWidth());
			recto.setLineColor(graphSettings.getFrameworkColor());
			recto.setFillColor(graphSettings.getGraphColor());
			//recto.setDrawGroup(drawFeature);
			drawFeature.addVisualItem(recto);
			recto.draw();
		}
		if (graphSettings.getShowGrid()){
		 
 			Point firstPoint = new Point(scaler.getLeft(feature),maxTop-(graphHeight/3));			Point secondPoint = new Point(scaler.getRight(feature),maxTop-(graphHeight/3));
			Point thirdPoint  = new Point(scaler.getLeft(feature),maxTop-(2*graphHeight/3));			Point fourthPoint 	= new Point(scaler.getRight(feature),maxTop-(2*graphHeight/3));

			
 			DrawLabel firstValue = new DrawLabel();
 			firstValue.setDrawPane(drawPane);
  			//firstValue.setDrawGroup(drawFeature);
 			drawFeature.addVisualItem(firstValue);
 			firstValue.setLeft(scaler.getScreenRegion().getVisibleStart());
 			firstValue.setTop(maxTop-10);
  			firstValue.setContents("0");
  			firstValue.setLineColor("black");
 			firstValue.setFontSize(10);
  			
			DrawLine firstLine = new DrawLine();
			firstLine.setDrawPane(drawPane);
			firstLine.setStartPoint(firstPoint);
			firstLine.setEndPoint(secondPoint);
			firstLine.setLineWidth(1);
			firstLine.setLineColor("gray");
			//firstLine.setDrawGroup(drawFeature);
			drawFeature.addVisualItem(firstLine);
			firstLine.draw();
			
			DrawLabel secondValue = new DrawLabel();
			secondValue.setDrawPane(drawPane);
			//secondValue.setDrawGroup(drawFeature);
			drawFeature.addVisualItem(secondValue);
			secondValue.setLeft(scaler.getScreenRegion().getVisibleStart());
 			secondValue.setTop(maxTop-(graphHeight/3)-10);
  			secondValue.setContents(Float.toString(max/3));
 			secondValue.setLineColor("black");
 			secondValue.setFontSize(10);
 			
			DrawLine secondLine = new DrawLine();
			secondLine.setDrawPane(drawPane);
			secondLine.setStartPoint(thirdPoint); 
			secondLine.setEndPoint(fourthPoint);
			secondLine.setLineWidth(1);
			secondLine.setLineColor("gray");
			//secondLine.setDrawGroup(drawFeature);
			drawFeature.addVisualItem(secondLine);
			secondLine.draw();
			
			DrawLabel thirdValue = new DrawLabel();
			thirdValue.setDrawPane(drawPane);
			//thirdValue.setDrawGroup(drawFeature);
			drawFeature.addVisualItem(thirdValue);
			thirdValue.setLeft(scaler.getScreenRegion().getVisibleStart());
			thirdValue.setTop(maxTop-((2*graphHeight)/3)-10);
  			thirdValue.setContents(Float.toString(2*max/3));
			thirdValue.setLineColor("black");
			thirdValue.setFontSize(10);
			
			DrawLabel fourthValue = new DrawLabel();
			fourthValue.setDrawPane(drawPane);
			//fourthValue.setDrawGroup(drawFeature);
			drawFeature.addVisualItem(fourthValue);
			fourthValue.setLeft(scaler.getScreenRegion().getVisibleStart());
			fourthValue.setTop(maxTop-graphHeight-10);
 			fourthValue.setContents(Float.toString(max));
			fourthValue.setLineColor("black");
			fourthValue.setFontSize(10);
 			
			if (scaler.getRight(feature)>scaler.getScreenRegion().getVisibleStart()){
				firstValue.draw();
				secondValue.draw();
				thirdValue.draw();
				fourthValue.draw();
				
			}

			
		}
		DrawRect rect = new DrawRect();
		rect.setLeft(scaler.getLeft(widthList.get(0)));
		rect.setHeight(graphHeight);
		rect.setTop(defaultGraphTop);
		rect.setWidth(scaler.getRight(widthList.get(widthList.size()-1)) - scaler.getLeft(widthList.get(0))+2);
		rect.setLineWidth(1);
		rect.setLineColor("red");
		//rect.setDrawGroup(drawFeature);
		drawFeature.addVisualItem(rect);
		rect.draw();

		return drawFeature;
	}

	@Override
	public void onFeatureSelect(FeatureSelection selection, Feature fullFeature) {
		// TODO Auto-generated method stub
		
	}

 


}