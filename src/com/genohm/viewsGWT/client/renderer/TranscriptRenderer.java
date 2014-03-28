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

import com.genohm.viewsGWT.client.track.Track;
import com.genohm.viewsGWT.client.util.HorizontalScaler;
import com.genohm.viewsGWT.shared.Location;
import com.genohm.viewsGWT.shared.data.feature.Exon;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.genohm.viewsGWT.shared.data.feature.Transcript;
import com.genohm.viewsGWT.shared.renderer.RendererSettings;
import com.smartgwt.client.widgets.drawing.DrawLabel;
import com.smartgwt.client.widgets.drawing.DrawLine;
import com.smartgwt.client.widgets.drawing.DrawPane;
import com.smartgwt.client.widgets.drawing.DrawPath;
import com.smartgwt.client.widgets.drawing.DrawRect;
import com.smartgwt.client.widgets.drawing.Point;

public class TranscriptRenderer extends DirectionalBlockRenderer {

	private float MIN_WIDTH_FOR_AMINO_ACID = 5;
	private float MIN_WIDTH_FOR_LABEL = 10;

	
	public static Map<String, String> aaColor = new HashMap<String, String>() {
		{
			put("A","#8B0000");put("R","#B0C4DE");put("N","#4B0082");
			put("D","#7788FF");put("C","#4682B4");put("E","#778899");
			put("Q","#5F9EA0");put("G","#7788FF");put("H","#2F4F4F");
			put("I","#20B2AA");put("L","#8FBC8F");put("K","#00FA9A");
			put("M","#00FF00");put("F","#A52A2A");put("P","#C0C0C0");
			put("S","#CD5C5C");put("T","#00008B");put("W","#DC143C");
			put("Y","#4682B4");put("V","#FFA500");put("_","#FF0000");
		}
	};

	
	public TranscriptRenderer(DrawPane drawPane, HorizontalScaler scaler, Track track, RendererSettings settings) {
		super(drawPane, scaler, track, settings);
	}

	
	private void addPart(DrawFeature target, Location loc, int height, int top, Boolean translated, Location previous, Boolean hasNext, String protein, int phase) {
		if (!scaler.withinRegion(loc)) return;
		Boolean direction = scaler.getDirection(loc);
		int left = scaler.getLeft(loc);
		int right = scaler.getRight(loc);
		String color = translated ? "green" : "red";
		if (previous != null) {
			int prevLeft = scaler.getLeft(previous);
			int prevRight = scaler.getRight(previous);
			if (prevLeft < left) {
				// drawing left to right
				if (left - prevRight > 3) {
					int middle = (left + prevRight) / 2;
					DrawPath arc = new DrawPath();
					arc.setPoints(new Point(prevRight,top + height / 2), new Point(middle,top), new Point(left,top + height / 2));
					arc.setLineColor("black");
					arc.setLineWidth(1);
					target.addVisualItem(arc);
				}
			} else {
				if (prevLeft - right > 3) {
					int middle = (right + prevLeft) / 2;
					DrawPath arc = new DrawPath();
					arc.setPoints(new Point(right,top + height / 2), new Point(middle,top), new Point(prevLeft,top + height / 2));
					arc.setLineColor("black");
					arc.setLineWidth(1);
					target.addVisualItem(arc);
				}
			}
		}
		if (hasNext) {
			DrawRect rect = new DrawRect();
			rect.setLeft(left);
			rect.setTop(top);
			rect.setLineWidth(settings.getLineWidth());
			rect.setLineColor(settings.getLineColor());				
			rect.setWidth(scaler.getWidth(loc));
			rect.setHeight(height);
			rect.setFillColor(color);
			target.addVisualItem(rect);
		} else {
			DrawPointedRect pointedRect = new DrawPointedRect();
			pointedRect.setLeft(left);
			pointedRect.setTop(top);
			pointedRect.setLineWidth(settings.getLineWidth());
			pointedRect.setLineColor(settings.getLineColor());				
			pointedRect.setWidth(scaler.getWidth(loc));
			pointedRect.setHeight(height);
			pointedRect.setDirection(direction);
			pointedRect.setFillColor(color);
			target.addVisualItem(pointedRect);
		}
		if (protein != null) {
			if (scaler.getBaseWidth()*3 <= MIN_WIDTH_FOR_AMINO_ACID ) return;
			Location aaLoc = loc.getDuplicate();
			if (loc.getStrand()) {
				aaLoc.setStart(loc.getStart());
				aaLoc.setEnd(loc.getStart() + 2 - phase);
			} else {
				aaLoc.setStart(loc.getEnd() - 2 + phase);
				aaLoc.setEnd(loc.getEnd());
			}
			int proteinLength = protein.length();
			for (int i = 0; i < proteinLength; i++) {
				DrawRect aa = new DrawRect();
				int aaLeft = scaler.getLeft(aaLoc);
				int aaWidth = scaler.getWidth(aaLoc);
				String aaLetter = null;
				aaLetter = protein.substring(i, i+1);
				aa.setLeft(aaLeft);
				aa.setWidth(aaWidth);
				aa.setTop(top);
				aa.setHeight(height);
				aa.setLineWidth(1);
				aa.setLineColor("black");
				aa.setFillColor(aaColor.get(aaLetter));
				target.addVisualItem(aa);
				if (loc.getStrand()) {
					aaLoc.setStart(aaLoc.getEnd() + 1);
					aaLoc.setEnd(Math.min(loc.getEnd(), aaLoc.getEnd()+3));
				} else {
					aaLoc.setEnd(aaLoc.getStart() - 1);
					aaLoc.setStart(Math.max(aaLoc.getStart() - 3, loc.getStart()));
				}
				//target.addVisualItem(aa);
				if (scaler.getBaseWidth() > MIN_WIDTH_FOR_LABEL ) {
					DrawLabel nucleotideLabel = new DrawLabel();
					nucleotideLabel.setFontWeight("normal");
					nucleotideLabel.setLeft(aaLeft+2);
					nucleotideLabel.setTop(top+2);
					nucleotideLabel.setContents(aaLetter);
					target.addVisualItem(nucleotideLabel);
				}

			}
		}
	}
	
