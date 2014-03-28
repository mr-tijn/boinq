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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.genohm.viewsGWT.client.ResourceManager;
import com.genohm.viewsGWT.client.ViewsGWT;
import com.genohm.viewsGWT.client.ViewsServerAsync;
import com.genohm.viewsGWT.client.analysis.GraphicalAnalysisVisualizer;
import com.genohm.viewsGWT.client.dialog.AnalysisBuilderWizard;
import com.genohm.viewsGWT.client.dialog.SmartROIBuilderWizard;
import com.genohm.viewsGWT.client.dialog.TrackBuilderWizard;
import com.genohm.viewsGWT.client.eventbus.Event;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.client.eventbus.Subscription;
import com.genohm.viewsGWT.client.eventbus.TopicSubscriber;
import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.RegionOfInterest;
import com.genohm.viewsGWT.shared.analysis.Analysis;
import com.genohm.viewsGWT.shared.data.TrackSpecification;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;

public class Navigator extends SectionStack {

	protected ViewsServerAsync viewsServer = ViewsGWT.getViewsServer();
	
	protected List<RegionOfInterest> regions = null;
	protected List<Analysis> analyses = null;
	protected TreeGrid publicRegionsTree;
	protected TreeGrid trackTree;
	protected ListGrid analysesList;
	protected TrackDataTree trackData;

	
	public Navigator() {
		addPublicRegions();
		addTracks();
		addAnalyses();
		refresh();
		EventBus.subscribe(EventbusTopic.REFRESH_ROI,
				new TopicSubscriber<Integer>() {
					@Override
					public void onEvent(Subscription subscription, Integer event) {
						refreshRoi();
					}

					@Override
					public int getId() {
						return 0;
					}
				});
	}

