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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.genohm.viewsGWT.client.eventbus.FeatureSelection;
import com.genohm.viewsGWT.client.track.Track;
import com.genohm.viewsGWT.client.util.HorizontalScaler;
import com.genohm.viewsGWT.client.util.VerticalStacker;
import com.genohm.viewsGWT.shared.BioTools;
import com.genohm.viewsGWT.shared.Location;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.genohm.viewsGWT.shared.data.feature.PairedEndFeature;
import com.genohm.viewsGWT.shared.renderer.RendererSettings;
import com.smartgwt.client.widgets.drawing.DrawLabel;
import com.smartgwt.client.widgets.drawing.DrawLine;
import com.smartgwt.client.widgets.drawing.DrawPane;
import com.smartgwt.client.widgets.drawing.DrawRect;
import com.smartgwt.client.widgets.drawing.Point;

public class PairedEndRenderer extends FeatureRenderer {
	protected final static int MIN_WIDTH_FOR_LABEL = 20;
	protected final static int MIN_FEATURE_HEIGHT = 1;
	protected static int MIN_WIDTH_FOR_ENDS = 10;
	protected static int MIN_WIDTH_FOR_NUCLEOTIDECOLOR = 1;
	protected static int MIN_WIDTH_FOR_NUCLEOTIDELABEL = 10;

	protected VerticalStacker stacker = new VerticalStacker();
	protected static Map<String,String> nucleotideColors = new HashMap<String, String>();
	static {
		nucleotideColors.put("a", "#44ff44");
		nucleotideColors.put("A", "#44ff44");
		nucleotideColors.put("c", "#4444ff");
		nucleotideColors.put("C", "#4444ff");
		nucleotideColors.put("g", "#444444");
		nucleotideColors.put("G", "#444444");
		nucleotideColors.put("t", "#ff4444");
		nucleotideColors.put("T", "#ff4444");
		nucleotideColors.put("X", "#ffffff");
		nucleotideColors.put("N", "#ffffff");
	}

	public PairedEndRenderer(DrawPane drawPane, HorizontalScaler scaler,
			Track track, RendererSettings settings) {
		super(drawPane, scaler, track, settings);
	}

