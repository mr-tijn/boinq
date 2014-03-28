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

import org.apache.avro.generic.GenericRecord;

import com.genohm.viewsGWT.client.dialog.GeneRecord;
import com.genohm.viewsGWT.client.eventbus.Event;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.client.eventbus.FeatureSelection;
import com.genohm.viewsGWT.client.eventbus.Subscription;
import com.genohm.viewsGWT.client.eventbus.TopicSubscriber;
import com.genohm.viewsGWT.client.track.DragMode;
import com.genohm.viewsGWT.client.util.HorizontalScaler;
import com.genohm.viewsGWT.client.util.ScreenRegion;
import com.genohm.viewsGWT.shared.ContigSet;
import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.Species;
import com.genohm.viewsGWT.shared.data.ensembl.EnsemblGene;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.google.gwt.core.client.GWT;

public class ViewPort {
	
	protected GenomicRegion mainGenomicRegion = new GenomicRegion();
	protected GenomicRegion contextGenomicRegion = new GenomicRegion();
	
	protected ScreenRegion mainScreenRegion = new ScreenRegion();
	protected ScreenRegion contextScreenRegion = new ScreenRegion();
	
	protected HorizontalScaler mainScaler = new HorizontalScaler(mainScreenRegion, mainGenomicRegion);
	protected HorizontalScaler contextScaler = new HorizontalScaler(contextScreenRegion, contextGenomicRegion);
	
	protected DragMode dragMode = DragMode.ZOOM;
	
	protected Feature focusedFeature = null;
	protected ContigSet contigs = null;
	
	public ViewPort() {
		super();
		final ViewPort thisViewPort = this;
		EventBus.subscribe(EventbusTopic.FEATURE_SELECTED, new TopicSubscriber<FeatureSelection>() {
			@Override
			public void onEvent(Subscription subscription, FeatureSelection selection) {
				focusedFeature = selection.getDrawFeature().getFeature();
				contextGenomicRegion.setVisibleStart(focusedFeature.getLoc().get(0).getStart() - focusedFeature.getLoc().get(0).getLength()/4);
				contextGenomicRegion.setVisibleEnd(focusedFeature.getLoc().get(0).getEnd() + focusedFeature.getLoc().get(0).getLength()/4);
				mainGenomicRegion.setVisibleStart(focusedFeature.getLoc().get(0).getStart());
				mainGenomicRegion.setVisibleEnd(focusedFeature.getLoc().get(0).getEnd());
				EventBus.publish(new Event(EventbusTopic.VIEWCONTEXT_CHANGED), thisViewPort);
				EventBus.publish(new Event(EventbusTopic.VIEWPORT_CHANGED), thisViewPort);
			}
			@Override
			public int getId() {
				return 0;
			}
		});
		EventBus.subscribe(EventbusTopic.GENE_SELECTED, new TopicSubscriber<GeneRecord>() {
			@Override
			public void onEvent(Subscription subscription, GeneRecord event) {
				Long length = event.getEndPos() - event.getStartPos();
				contextGenomicRegion.setVisibleStart(event.getStartPos() - length.intValue()/4);
				contextGenomicRegion.setVisibleEnd(event.getEndPos() + length.intValue()/4);
				contextGenomicRegion.setChromosome(event.getChromosome());
				mainGenomicRegion.setVisibleStart(event.getStartPos());
				mainGenomicRegion.setVisibleEnd(event.getEndPos());
				mainGenomicRegion.setChromosome(event.getChromosome());
				EventBus.publish(new Event(EventbusTopic.VIEWCONTEXT_CHANGED), thisViewPort);
				EventBus.publish(new Event(EventbusTopic.VIEWPORT_CHANGED), thisViewPort);
			}
			@Override
			public int getId() {
				return 0;
			}
		});
	}
	