	protected ImgButton addTrackButton() {
		ImgButton addTrackButton = new ImgButton();
		addTrackButton.setSrc(ResourceManager.IMAGE_ADD);
		addTrackButton.setShowDisabledIcon(false);
		addTrackButton.setShowDownIcon(false);
		addTrackButton.setShowHover(false);
		addTrackButton.setWidth(16);
		addTrackButton.setHeight(16);
		addTrackButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				TrackBuilderWizard wizard = new TrackBuilderWizard();
				wizard.setIsModal(true);
				wizard.setCanDragResize(true);
				wizard.setTop("10%");
				wizard.setLeft("10%");
				wizard.setWidth("80%");
				wizard.setHeight("80%");
				wizard.show();
			}
		});
		return addTrackButton;
	}

	protected ImgButton addROIButton() {
		ImgButton addROIButton = new ImgButton();
		addROIButton.setSrc(ResourceManager.IMAGE_ADD);
		addROIButton.setShowDisabledIcon(false);
		addROIButton.setShowDownIcon(false);
		addROIButton.setShowHover(false);
		addROIButton.setWidth(16);
		addROIButton.setHeight(16);
		addROIButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SmartROIBuilderWizard wizard = new SmartROIBuilderWizard();
				wizard.setIsModal(true);
				wizard.setCanDragResize(true);
				wizard.setTop("10%");
				wizard.setLeft("10%");
				wizard.setWidth("80%");
				wizard.setHeight("80%");
				wizard.show();
			}
		});
		return addROIButton;
	}

	protected ImgButton addAnalysisButton() {
		ImgButton addAnalysesButton = new ImgButton();
		addAnalysesButton.setSrc(ResourceManager.IMAGE_ADD);
		addAnalysesButton.setShowDisabledIcon(false);
		addAnalysesButton.setShowDownIcon(false);
		addAnalysesButton.setShowHover(false);
		addAnalysesButton.setWidth(16);
		addAnalysesButton.setHeight(16);
		addAnalysesButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AnalysisBuilderWizard wizard = new AnalysisBuilderWizard();
				wizard.setIsModal(true);
				wizard.setCanDragResize(true);
				wizard.setTop("10%");
				wizard.setLeft("10%");
				wizard.setWidth("40%");
				wizard.setHeight("30%");
				wizard.show();
			}
		});
		return addAnalysesButton;
	}

	protected ImgButton refreshRoiButton() {
		ImgButton refreshButton = new ImgButton();
		refreshButton.setSrc(ResourceManager.IMAGE_REFRESH);
		refreshButton.setWidth(16);
		refreshButton.setHeight(16);
		refreshButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refreshRoi();
			}
		});
		return refreshButton;
	}

	protected ImgButton refreshTracksButton() {
		ImgButton refreshButton = new ImgButton();
		refreshButton.setSrc(ResourceManager.IMAGE_REFRESH);
		refreshButton.setWidth(16);
		refreshButton.setHeight(16);
		refreshButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refreshTracks();
			}
		});
		return refreshButton;
	}

	protected ImgButton refreshAnalysesButton() {
		ImgButton refreshButton = new ImgButton();
		refreshButton.setSrc(ResourceManager.IMAGE_REFRESH);
		refreshButton.setWidth(16);
		refreshButton.setHeight(16);
		refreshButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refreshAnalyses();
			}
		});
		return refreshButton;
	}

	protected void addPublicRegions() {
		SectionStackSection publicRegions = new SectionStackSection();
		publicRegions.setControls(addROIButton(), refreshRoiButton());
		publicRegionsTree = new TreeGrid();
		publicRegionsTree.addRecordClickHandler(new RecordClickHandler() {
			@Override
			public void onRecordClick(RecordClickEvent event) {
				GenomicRegion region = ((RegionTreeNode) event.getRecord())
						.getRegion();
				if (region != null)
					EventBus.publish(new Event(
							EventbusTopic.CHANGE_GENOMICREGION), region);
			}
		});
		// TODO: split into public and my regions
		publicRegions.setTitle("Regions Of Interest");
		publicRegions.addItem(publicRegionsTree);
		TreeGridField nameField = new TreeGridField("name");
		nameField.setType(ListGridFieldType.TEXT);
		TreeGridField chrField = new TreeGridField("chr");
		chrField.setType(ListGridFieldType.TEXT);
		chrField.setHidden(true);
		TreeGridField startField = new TreeGridField("start");
		startField.setType(ListGridFieldType.INTEGER);
		startField.setHidden(true);
		TreeGridField endField = new TreeGridField("end");
		endField.setType(ListGridFieldType.INTEGER);
		endField.setHidden(true);
		TreeGridField strandField = new TreeGridField("strand");
		strandField.setType(ListGridFieldType.BOOLEAN);
		strandField.setHidden(true);
		TreeGridField speciesIdField = new TreeGridField("speciesId");
		speciesIdField.setHidden(true);
		TreeGridField ownerField = new TreeGridField("owner");
		ownerField.setHidden(true);
		TreeGridField statusField = new TreeGridField("status",20);
		statusField.setImageURLPrefix("");
		statusField.setAlign(Alignment.CENTER);
		statusField.setShowTitle(false);
		statusField.setCanSort(false);
		statusField.setType(ListGridFieldType.IMAGE);
		TreeGridField removeField = new TreeGridField("remove");
		removeField.setType(ListGridFieldType.ICON);
		removeField.setIcon(ResourceManager.IMAGE_REMOVE);
		removeField.setCellIcon(ResourceManager.IMAGE_REMOVE);
		removeField.addRecordClickHandler(new RecordClickHandler() {
			
			@Override
			public void onRecordClick(RecordClickEvent event) {
				RegionTreeNode node = (RegionTreeNode) event.getRecord();
				RegionTree tree = (RegionTree) publicRegionsTree.getData();
				if ("region".equals(node.getNodeType())) {
					//region
					RegionTreeNode roi = (RegionTreeNode) tree.getParent(node);
					if (roi != null) {
						Long roiId = Long.parseLong(roi.getAttribute("id"));
						Long regionId = Long.parseLong(node.getAttribute("id"));
						ViewsGWT.getViewsServer().removeRegionFromRoi(regionId, roiId, new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								EventBus.publish(new Event(EventbusTopic.ERROR), "Could not remove region\n" + caught.getMessage());
							}
							@Override
							public void onSuccess(Void result) {
								EventBus.publish(new Event(EventbusTopic.REFRESH_ROI), null);
							}
						});
					}
				} else if ("roi".equals(node.getNodeType())){
					//roi
					Long roiId = Long.parseLong(node.getAttribute("id"));
					ViewsGWT.getViewsServer().removeRoi(roiId, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							EventBus.publish(new Event(EventbusTopic.ERROR), "Could not remove region of interest\n" + caught.getMessage());
						}
						@Override
						public void onSuccess(Void result) {
							EventBus.publish(new Event(EventbusTopic.REFRESH_ROI), null);
						}
					});
				}
			}
		});
		publicRegionsTree.setFields(nameField, chrField, startField, endField,
				strandField, speciesIdField, ownerField, statusField, removeField);
		addSection(publicRegions);
	}

	protected void addTracks() {
		SectionStackSection tracks = new SectionStackSection();
		tracks.setControls(addTrackButton(),refreshTracksButton());
		trackTree = new TreeGrid();
		trackTree.addRecordClickHandler(new RecordClickHandler() {
			@Override
			public void onRecordClick(RecordClickEvent event) {
				if (trackData != null) {
					TrackSpecification spec = trackData
							.getSpec(((TreeNode) event.getRecord()).getName());
					if (spec != null)
						EventBus.publish(new Event(EventbusTopic.ADD_TRACK),
								spec);
				}
			}
		});
		tracks.setTitle("Tracks");
		tracks.addItem(trackTree);
		TreeGridField nameField = new TreeGridField("title");
		nameField.setType(ListGridFieldType.TEXT);
		trackTree.setFields(nameField);
		addSection(tracks);
	}

	protected void addAnalyses() {
		SectionStackSection analyses = new SectionStackSection();
		analyses.setControls(addAnalysisButton(), refreshAnalysesButton());
		ListGridField nameField = new ListGridField("name");
		nameField.setType(ListGridFieldType.TEXT);
		ListGridField statusField = new ListGridField("status",20);
		statusField.setType(ListGridFieldType.IMAGE);
		statusField.setShowTitle(false);
		analysesList = new ListGrid();
		analysesList.setFields(nameField, statusField);
		analysesList.addRecordClickHandler(new RecordClickHandler() {
			@Override
			public void onRecordClick(RecordClickEvent event) {
				Long analysisId = Long.parseLong(event.getRecord().getAttribute("id"));
				Analysis analysis = findById(analysisId);
				GraphicalAnalysisVisualizer analysisVisualizer = new GraphicalAnalysisVisualizer();
				analysis.acceptVisualizer(analysisVisualizer);
				analysisVisualizer.show();
			}
		});
		analyses.setTitle("Analyses");
		analyses.addItem(analysesList);
		addSection(analyses);
	}

	protected Analysis findById(Long id) {
		for (Analysis search: analyses) {
			if (id == search.getId()) return search;
		}
		return null;
	}
	
	protected void refreshRoi() {
		try {
			viewsServer.getROIs(new AsyncCallback<List<RegionOfInterest>>() {
				@Override
				public void onSuccess(List<RegionOfInterest> rois) {
					RegionTree regionTree = new RegionTree(rois);
					publicRegionsTree.setData(regionTree);
				}

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("server threw error while getting regions of interest",
							caught);
					EventBus.publish(new Event(EventbusTopic.ERROR),
							"Server error while getting regions of interest");
				}
			});
		} catch (Exception e) {
			GWT.log("could not get regions of interest", e);
			EventBus.publish(new Event(EventbusTopic.ERROR),
					"Could not get regions of interest");
		}
	}

	protected void refreshTracks() {
		try {
			viewsServer
					.getTrackSpecs(new AsyncCallback<List<TrackSpecification>>() {
						@Override
						public void onSuccess(List<TrackSpecification> result) {
							trackData = new TrackDataTree(result);
							trackTree.setData(trackData);
						}

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("server threw error while getting track specifications",
									caught);
							EventBus.publish(new Event(EventbusTopic.ERROR),
									"Server error while getting track specifications");
						}
					});
		} catch (Exception e) {
			GWT.log("could not get track specifications", e);
			EventBus.publish(new Event(EventbusTopic.ERROR),
					"Could not get track specifications");
		}
	}

	protected void refreshAnalyses() {
		try {
			viewsServer
					.getAnalyses(new AsyncCallback<List<Analysis>>() {
						@Override
						public void onSuccess(List<Analysis> results) {
							if (results == null) return;
							analyses = results;
							RecordList analysesRecordList = new RecordList();
							for (Analysis result:results) {
								analysesRecordList.add(new AnalysisRecord(result));
							}
							analysesList.setData(analysesRecordList);
						}

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("server threw error while getting analyses", caught);
							EventBus.publish(new Event(EventbusTopic.ERROR), "Server error while getting analyses");
						}
					});
		} catch (Exception e) {
			GWT.log("could not get analyses", e);
			EventBus.publish(new Event(EventbusTopic.ERROR), "Could not get analyses");
		}
	}

	protected void refresh() {
		refreshRoi();
		refreshTracks();
		refreshAnalyses();
	}

	class AnalysisRecord extends Record {
		public AnalysisRecord(Analysis analysis) {
			setAttribute("id", analysis.getId());
			setAttribute("name", analysis.getName());
			if (analysis.getStatus() != null) {
				switch (analysis.getStatus()) {
				case Analysis.STATUS_PENDING:
					setAttribute("status", ResourceManager.IMAGE_PENDING);
					break;
				case Analysis.STATUS_COMPUTING:
					setAttribute("status", ResourceManager.IMAGE_RUNNING);
					break;
				case Analysis.STATUS_SUCCESS:
					setAttribute("status", ResourceManager.IMAGE_DONE);
					break;
				case Analysis.STATUS_ERROR:
					setAttribute("status", ResourceManager.IMAGE_WARNING);
					break;
				default:
					setAttribute("status", "");
				}
			}
		}
	}
	
	
	class RegionTree extends Tree {
		public RegionTree(List<RegionOfInterest> rois) {
			TreeNode root = new TreeNode();
			setRoot(root);
			for (RegionOfInterest roi : rois) {
				RegionTreeNode parent = new RegionTreeNode(roi);
				add(parent, root);
				if (roi.getRegions() != null) {
					for (GenomicRegion reg : roi.getRegions()) {
						RegionTreeNode node = new RegionTreeNode(reg);
						add(node, parent);
					}
				}
			}
		}

	}

	class RegionTreeNode extends TreeNode {

		public RegionTreeNode(RegionOfInterest roi) {
			setID(roi.getId().toString());
			setNameField(roi.getName());
			setStatusField(roi.getStatus());
			setOwner(roi.getOwner());
			setNodeType("roi");
		}

		public RegionTreeNode(GenomicRegion region) {
			setID(region.getId().toString());
			setNameField(region.getName());
			setStartField(region.getVisibleStart());
			setEndField(region.getVisibleEnd());
			setChrField(region.getChromosome());
			setStrandField(region.getStrand());
			setSpeciesIdField(region.getSpeciesId());
			setNodeType("region");
		}

		protected void setStatusField(Integer status) {
			if (status == null)
				status = -1;
			switch (status) {
			case RegionOfInterest.STATUS_PENDING:
				setAttribute("status", ResourceManager.IMAGE_PENDING);
				break;
			case RegionOfInterest.STATUS_COMPUTING:
				setAttribute("status", ResourceManager.IMAGE_RUNNING);
				break;
			case RegionOfInterest.STATUS_READY:
				setAttribute("status", ResourceManager.IMAGE_DONE);
				break;
			case RegionOfInterest.STATUS_ERROR:
				setAttribute("status", ResourceManager.IMAGE_WARNING);
				break;
			default:
				setAttribute("status", "");
			}
		}

		protected void setNodeType(String nodeType) {
			setAttribute("nodeType", nodeType);
		}
		
		public String getNodeType() {
			return getAttributeAsString("nodeType");
		}
		
		protected void setOwner(String owner) {
			setAttribute("owner", owner);
		}
		
		protected void setNameField(String name) {
			setAttribute("name", name);
		}

		protected String getNameField() {
			return getAttributeAsString("name");
		}

		protected void setStartField(Long start) {
			setAttribute("start", start);
		}

		protected Long getStartField() {
			return getAttributeAsLong("start");
		}

		protected void setEndField(Long end) {
			setAttribute("end", end);
		}

		protected Long getEndField() {
			return getAttributeAsLong("end");
		}

		protected void setChrField(String chr) {
			setAttribute("chr", chr);
		}

		protected String getChrField() {
			return getAttributeAsString("chr");
		}

		protected void setStrandField(Boolean strand) {
			setAttribute("strand", strand);
		}

		protected Boolean getStrandField() {
			return getAttributeAsBoolean("strand");
		}

		protected void setSpeciesIdField(Integer speciesId) {
			setAttribute("speciesId", speciesId);
		}

		protected Integer getSpeciesIdField() {
			return getAttributeAsInt("speciesId");
		}

		public GenomicRegion getRegion() {
			if (getSpeciesIdField() == null || getChrField() == null
					|| getStartField() == null || getEndField() == null
					|| getStrandField() == null)
				return null;
			GenomicRegion gRegion = new GenomicRegion();
			gRegion.setName(getNameField());
			gRegion.setSpeciesId(getSpeciesIdField());
			gRegion.setChromosome(getChrField());
			gRegion.setVisibleStart(getStartField());
			gRegion.setVisibleEnd(getEndField());
			gRegion.setStrand(getStrandField());
			return gRegion;
		}
	}

	class TrackDataTree extends Tree {
		protected Map<String, TrackSpecification> trackMap = new HashMap<String, TrackSpecification>();

		TrackDataTree(List<TrackSpecification> specList) {
			TreeNode root = new TreeNode();
			setRoot(root);
			TrackTreeNode pubTracks = new TrackTreeNode();
			pubTracks.setTitle("Public tracks");
			add(pubTracks, root);
			TrackTreeNode myTracks = new TrackTreeNode();
			myTracks.setTitle("Private tracks");
			add(myTracks, root);
			for (TrackSpecification spec : specList) {
				TrackTreeNode node = new TrackTreeNode(spec);
				if (spec.getIsPublic()) {
					TreeNode treeNode = add(node, pubTracks);
					trackMap.put(treeNode.getName(), spec);
				} else {
					TreeNode treeNode = add(node, myTracks);
					trackMap.put(treeNode.getName(), spec);
				}
			}
		}

		public TrackSpecification getSpec(String id) {
			return trackMap.get(id);
		}
	}

	class TrackTreeNode extends TreeNode {
		public TrackTreeNode() {
			super();
		}

		TrackTreeNode(TrackSpecification trackSpec) {
			setTitle(trackSpec.getTitle());
			setId(trackSpec.getId());
		}

		public void setTitle(String name) {
			setAttribute("title", name);
		}

		public void setId(Integer id) {
			setAttribute("id", id);
		}
	}

}