	@Override
	public DrawFeature createDrawFeature(final Feature generic, final int height, final int top, DrawFeature drawParentFeature) throws Exception {
		if (!(generic instanceof PairedEndFeature)) throw new Exception("Can only render paired end features");
		
		PairedEndFeature feature = (PairedEndFeature) generic; 
		
		final DrawFeature drawFeature = new DrawFeature(feature);
		drawFeature.setDrawPane(drawPane);

		DrawRect rect = new DrawRect();
		rect.setLeft(scaler.getLeft(feature));
		rect.setWidth(scaler.getWidth(feature));
		rect.setTop(top);
		rect.setHeight(height);
		rect.setLineWidth(1);
		drawFeature.addVisualItem(rect);

		Boolean direction = scaler.getDirection(feature);
		
		int leftLeft;
		int leftRight;
		int widthLeft;
		int widthRight;
		String sequenceLeft = null;
		String sequenceRight = null;

		if (direction) {
			leftLeft = scaler.getLeft(feature.getLoc().get(0)); // first location = forward part = left on screen
			leftRight = scaler.getLeft(feature.getLoc().get(1));
			widthLeft = scaler.getWidth(feature.getLoc().get(0));
			widthRight = scaler.getWidth(feature.getLoc().get(1));
			sequenceLeft = feature.getSequenceForward();
			sequenceRight = feature.getSequenceBackward();
		} else {
			leftLeft = scaler.getLeft(feature.getLoc().get(1)); // second location = backward part = left on screen
			leftRight = scaler.getLeft(feature.getLoc().get(0)); 
			widthLeft = scaler.getWidth(feature.getLoc().get(1));
			widthRight = scaler.getWidth(feature.getLoc().get(0));
			sequenceLeft = BioTools.reverseComplement(feature.getSequenceBackward());
			sequenceRight = BioTools.reverseComplement(feature.getSequenceForward());
		}
		
		if (scaler.getWidth(feature) > MIN_WIDTH_FOR_ENDS) {

			float widthPerBase = scaler.getBaseWidth();

			DrawRect leftRead = new DrawRect();
			leftRead.setLeft(leftLeft);
			leftRead.setWidth(widthLeft);
			leftRead.setHeight(height);
			leftRead.setTop(top);
			leftRead.setLineWidth(0);
			leftRead.setFillColor("#DDDDDD");
			drawFeature.addVisualItem(leftRead);
			leftRead.draw(); // draw before nucleotides

			DrawRect rightRead = new DrawRect();
			rightRead.setLeft(leftRight);
			rightRead.setWidth(widthRight);
			rightRead.setHeight(height);
			rightRead.setTop(top);
			rightRead.setLineWidth(0);
			rightRead.setFillColor("#DDDDDD");
			drawFeature.addVisualItem(rightRead);
			rightRead.draw();

			if (widthPerBase > MIN_WIDTH_FOR_NUCLEOTIDECOLOR && sequenceLeft != null && sequenceRight != null) {

				for (int i = 0; i < sequenceLeft.length(); i++) {

					String nuc = sequenceLeft.substring(i, i+1);

					DrawRect nucleotide = new DrawRect();
					nucleotide.setFillColor(nucleotideColors.get(nuc));
					nucleotide.setLeft(leftLeft + Math.round(i * widthPerBase));
					nucleotide.setWidth((int) Math.floor(widthPerBase));
					nucleotide.setTop(top);			
					nucleotide.setHeight(height);
					drawFeature.addVisualItem(nucleotide);
					if (widthPerBase > MIN_WIDTH_FOR_NUCLEOTIDELABEL) {
						DrawLabel nucleotideLabel = new DrawLabel();
						drawFeature.addVisualItem(nucleotideLabel);
						nucleotideLabel.setLeft(leftLeft + Math.round(i * widthPerBase));
						nucleotideLabel.setTop(top);
						nucleotideLabel.setContents(nuc);
						nucleotideLabel.draw(); //must draw in order to get coordinates right
						int[] coords = nucleotideLabel.getCenter();
						int[] target = nucleotide.getCenter();
						nucleotideLabel.moveBy(target[0]-coords[0], target[1]-coords[1]);
					}

				}

				for (int i = 0; i < sequenceRight.length(); i++) {

					String nuc = sequenceRight.substring(i, i+1);

					DrawRect nucleotide = new DrawRect();
					nucleotide.setLineWidth(0);
					nucleotide.setFillColor(nucleotideColors.get(nuc));
					nucleotide.setLeft(leftRight + Math.round(i * widthPerBase));
					nucleotide.setWidth((int) Math.floor(widthPerBase));
					nucleotide.setTop(top);			
					nucleotide.setHeight(height);
					drawFeature.addVisualItem(nucleotide);
					if (widthPerBase > MIN_WIDTH_FOR_NUCLEOTIDELABEL) {
						DrawLabel nucleotideLabel = new DrawLabel();
						drawFeature.addVisualItem(nucleotideLabel);
						nucleotideLabel.setLeft(leftRight + Math.round(i * widthPerBase));
						nucleotideLabel.setTop(top);
						nucleotideLabel.setContents(nuc);
						nucleotideLabel.draw(); //must draw in order to get coordinates right
						int[] coords = nucleotideLabel.getCenter();
						int[] target = nucleotide.getCenter();
						nucleotideLabel.moveBy(target[0]-coords[0], target[1]-coords[1]);
					}

				}
			}

		}

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
				int	top = settings.getGapWidth() + level * levelHeight;
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
	public void onFeatureSelect(FeatureSelection selection,
			Feature detailFeature) {
		selection.getDrawFeature().setFeature(detailFeature);
		//TODO: complete
	}

}