	public Species getSpecies() {
		return mainGenomicRegion.getSpecies();
	}
	public void setSpecies(Species species) {
		if (species.equals(getSpecies())) return;
		focusedFeature = null;
		mainGenomicRegion.setSpecies(species);
		contextGenomicRegion.setSpecies(species);
		focusedFeature = null;
		EventBus.publish(new Event(EventbusTopic.VIEWPORT_CHANGED), this);
		EventBus.publish(new Event(EventbusTopic.VIEWCONTEXT_CHANGED), this);
	}
	public ContigSet getContigs() {
		return contigs;
	}
	public void setContigs(ContigSet contigs) {
		this.contigs = contigs;
	}
	public Long getGenomicStart() {
		return mainGenomicRegion.getVisibleStart();
	}
	public void setGenomicStart(Long start) {
		mainGenomicRegion.setVisibleStart(start);
		EventBus.publish(new Event(EventbusTopic.VIEWPORT_CHANGED), this);
		if (start < getGenomicContextStart()) {
			setGenomicContextStart(start);
		}
	}
	public Long getGenomicEnd() {
		return mainGenomicRegion.getVisibleEnd();
	}
	public void setGenomicEnd(Long end) {
		mainGenomicRegion.setVisibleEnd(end);
		EventBus.publish(new Event(EventbusTopic.VIEWPORT_CHANGED), this);
		if (end > getGenomicContextEnd()) {
			setGenomicContextEnd(end);
		}
	}
	public Long getGenomicContextStart() {
		return contextGenomicRegion.getVisibleStart();
	}
	public void setGenomicContextStart(Long genomicContextStart) {
		contextGenomicRegion.setVisibleStart(genomicContextStart);
		EventBus.publish(new Event(EventbusTopic.VIEWCONTEXT_CHANGED), this);
	}
	public Long getGenomicContextEnd() {
		return contextGenomicRegion.getVisibleEnd();
	}
	public void setGenomicContextEnd(Long genomicContextEnd) {
		contextGenomicRegion.setVisibleEnd(genomicContextEnd);
		EventBus.publish(new Event(EventbusTopic.VIEWCONTEXT_CHANGED), this);
	}
	public int getScreenStart() {
		return mainScreenRegion.getVisibleStart();
	}
	public void setScreenStart(int screenStart) {
		mainScreenRegion.setVisibleStart(screenStart);
		// should not publis a VIEWPORT_CHANGED event as this causes an infinite loop
		//EventBus.publish(new Event(EventbusTopic.VIEWPORT_CHANGED), this);
	}
	public int getScreenEnd() {
		return mainScreenRegion.getVisibleEnd();
	}
	public void setScreenEnd(int screenEnd) {
		mainScreenRegion.setVisibleEnd(screenEnd);
		// should not publis a VIEWPORT_CHANGED event as this causes an infinite loop
		//EventBus.publish(new Event(EventbusTopic.VIEWPORT_CHANGED), this);
	}
	public Boolean getStrand() {
		return mainGenomicRegion.getStrand();
	}
	public void setStrand(Boolean strand) {
		if (strand == getStrand()) return;
		mainGenomicRegion.setStrand(strand);
		contextGenomicRegion.setStrand(strand);
		EventBus.publish(new Event(EventbusTopic.VIEWPORT_CHANGED), this);
		EventBus.publish(new Event(EventbusTopic.VIEWCONTEXT_CHANGED), this);
	}
	public void setMainGenomicRegion(GenomicRegion genomicRegion) {
		this.mainGenomicRegion = genomicRegion;
		mainScaler.setGenomicRegion(genomicRegion);
		EventBus.publish(new Event(EventbusTopic.VIEWPORT_CHANGED), this);
	}
	public GenomicRegion getFetchGenomicRegion()
	{
		GenomicRegion widerRegion = mainGenomicRegion.getDuplicate();
		widerRegion.setVisibleStart(mainGenomicRegion.getVisibleStart() - mainGenomicRegion.getWidth());
		widerRegion.setVisibleEnd(mainGenomicRegion.getVisibleEnd() + mainGenomicRegion.getWidth());
		return widerRegion;
	}
	public GenomicRegion getMainGenomicRegion()
	{
		//return a copy to avoid tampering without throwing an event
		return mainGenomicRegion.getDuplicate();
	}
	public ScreenRegion getScreenRegion() {
		return mainScreenRegion;
	}
	public void setScreenRegion(ScreenRegion screenRegion) {
		this.mainScreenRegion = screenRegion;
		mainScaler.setScreenRegion(screenRegion);
	}
	public String getChromosome() {
		return mainGenomicRegion.getChromosome();
	}
	public void setChromosome(String chromosome) {
		if (chromosome.equals(getChromosome())) return;
		focusedFeature = null; // set to chromosome feature if implemented
		mainGenomicRegion.setChromosome(chromosome);
		contextGenomicRegion.setChromosome(chromosome);
		EventBus.publish(new Event(EventbusTopic.VIEWPORT_CHANGED), this);
		EventBus.publish(new Event(EventbusTopic.VIEWCONTEXT_CHANGED), this);
	}
	public Boolean equals(ViewPort viewPort) {
		return (mainGenomicRegion.equals(viewPort.mainGenomicRegion) && mainScreenRegion.equals(viewPort.mainScreenRegion));
	}
	public Boolean equalsIgnoreStrand(ViewPort viewPort) {
		return (mainGenomicRegion.equalsIgnoreStrand(viewPort.mainGenomicRegion) && mainScreenRegion.equals(viewPort.mainScreenRegion));
	}
	public Boolean complete() {
		return (contigs != null && mainGenomicRegion.complete());
	}
	public GenomicRegion getContextGenomicRegion() {
		return contextGenomicRegion.getDuplicate();
	}
	public void setContextGenomicRegion(GenomicRegion contextGenomicRegion) {
		this.contextGenomicRegion = contextGenomicRegion;
		contextScaler.setGenomicRegion(contextGenomicRegion);
		EventBus.publish(new Event(EventbusTopic.VIEWCONTEXT_CHANGED), this);
	}
	public ScreenRegion getMainScreenRegion() {
		return mainScreenRegion;
	}
	public void setMainScreenRegion(ScreenRegion mainScreenRegion) {
		this.mainScreenRegion = mainScreenRegion;
	}
	public ScreenRegion getContextScreenRegion() {
		return contextScreenRegion;
	}
	public void setContextScreenRegion(ScreenRegion contextScreenRegion) {
		this.contextScreenRegion = contextScreenRegion;
	}
	public HorizontalScaler getContextScaler() {
		return contextScaler;
	}
	public void setContextScaler(HorizontalScaler contextScaler) {
		this.contextScaler = contextScaler;
	}
	public HorizontalScaler getMainScaler() {
		return mainScaler;
	}
	public void setMainScaler(HorizontalScaler mainScaler) {
		this.mainScaler = mainScaler;
	}
	public DragMode getDragMode() {
		return dragMode;
	}
	public void setDragMode(DragMode dragMode) {
		this.dragMode = dragMode;
	}
	public Feature getFocusedFeature() {
		return focusedFeature;
	}
	
}
