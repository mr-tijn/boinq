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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.genohm.viewsGWT.client.eventbus.FeatureSelection;
import com.genohm.viewsGWT.client.track.Track;
import com.genohm.viewsGWT.client.util.HorizontalScaler;
import com.genohm.viewsGWT.client.util.VerticalStacker;
import com.genohm.viewsGWT.shared.Location;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.genohm.viewsGWT.shared.data.feature.MappedReadFeature;
import com.genohm.viewsGWT.shared.renderer.RendererSettings;
import com.smartgwt.client.widgets.drawing.DrawImage;
import com.smartgwt.client.widgets.drawing.DrawLabel;
import com.smartgwt.client.widgets.drawing.DrawPane;
import com.smartgwt.client.widgets.drawing.DrawRect;

public class MappedReadRenderer extends FeatureRenderer {
	
	protected static int MIN_WIDTH_FOR_LABEL = 10;
	protected static int MIN_WIDTH_FOR_NUCLEOTIDECOLOR = 5;
	protected static int MIN_WIDTH_FOR_NUCLEOTIDELABEL = 10;
	private static final int FEATURE_GAP = 2;

	protected VerticalStacker stacker = new VerticalStacker();
	protected static Map<String,String> nucleotideColors = new HashMap<String, String>();
	static {
		//A 148 135 255 x9487ff
		//C 127 208 142 x7fd08e
		//G 255 220 132 xffdc84
		//T 255 135 137 xff8789
		nucleotideColors.put("a", "#9487ff");
		nucleotideColors.put("A", "#9487ff");
		nucleotideColors.put("c", "#7fd08e");
		nucleotideColors.put("C", "#7fd08e");
		nucleotideColors.put("g", "#ffdc84");
		nucleotideColors.put("G", "#ffdc84");
		nucleotideColors.put("t", "#ff8789");
		nucleotideColors.put("T", "#ff8789");
		nucleotideColors.put("X", "#ffffff");
		nucleotideColors.put("N", "#ffffff");
	}

	
	MappedReadRenderer(DrawPane drawPane, HorizontalScaler scaler, Track track, RendererSettings settings) {
		super(drawPane, scaler, track, settings);
	}
	
	@Override
	public List<DrawFeature> drawFeatures(List<Feature> features) throws Exception {
		List<DrawFeature> drawFeatures = new LinkedList<DrawFeature>();
		stacker.init(features);
		for (Feature feature: (List<Feature>) features) {
			if (scaler.withinRegion(feature)) {
				int level = stacker.getLevel(feature);
				int top = level*(settings.getFeatureHeight() + FEATURE_GAP);
				int height = settings.getFeatureHeight();
				DrawFeature drawFeature = createDrawFeature(feature,height,top);
				drawFeatures.add(drawFeature);
				drawFeature.draw();
			}
		}
		return drawFeatures;
	}

