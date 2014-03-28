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
package com.genohm.viewsGWT.client.components;



import java.util.LinkedList;
import java.util.List;

import com.genohm.viewsGWT.client.ViewPort;
import com.genohm.viewsGWT.client.ViewsGWT;
import com.genohm.viewsGWT.client.eventbus.Event;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.client.eventbus.Subscription;
import com.genohm.viewsGWT.client.eventbus.TopicSubscriber;
import com.genohm.viewsGWT.client.track.SequenceTrack;
import com.genohm.viewsGWT.client.track.Track;
import com.genohm.viewsGWT.client.track.TranscriptTrack;
import com.genohm.viewsGWT.shared.BrowserPerspective;
import com.genohm.viewsGWT.shared.Species;
import com.genohm.viewsGWT.shared.data.BBxAssemblyDatasource;
import com.genohm.viewsGWT.shared.data.GFFDatasource;
import com.genohm.viewsGWT.shared.data.GeneDatasource;
import com.genohm.viewsGWT.shared.data.RefSeqDatasource;
import com.genohm.viewsGWT.shared.data.TrackSpecification;
import com.genohm.viewsGWT.shared.renderer.RendererGraphSettings;
import com.genohm.viewsGWT.shared.renderer.RendererSettings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.MouseOutEvent;
import com.smartgwt.client.widgets.events.MouseOutHandler;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;

public class Browser extends VLayout {