	@Override
	public DrawFeature createDrawFeature(Feature feature, int height, int top, DrawFeature parent) throws Exception {
		DrawFeature drawFeature = super.createDrawFeature(feature, height, top, null);
		Transcript transcript = (Transcript) feature;
		Boolean strand = feature.getLoc().get(0).getStrand();
		List<Exon> exons = transcript.getExons();
		Iterator<Exon> exonIterator = exons.iterator();
		Location previous = null;
		if (transcript.getTranslationStart() == null) {
			//normal rendering
			while (exonIterator.hasNext()) {
				Location loc = exonIterator.next().getLoc().get(0);
				addPart(drawFeature, loc, height, top, Boolean.FALSE, previous, exonIterator.hasNext(), null, 0);
				previous = loc;
			}
		}
		else {
			String protein = null;
			int phase = 0;
			if (strand) {
				// positive strand
				while (exonIterator.hasNext()) {
					Exon exon = exonIterator.next();
					protein = transcript.getProtein(exon);
					phase = transcript.getPhase(exon);
					Location currentLoc = exon.getLoc().get(0);
					if (currentLoc.getEnd() < transcript.getTranslationStart() || currentLoc.getStart() > transcript.getTranslationEnd()) {
						// completely utr
						addPart(drawFeature, currentLoc, height, top, Boolean.FALSE, previous, exonIterator.hasNext(), null, 0);
						previous = currentLoc.getDuplicate();
					} else if (currentLoc.getStart() > transcript.getTranslationStart() && currentLoc.getEnd() < transcript.getTranslationEnd()) {
						// completely tr
						addPart(drawFeature, currentLoc, height, top, Boolean.TRUE, previous, exonIterator.hasNext(), protein, phase);
						previous = currentLoc.getDuplicate();
					} else {
						Boolean chopEnd = false;
						if (currentLoc.getEnd() > transcript.getTranslationEnd()) chopEnd = true;

						if (currentLoc.getStart() < transcript.getTranslationStart()) {
							Location utr = currentLoc.getDuplicate();
							utr.setEnd(transcript.getTranslationStart()-1);
							addPart(drawFeature, utr, height, top, Boolean.FALSE, previous, true, null, 0);
							previous = utr;
						}
						Location tr = currentLoc.getDuplicate();
						tr.setStart(Math.max(currentLoc.getStart(), transcript.getTranslationStart()));
						tr.setEnd(Math.min(currentLoc.getEnd(), transcript.getTranslationEnd()));
						addPart(drawFeature, tr, height, top, Boolean.TRUE, previous, exonIterator.hasNext() || chopEnd, protein, phase);
						previous = tr;
						if (chopEnd) {
							Location utr = currentLoc.getDuplicate();
							utr.setStart(transcript.getTranslationEnd()+1);
							addPart(drawFeature, utr, height, top, Boolean.FALSE, null, exonIterator.hasNext(), null, 0);	
							previous = utr;
						}
						
					}
				} 
			} else {
				while (exonIterator.hasNext()) {
					Exon exon = exonIterator.next();
					if (scaler.getBaseWidth()*3 > MIN_WIDTH_FOR_AMINO_ACID ) {
						protein = transcript.getProtein(exon);
						phase = transcript.getPhase(exon);
					}
					Location currentLoc = exon.getLoc().get(0);
					if (currentLoc.getStart() > transcript.getTranslationStart() || currentLoc.getEnd() < transcript.getTranslationEnd()) {
						// completely utr
						addPart(drawFeature, currentLoc, height, top, Boolean.FALSE, previous, exonIterator.hasNext(), null, 0);
						previous = currentLoc.getDuplicate();
					} else if (currentLoc.getEnd() < transcript.getTranslationStart() && currentLoc.getStart() > transcript.getTranslationEnd()) {
						// completely tr
						addPart(drawFeature, currentLoc, height, top, Boolean.TRUE, previous, exonIterator.hasNext(), protein, phase);
						previous = currentLoc.getDuplicate();
					} else {
						Boolean chopEnd = false;
						if (currentLoc.getStart() < transcript.getTranslationEnd()) chopEnd = true;
						if (currentLoc.getEnd() > transcript.getTranslationStart()) {
							Location utr = currentLoc.getDuplicate();
							utr.setStart(transcript.getTranslationStart()+1);
							addPart(drawFeature, utr, height, top, Boolean.FALSE, previous, true, null, 0);
							previous = utr;
						}
						Location tr = currentLoc.getDuplicate();
						tr.setStart(Math.max(currentLoc.getStart(), transcript.getTranslationEnd()));
						tr.setEnd(Math.min(currentLoc.getEnd(), transcript.getTranslationStart()));
						addPart(drawFeature, tr, height, top, Boolean.TRUE, previous, chopEnd || exonIterator.hasNext(), protein, phase);
						previous = tr;
						if (chopEnd) {
							Location utr = currentLoc.getDuplicate();
							utr.setEnd(transcript.getTranslationEnd()-1);
							addPart(drawFeature, utr, height, top, Boolean.FALSE, null, exonIterator.hasNext(), null, 0);	
							previous = utr;
						}
						
					}
				} 
					
			}
		}
		addListeners(drawFeature);
		return drawFeature;
	}

	
}
