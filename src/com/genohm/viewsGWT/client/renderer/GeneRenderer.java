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
import com.genohm.viewsGWT.shared.data.feature.Exon;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.genohm.viewsGWT.shared.data.feature.Gene;
import com.genohm.viewsGWT.shared.data.feature.Transcript;
import com.genohm.viewsGWT.shared.renderer.RendererSettings;
import com.smartgwt.client.widgets.drawing.DrawLine;
import com.smartgwt.client.widgets.drawing.DrawPane;
import com.smartgwt.client.widgets.drawing.DrawPath;
import com.smartgwt.client.widgets.drawing.DrawRect;
import com.smartgwt.client.widgets.drawing.Point;


public class GeneRenderer extends DirectionalBlockRenderer {
//	String rendererName;
//	int featureheight =10;
//	String featureColor = "green";
//	String lineColor = "red";
//	int linewidth=1;
//	int gapWidth = 0;
//	protected RendererSettings settings = new RendererSettings();

	public GeneRenderer(DrawPane drawPane, HorizontalScaler scaler, Track track, RendererSettings settings) {
		super(drawPane, scaler, track, settings);
	}



	@Override
	public DrawFeature createDrawFeature(Feature feature, int height, int top, DrawFeature parent) throws Exception {
		DrawFeature drawFeature = super.createDrawFeature(feature, height, top, null);
 		Gene gene = (Gene) feature;
		List<Transcript> transcripts = gene.getTranscripts();
		if (transcripts != null && transcripts.size() > 0) {
			int transcriptHeight = height/transcripts.size();
			int transcriptCount = 0;
			for (Transcript transcript: transcripts) {
				List<Exon> exons = transcript.getExons();
				
				Iterator<Exon> exonIterator = exons.iterator();
				Exon previous = null;
				while (exonIterator.hasNext()) {
					int transcriptTop = top + (transcriptCount * height) / transcripts.size();
					Exon exon = exonIterator.next();
					int left = scaler.getLeft(exon);
					int width = scaler.getWidth(exon);
					Boolean direction = scaler.getDirection(exon);
					DrawRect rect = new DrawRect();
					rect.setFillColor("black");
					rect.setLineWidth(0);
					rect.setLeft(left);
					rect.setTop(transcriptTop); 		
					rect.setWidth(width);
					rect.setHeight(transcriptHeight);
					drawFeature.addVisualItem(rect);
					if (previous != null) {
						int start;
						int end;
						if (direction) {
							start = scaler.getRight(previous);
							end = left;
						} else {
							start = scaler.getLeft(previous);
							end = left+width;
						}
						DrawLine arc = new DrawLine();
						arc.setStartPoint(new Point(start,transcriptTop+transcriptHeight/2));
						arc.setEndPoint(new Point(end,transcriptTop+transcriptHeight/2));
						arc.setLineColor("black");
						arc.setLineWidth(1);
						drawFeature.addVisualItem(arc);
					}
					previous = exon;
				}
				transcriptCount++;
			}
		}
		return drawFeature;
	}

	
	@Override
	public void onFeatureSelect(FeatureSelection selection, Feature feature) {
	}


	




}