	protected BrowserPerspective perspective = null;
	private SectionStack sectionStack = null;
	protected ViewPort mainViewPort = new ViewPort();
//	protected Integer numberOfProcessesToWaitFor = 0;
//	protected HStack modal = null;
//    protected RendererSettings settings = new RendererSettings();
// 	protected RendererGraphSettings graphSettings = new RendererGraphSettings();
	public Browser() {
		super();
		subscribe();

		// some init values
		mainViewPort.setSpecies(Species.HUMAN);
		mainViewPort.setGenomicContextStart(1000001L);
		mainViewPort.setGenomicContextEnd(1010000L);
		mainViewPort.setChromosome("1");
		mainViewPort.setGenomicStart(10000001L);
		mainViewPort.setGenomicEnd(10001000L);
		mainViewPort.setStrand(true);

		this.addMember(new ViewPortController(mainViewPort));
		this.addMember(new VisualViewPortController(mainViewPort));
		createSectionStack();
		this.addMember(sectionStack);

//		RefSeqDatasource rsds = new RefSeqDatasource();
//		rsds.setAssemblyName("GRCh37");
//		rsds.setCanBeFiltered(false);
//		rsds.setChromosomePrefix(null);
//		rsds.setName("GRCh37 reference assembly");
//		rsds.setDescription("The GRCh37 reference assembly");
//		rsds.setIsPublic(true);
//		rsds.setOwner("admin");
//		
//		RendererSettings sequenceSettings = new RendererSettings();
//		sequenceSettings.setRendererName("mappedread");
//		sequenceSettings.setFeatureHeight(50);
//		
//		TrackSpecification sequenceTrackSpec = new TrackSpecification();
//		sequenceTrackSpec.setFeatureDatasource(rsds);
//		sequenceTrackSpec.setIsPublic(true);
//		sequenceTrackSpec.setTitle("Reference assembly");
//		sequenceTrackSpec.setRendererSettings(sequenceSettings);
//		
//		GeneDatasource gds = new GeneDatasource();
//		gds.setCanBeFiltered(false);
//		gds.setChromosomePrefix(null);
//		gds.setName("Ensembl gene data");
//		gds.setDescription("Gene info from ENSEMBL");
//		gds.setIsPublic(true);
//		gds.setOwner("admin");
//		
//		RendererSettings geneSettings = new RendererSettings();
//		geneSettings.setRendererName("gene");
//		geneSettings.setFeatureHeight(10);
//		geneSettings.setGapWidth(2);
//		
//		TrackSpecification geneTrackSpec = new TrackSpecification();
//		geneTrackSpec.setFeatureDatasource(gds);
//		geneTrackSpec.setIsPublic(true);
//		geneTrackSpec.setTitle("Gene track");
//		geneTrackSpec.setRendererSettings(geneSettings);
//		
//		GFFDatasource gfds = new GFFDatasource();
//		gfds.setCanBeFiltered(false);
//		gfds.setChromosomePrefix("chr");
//		gfds.setName("Unfiltered GFF file");
//		//TODO: parametrize base path
//		gfds.setFilePath("/ontologies/transcripts.gff3");
//		gfds.setDescription("Full gff3 dataset without filtering");
//		gfds.setIsPublic(true);
//		gfds.setOwner("admin");
//		
//		RendererSettings gffSettings = new RendererSettings();
//		gffSettings.setRendererName("block");
//		
//		TrackSpecification gffTrackSpec = new TrackSpecification();
//		gffTrackSpec.setFeatureDatasource(gfds);
//		gffTrackSpec.setIsPublic(true);
//		gffTrackSpec.setTitle("All gff data");
//		gffTrackSpec.setRendererSettings(gffSettings);
//		
//		BBxAssemblyDatasource peds = new BBxAssemblyDatasource();
//		peds.setCanBeFiltered(false);
//		peds.setSampleId("1716");
//		peds.setOwner("admin");
//		peds.setIsPublic(false);
//		
//		
//		RendererSettings pairedEndSettings = new RendererSettings();
//		pairedEndSettings.setRendererName("pairedend");
//		pairedEndSettings.setFeatureHeight(10);
//		pairedEndSettings.setGapWidth(2);
//		
//		TrackSpecification pairedEndTrackSpec = new TrackSpecification();
//		pairedEndTrackSpec.setIsPublic(false);
//		pairedEndTrackSpec.setOwner("admin");
//		pairedEndTrackSpec.setFeatureDatasource(peds);
//		pairedEndTrackSpec.setTitle("sample paired end track");
//		pairedEndTrackSpec.setRendererSettings(pairedEndSettings);
//		
//		
//		
//		RendererSettings transcriptSettings = new RendererSettings();
//		transcriptSettings.setRendererName("transcript");
//		
//		TrackSpecification transcriptSpec = new TrackSpecification();
//		transcriptSpec.setIsPublic(true);
//		transcriptSpec.setFeatureDatasource(null);
//		transcriptSpec.setTitle("transcripts");
//		transcriptSpec.setRendererSettings(transcriptSettings);
//		
////		addTrack(new SequenceTrack(mainViewPort, "DS_refseq",
////				new RendererSettings ("mappedreader",settings.getFeatureHeight(),settings.getFeatureColor(),settings.getLineColor(),settings.getLineWidth(),settings.getGapWidth(),settings.getMinScore(),settings.getMaxScore(),settings.getMinHue(),settings.getMaxHue())));
//		addTrack(new SequenceTrack(mainViewPort,sequenceTrackSpec));
////		addTrack(new Track(mainViewPort, "ensembl/gene", "the gene track", "DS_gene",
////				new RendererSettings ("gene",settings.getFeatureHeight(),settings.getFeatureColor(),settings.getLineColor(),settings.getLineWidth(),settings.getGapWidth(),settings.getMinScore(),settings.getMaxScore(),settings.getMinHue(),settings.getMaxHue()),null));
//		addTrack(new Track(mainViewPort,geneTrackSpec));
////		addTrack(new Track(mainViewPort, "ensembl/transcript", "the transcript track", "DS_transcript",
////				new RendererSettings ("block",settings.getPrefixChromosome(),settings.getFeatureheight(),settings.getFeatureColor(),settings.getLineColor(),settings.getLinewidth(),settings.getGapWidth(),settings.getMinScore(),settings.getMaxScore(),settings.getMinHue(),settings.getMaxHue()),null));
////		addTrack(new Track(mainViewPort, "test/simple","a test track","DS_example",
////				new RendererSettings ("block",settings.getPrefixChromosome(),settings.getFeatureheight(),settings.getFeatureColor(),settings.getLineColor(),settings.getLinewidth(),settings.getGapWidth(),settings.getMinScore(),settings.getMaxScore(),settings.getMinHue(),settings.getMaxHue()),null));
////		addTrack(new Track(mainViewPort, "gff/feature", "the feature track", "DS_GFF",
////				new RendererSettings ("block",settings.getFeatureHeight(),settings.getFeatureColor(),settings.getLineColor(),settings.getLineWidth(),settings.getGapWidth(),settings.getMinScore(),settings.getMaxScore(),settings.getMinHue(),settings.getMaxHue()),null));
////		addTrack(new Track(mainViewPort, gffTrackSpec));
////		addTrack(new Track(mainViewPort, "Wig/bar-graph", "the graph track", "DS_Wig",
////				new  RendererGraphSettings("wigBar",settings.getPrefixChromosome(),"bar",graphSettings.getGraphHeight(),graphSettings.getFrameworkColor(),graphSettings.getGraphColor(),graphSettings.getGraphLineWidth(),graphSettings.returnShowGrid()),null));
////		addTrack(new Track(mainViewPort, "Wig/curve-graph", "the graph track", "DS_Wig",
////				new  RendererGraphSettings("wigCurve",settings.getPrefixChromosome(),"bar",graphSettings.getGraphHeight(),graphSettings.getFrameworkColor(),graphSettings.getGraphColor(),graphSettings.getGraphLineWidth(),graphSettings.returnShowGrid()),null));	
////		addTrack(new Track(mainViewPort, "BigWig/graph-bar", "the graph for Big", "DS_BigWig",
////				new  RendererGraphSettings("bigWigBar",settings.getPrefixChromosome(),"bar",graphSettings.getGraphHeight(),graphSettings.getFrameworkColor(),graphSettings.getGraphColor(),graphSettings.getGraphLineWidth(),graphSettings.returnShowGrid()),null));
////		addTrack(new Track(mainViewPort, "BigWig/curve-graph", "the graph for Big", "DS_BigWig",
////				new  RendererGraphSettings("bigWigCurve",settings.getPrefixChromosome(),"bar",graphSettings.getGraphHeight(),graphSettings.getFrameworkColor(),graphSettings.getGraphColor(),graphSettings.getGraphLineWidth(),graphSettings.returnShowGrid()),null));
////		addTrack(new Track(mainViewPort, "Bed/feature", "the BigBed graph", "DS_Bed",
////				new RendererSettings ("Bed",settings.getPrefixChromosome(),settings.getFeatureheight(),settings.getFeatureColor(),settings.getLineColor(),settings.getLinewidth(),settings.getGapWidth(),settings.getMinScore(),settings.getMaxScore(),settings.getMinHue(),settings.getMaxHue()),null));
////		addTrack(new Track(mainViewPort, "BigBed/feature", "the BigBed graph", "DS_BigBed",
////				new RendererSettings ("bigBed",settings.getPrefixChromosome(),settings.getFeatureheight(),settings.getFeatureColor(),settings.getLineColor(),settings.getLinewidth(),settings.getGapWidth(),settings.getMinScore(),settings.getMaxScore(),settings.getMinHue(),settings.getMaxHue()),null));
////		addTrack(new Track(mainViewPort, pairedEndTrackSpec));
//		addTrack(new TranscriptTrack(mainViewPort, transcriptSpec));
//		addTrack(new Track(mainViewPort, pairedEndTrackSpec));
//		
//		
//		this.draw();
 		
//		EventBus.publish(new Event(EventbusTopic.VIEWPORT_CHANGED), mainViewPort);

		ViewsGWT.getViewsServer().getDefaultPerspective(new AsyncCallback<BrowserPerspective>() {
			@Override
			public void onSuccess(BrowserPerspective result) {
				apply(result);
				EventBus.publish(new Event(EventbusTopic.VIEWPORT_CHANGED), mainViewPort);
			}
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Problem fetching default browser perspective",caught);
				EventBus.publish(new Event(EventbusTopic.ERROR), "Could not fetch default browser perspective");
			}
		});
		