	@Override
	public DrawFeature createDrawFeature(Feature feature, int height, int top, DrawFeature parentFeature) throws Exception {
 		//stacker.init((List<Feature>) feature.getSubFeatures());
		Boolean direction = scaler.getDirection(feature);
		MappedReadFeature mappedReadFeature = (MappedReadFeature) feature;
		// assume single location
		Location loc = feature.getLoc().get(0);
		int left = scaler.getLeft(loc);
 		int width = scaler.getWidth(feature);
		float widthPerBase = scaler.getBaseWidth();

		Long visibleStart = scaler.getVisibleStart(feature);
		Integer visibleLength = scaler.getVisibleLength(feature);
		DrawFeature drawFeature = new DrawFeature(feature);
		drawFeature.setDrawPane(drawPane);
		DrawRect rect = new DrawRect();
		rect.setLeft(scaler.getLeft(feature));
		rect.setTop(0);
		rect.setWidth(scaler.getWidth(feature));
		rect.setHeight((Integer) settings.getFeatureHeight());
		rect.setWidth(settings.getLineWidth());
 		//rect.setDrawGroup(drawFeature);
		drawFeature.addVisualItem(rect);
		//rect.draw();
		String sequence;
		if (scaler.getDirection(feature)) {
			sequence = mappedReadFeature.getDnaString();
		} else {
			sequence = mappedReadFeature.getDnaStringComplement();
		}
		if (widthPerBase > MIN_WIDTH_FOR_NUCLEOTIDECOLOR && sequence != null && sequence.length() > 0) {
			int offset = (int) (visibleStart - feature.getLoc().get(0).getStart());
			int length = Math.min(sequence.length(), visibleLength);
			char nucs[] = new char[length];
			sequence.getChars(offset, offset+length-1, nucs, 0);
			for (int i = 0; i < length; i++) {
				
				
//				DrawImage nucleotideRect = new DrawImage();
//				switch (nucs[i]) {
//				case 'a' :
//				case 'A' :
//					nucleotideRect.setSrc("a.png");
//					break;
//				case 'c' :
//				case 'C' :
//					nucleotideRect.setSrc("c.png");
//					break;
//				case 'g' :
//				case 'G' :
//					nucleotideRect.setSrc("g.png");
//					break;
//				case 't' :
//				case 'T' :
//					nucleotideRect.setSrc("t.png");
//					break;
//				}
//				if (direction) {
//					nucleotideRect.setLeft(left + Math.round(i*widthPerBase));
//				} else {
//					nucleotideRect.setLeft(left + Math.round((length-i-1)*widthPerBase));
//				}
//				nucleotideRect.setWidth((int) Math.floor(widthPerBase));
//				nucleotideRect.setTop(top);			
//				nucleotideRect.setHeight(height);
//				drawFeature.addVisualItem(nucleotideRect);
				
				String nuc = sequence.substring(offset + i, offset + i + 1);
				DrawRect nucleotideRect = new DrawRect();
				nucleotideRect.setFillColor(nucleotideColors.get(nuc));
				nucleotideRect.setLineWidth(1);
				if (direction) {
					nucleotideRect.setLeft(left + Math.round(i*widthPerBase));
				} else {
					nucleotideRect.setLeft(left + Math.round((length-i-1)*widthPerBase));
				}
				nucleotideRect.setWidth((int) Math.floor(widthPerBase));
				nucleotideRect.setTop(top);			
				nucleotideRect.setHeight(height);
				drawFeature.addVisualItem(nucleotideRect);
				//nucleotideRect.draw();
				if (widthPerBase > MIN_WIDTH_FOR_NUCLEOTIDELABEL) {
					DrawLabel nucleotideLabel = new DrawLabel();
					if (direction) {
						nucleotideLabel.setLeft(left + Math.round(i*widthPerBase)+2);
					} else {
						nucleotideLabel.setLeft(left + Math.round((length-i-1)*widthPerBase)+2);
					}
					nucleotideLabel.setTop(top+2);
					nucleotideLabel.setContents(nuc);
					nucleotideLabel.setFontWeight("normal");
					drawFeature.addVisualItem(nucleotideLabel);
//					nucleotideLabel.draw(); //must draw in order to get coordinates right
//					int[] coords = nucleotideLabel.getCenter();
//					int[] target = nucleotideRect.getCenter();
//					nucleotideLabel.moveBy(target[0]-coords[0], target[1]-coords[1]);
				}
			}
		} else if (width > MIN_WIDTH_FOR_LABEL) {
			DrawLabel label = new DrawLabel();
			label.setLeft(left);
			label.setTop(top);
			label.setContents(feature.getDescription());
			//label.setDrawGroup(drawFeature);
			drawFeature.addVisualItem(label);
			//label.draw();
		}

		
		//addListeners(drawFeature);
		//drawFeature.draw();
		return drawFeature;
	}
	
	@Override
	public void onFeatureSelect(FeatureSelection selection, Feature feature) {
	}

	
}