		Timer autoSave = new Timer() {
			@Override
			public void run() {
				savePerspective();
			}
		};
		autoSave.scheduleRepeating(10000);
		
	}

	public void apply(BrowserPerspective perspective) {
		if (sectionStack != null) {
 			for (SectionStackSection section: sectionStack.getSections()) {
 				sectionStack.removeSection(section.getID());
 			}
 			for (TrackSpecification spec: perspective.getTracks()) {
 				addTrack(spec);
 			}
		}
		this.perspective = perspective;
		draw();
	}
	public void savePerspective() {
		if (sectionStack != null) {
			List<TrackSpecification> specList = new LinkedList<TrackSpecification>();
 			for (SectionStackSection section: sectionStack.getSections()) {
 				Track track = (Track) section;
 				specList.add(sectionStack.getSectionNumber(section.getName()),track.getTrackSpec());
 			}
 			perspective.setTracks(specList);
		}
		ViewsGWT.getViewsServer().saveDefaultPerspective(perspective, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				EventBus.publish(new Event(EventbusTopic.ERROR), "Could not save browser perspective");
			}
			@Override
			public void onSuccess(Void result) {
				//ok
			}
		});
	}

	protected void subscribe() {
//		EventBus.subscribe(EventbusTopic.START_WAIT, new TopicSubscriber<Integer>() {
//			@Override
//			public void onEvent(Subscription subscription, Integer event) {
//				numberOfProcessesToWaitFor++;
//				if (numberOfProcessesToWaitFor == 1) {
//					showLoading(); 
//				}
//			}
//			@Override
//			public int getId() {
//				return 0;
//			}
//		});
//		EventBus.subscribe(EventbusTopic.END_WAIT, new TopicSubscriber<Integer>() {
//			@Override
//			public void onEvent(Subscription subscription, Integer event) {
//				numberOfProcessesToWaitFor--;
//				if (numberOfProcessesToWaitFor == 0) {
//					stopLoading();
//				}
//			}
//			@Override
//			public int getId() {
//				return 0;
//			}
//		});
		EventBus.subscribe(EventbusTopic.ERROR, new TopicSubscriber<String>() {
			@Override
			public void onEvent(Subscription subscription, String event) {
				SC.warn(event);
			}
			@Override
			public int getId() {
				return 0;
			}
		});
		EventBus.subscribe(EventbusTopic.ADD_TRACK, new TopicSubscriber<TrackSpecification>() {
			@Override
			public void onEvent(Subscription subscription, TrackSpecification specs) {
				addTrack(specs);
			}
			@Override
			public int getId() {
				return 0;
			}
		});
	}

	protected void createSectionStack() {
		sectionStack = new SectionStack();
		sectionStack.setWidth100();
		sectionStack.setHeight("*");
		sectionStack.setVisibilityMode(VisibilityMode.MULTIPLE);
		sectionStack.setCanResizeSections(true);
		sectionStack.setCanReorderSections(true);
		sectionStack.setAnimateSections(true);
		//sectionStack.setHeaderHeight(5);
		sectionStack.setOverflow(Overflow.HIDDEN);
		sectionStack.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				EventBus.publish(new Event(EventbusTopic.RESET_DRAG), null);
			}
		});
	}
	public void addTrack(TrackSpecification spec) {
		//TODO: find nicer way to handle specific tracks
		if (spec.getFeatureDatasource() == null) {
			TranscriptTrack track = new TranscriptTrack(mainViewPort, spec);
			addTrack(track);
		} else if (spec.getFeatureDatasource() instanceof RefSeqDatasource) {
			SequenceTrack track = new SequenceTrack(mainViewPort,spec);
			addTrack(track);
		} else {
			addTrack(new Track(mainViewPort,spec));
		}
	}
	public void addTrack(Track track) {
		sectionStack.addSection(track);
	}
//	protected void showLoading() {
//		modal = new HStack();
//		modal.setWidth100();
//		modal.setHeight100();
//		modal.setDefaultLayoutAlign(Alignment.CENTER);
//
//		Canvas transparent = new Canvas();
//		transparent.setWidth100();
//		transparent.setHeight100();
//		transparent.setBackgroundColor("#555");
//		transparent.setOpacity(60);
//
//		modal.addChild(transparent);
//
//		Label label = new Label();
//
//		label.setLeft(transparent.getWidth()/2 - 110);
//		label.setTop(transparent.getHeight()/2 - 10);
//		label.setIcon("/images/loading.gif");
//		label.setIconHeight(19);
//		label.setIconWidth(220);
//		label.setZIndex(transparent.getZIndex() + 2);		
//
//		modal.addChild(label);
//		addChild(modal);
//		modal.show();
//	};
//	protected void stopLoading() {
//		modal.destroy();
//	};

}
