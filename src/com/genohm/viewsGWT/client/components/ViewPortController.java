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

import java.util.LinkedHashMap;
import java.util.List;

import com.genohm.viewsGWT.client.FeatureServer;
import com.genohm.viewsGWT.client.FeatureServerAsync;
import com.genohm.viewsGWT.client.ViewPort;
import com.genohm.viewsGWT.client.ViewsGWT;
import com.genohm.viewsGWT.client.ViewsServerAsync;
import com.genohm.viewsGWT.client.dialog.GeneSelector;
import com.genohm.viewsGWT.client.dialog.SPARQLBrowser;
import com.genohm.viewsGWT.client.dialog.TrackBuilderWizard;
import com.genohm.viewsGWT.client.eventbus.Event;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.client.eventbus.Subscription;
import com.genohm.viewsGWT.client.eventbus.TopicSubscriber;
import com.genohm.viewsGWT.client.track.DragMode;
import com.genohm.viewsGWT.client.util.ScreenRegion;
import com.genohm.viewsGWT.shared.Chromosome;
import com.genohm.viewsGWT.shared.ContigSet;
import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.Species;
import com.genohm.viewsGWT.shared.data.ensembl.EnsemblGene;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.types.KeyNames;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.form.validator.CustomValidator;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class ViewPortController extends ToolStrip {
	
	protected ViewsServerAsync viewsServer = ViewsGWT.getViewsServer();
	protected RadioGroupItem dragModeSelect;
	protected SelectItem speciesSelect;
	protected SelectItem chromosomeSelect;
	protected TextItem startPositionText;
	protected TextItem endPositionText;
	protected RadioGroupItem strandSelect;
	protected ButtonItem goButton;
	protected ToolStripButton zoomInButton;
	protected ToolStripButton zoomOutButton;
	protected TextItem geneSearchItem;
	protected Label userLabel;
	protected Label loadingLabel;
	
	private int numberOfProcessesToWaitFor = 0;
	private DragMode previousDragMode;
	
	protected IntegerRangeValidator positionRangeValidator = new IntegerRangeValidator();
	protected KeyPressHandler checkForEnter = new KeyPressHandler() {
		@Override
		public void onKeyPress(KeyPressEvent event) {
			if (event.getKeyName().equals(KeyNames.ENTER)) {
				processAllFields();
			} 
		}
	};
	
	protected ViewPort viewPort;
	
	public ViewPortController(ViewPort theViewPort) {
		this.viewPort = theViewPort;
		//setWidth100();
		addSpacer(20);
//		addTrackWizardButton();
		addFill();
		addDragModeSelect();
		addSpeciesSelect();
		addChromosomeSelect();
		addStartTextItem();
		addEndTextItem();
		addStrandSelect();
		addGoButton();
		addGeneSearchItem();
		addZoomInButton();
		addZoomOutButton();
		addFill();
//		addUserLabel();
//		addLogoutButton();
		addLoadingIndicator();
		EventBus.subscribe(EventbusTopic.DRAG_SELECT, new TopicSubscriber<ScreenRegion>() {
			@Override
			public void onEvent(Subscription subscription, ScreenRegion newRegion) {
				GenomicRegion region = viewPort.getMainGenomicRegion();
				region.setVisibleStart(viewPort.getMainScaler().getLowerGenomicPosition(newRegion.getVisibleStart()));
				region.setVisibleEnd(viewPort.getMainScaler().getLowerGenomicPosition(newRegion.getVisibleEnd()));
				viewPort.setMainGenomicRegion(region);
			}
			@Override
			public int getId() {
				return 0;
			}
		});
		EventBus.subscribe(EventbusTopic.MOVE_SELECT, new TopicSubscriber<Integer>() {
			@Override
			public void onEvent(Subscription subscription, Integer offset) {
				GenomicRegion region = viewPort.getMainGenomicRegion();
				region.setVisibleStart(region.getVisibleStart()+offset);
				region.setVisibleEnd(region.getVisibleEnd()+offset);
				viewPort.setMainGenomicRegion(region);
			}
			@Override
			public int getId() {
				return 0;
			}
		});
		EventBus.subscribe(EventbusTopic.CHANGE_GENOMICREGION, new TopicSubscriber<GenomicRegion>() {
			@Override
			public void onEvent(Subscription subscription, GenomicRegion region) {
				viewPort.setMainGenomicRegion(region);
			}
			@Override
			public int getId() {
				return 0;
			}
		});
		EventBus.subscribe(EventbusTopic.VIEWPORT_CHANGED, new TopicSubscriber<ViewPort>() {
			@Override
			public void onEvent(Subscription subscription, ViewPort viewPort) {
				changeSpecies(viewPort.getSpecies().getValue());
				startPositionText.setValue(viewPort.getGenomicStart());
				endPositionText.setValue(viewPort.getGenomicEnd());
				speciesSelect.setValue(viewPort.getSpecies().getValue());
				chromosomeSelect.setValue(viewPort.getChromosome());
				strandSelect.setValue(viewPort.getStrand()?"pos":"neg");
			}
			@Override
			public int getId() {
				return 0;
			}
			
		});
		EventBus.subscribe(EventbusTopic.START_WAIT, new TopicSubscriber<Integer>() {
			@Override
			public void onEvent(Subscription subscription, Integer event) {
				numberOfProcessesToWaitFor++;
				if (numberOfProcessesToWaitFor == 1) {
					loadingLabel.setIcon("/images/small_loading.gif");
					speciesSelect.setDisabled(true);
					chromosomeSelect.setDisabled(true);
					startPositionText.setDisabled(true);
					endPositionText.setDisabled(true);
					strandSelect.setDisabled(true);
					goButton.setDisabled(true);
					zoomInButton.setDisabled(true);
					zoomOutButton.setDisabled(true);
					geneSearchItem.setDisabled(true);
					previousDragMode = viewPort.getDragMode();
					viewPort.setDragMode(DragMode.DISABLED);
					DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "wait");
				}
			}
			@Override
			public int getId() {
				return 0;
			}
		});
		EventBus.subscribe(EventbusTopic.END_WAIT, new TopicSubscriber<Integer>() {
			@Override
			public void onEvent(Subscription subscription, Integer event) {
				numberOfProcessesToWaitFor--;
				
				if (numberOfProcessesToWaitFor == 0) {
					loadingLabel.setIcon("/images/small_notloading.gif");
					speciesSelect.setDisabled(false);
					chromosomeSelect.setDisabled(false);
					startPositionText.setDisabled(false);
					endPositionText.setDisabled(false);
					strandSelect.setDisabled(false);
					goButton.setDisabled(false);
					zoomInButton.setDisabled(false);
					zoomOutButton.setDisabled(false);
					geneSearchItem.setDisabled(false);
					viewPort.setDragMode(previousDragMode);
					DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
				}
				
			}
			@Override
			public int getId() {
				return 0;
			}
		});

	}
	
	protected void addTrackWizardButton() {
		ToolStripButton trackWizardButton = new ToolStripButton();
		trackWizardButton.setIcon("add_filter.png");
		trackWizardButton.setActionType(SelectionType.BUTTON);
		trackWizardButton.setTooltip("Start track creation wizard");
		trackWizardButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
//				TrackBuilderWizard wizard = new TrackBuilderWizard();
//				wizard.setIsModal(true);
//				wizard.setTop("10%");
//				wizard.setLeft("10%");
//				wizard.setWidth("80%");
//				wizard.setHeight("80%");
//				wizard.show();
				SPARQLBrowser browser = new SPARQLBrowser();
				browser.setIsModal(true);
				browser.setCanDragResize(true);
				browser.show();
			}
		});
		this.addButton(trackWizardButton);
	}
	
	protected void addDragModeSelect() {
		//dragModeSelect = new RadioGroupItem();
		ToolStripButton zoomButton = new ToolStripButton();
		zoomButton.setIcon("vergroot.png");
		zoomButton.setActionType(SelectionType.RADIO);
		zoomButton.setTooltip("Switch to zoom mode");
		zoomButton.setRadioGroup("dragModeSelect");
		zoomButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				viewPort.setDragMode(DragMode.ZOOM);
			}
		});
		this.addButton(zoomButton);
		ToolStripButton moveButton = new ToolStripButton();
		moveButton.setIcon("hand.png");
		moveButton.setActionType(SelectionType.RADIO);
		moveButton.setTooltip("Switch to move mode");
		moveButton.setRadioGroup("dragModeSelect");
		moveButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				viewPort.setDragMode(DragMode.MOVE);
			}
		});
		this.addButton(moveButton);
	}
	
	protected void addSpeciesSelect() {
		speciesSelect = new SelectItem("speciesSelect");
		speciesSelect.setTitle("Species");
		speciesSelect.setValueMap(Species.asMap());
		speciesSelect.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				Integer speciesId = Integer.parseInt((String) event.getValue());
				viewPort.setSpecies(Species.getByID(speciesId));
				//changeSpecies(speciesId);
			}
			
		});
		this.addFormItem(speciesSelect);
	}
	
	protected void changeSpecies(Integer speciesId) {
		if (speciesSelect.getValue() == null || !speciesSelect.getValue().equals(speciesId)) {
//			chromosomeSelect.setDisabled(true);
//			startPositionText.setDisabled(true);
//			endPositionText.setDisabled(true);
//			strandSelect.setDisabled(true);
			EventBus.publish(new Event(EventbusTopic.START_WAIT), 0);
			FeatureServerAsync featureServer = (FeatureServerAsync) GWT.create(FeatureServer.class);
			AsyncCallback<List<Chromosome>> callback = new AsyncCallback<List<Chromosome>>() {
				@Override
				public void onFailure(Throwable caught) {
					EventBus.publish(new Event(EventbusTopic.ERROR), "Could not get chromosomes: "+caught.getMessage());
					EventBus.publish(new Event(EventbusTopic.END_WAIT), 0);
				}
				@Override
				public void onSuccess(List<Chromosome> result) {
					viewPort.setContigs(new ContigSet(result));
					chromosomeSelect.setValueMap(viewPort.getContigs().asMap());
//					chromosomeSelect.setDisabled(false);
//					startPositionText.setDisabled(false);
//					endPositionText.setDisabled(false);
//					strandSelect.setDisabled(false);
					EventBus.publish(new Event(EventbusTopic.SPECIES_CHANGED), viewPort.getSpecies());
					EventBus.publish(new Event(EventbusTopic.END_WAIT), 0);
				}
			};
			try {
				featureServer.getChromosomes(viewPort.getSpecies(), callback);
			} catch (Exception e) {
				EventBus.publish(new Event(EventbusTopic.ERROR), "Could not get chromosomes: "+e.getMessage());					
			}
		}
	}

	protected void addChromosomeSelect() {
		chromosomeSelect = new SelectItem("chromosomeSelect");
		chromosomeSelect.setTitle("Chromosome");
		chromosomeSelect.setDisabled(true);
		chromosomeSelect.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				String chromosome = (String) event.getValue();
				viewPort.setChromosome(chromosome);
				Chromosome activeContig = viewPort.getContigs().getContig(chromosome);
				positionRangeValidator.setMin(activeContig.getStartCoordinate().intValue());
				positionRangeValidator.setMax(activeContig.getEndCoordinate().intValue());
				GenomicRegion context = viewPort.getContextGenomicRegion();
				context.setVisibleStart(activeContig.getStartCoordinate());
				context.setVisibleEnd(activeContig.getEndCoordinate());
				viewPort.setContextGenomicRegion(context);
			}
		});
		this.addFormItem(chromosomeSelect);
	}
	
	protected void addStartTextItem() {
		startPositionText = new TextItem("startPosition");
		startPositionText.setTitle("From");
		startPositionText.setDisabled(true);
		startPositionText.setType("integer");
		startPositionText.setKeyPressFilter("[0-9,\n]");
		startPositionText.addKeyPressHandler(checkForEnter);
		startPositionText.setValidators(positionRangeValidator);
		startPositionText.setValidateOnChange(true);
		this.addFormItem(startPositionText);
	}
		
	protected void addEndTextItem() {
		endPositionText = new TextItem("endPosition");
		endPositionText.setTitle("To");
		endPositionText.setDisabled(true);
		endPositionText.setType("integer");
		endPositionText.setKeyPressFilter("[0-9,\n]");
		endPositionText.addKeyPressHandler(checkForEnter);
		CustomValidator compareValidator = new CustomValidator() {
			@Override
			protected boolean condition(Object value) {
				try {
					if (value != null && toLong(value) < toLong(startPositionText.getValue())) return false;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		};
		compareValidator.setErrorMessage("End position must exceed start position");
		endPositionText.setValidators(compareValidator, positionRangeValidator);
		endPositionText.setValidateOnChange(true);
		this.addFormItem(endPositionText);
	}
	
	protected Long toLong(Object value) {
		Long newPosition = null;
		try {
			newPosition = Long.parseLong(value.toString());
		} catch (Exception e) {
			EventBus.publish(new Event(EventbusTopic.ERROR), "could not parse "+value);
		}
		return newPosition;
	}
	
	protected void addStrandSelect() {
		strandSelect = new RadioGroupItem("strandSelect");
		LinkedHashMap<String, String> strandChoices = new LinkedHashMap<String, String>();
		strandChoices.put("pos", "+");
		strandChoices.put("neg", "-");
		strandSelect.setValueMap(strandChoices);
		strandSelect.setDisabled(true);
		strandSelect.setTitle("strand");
		strandSelect.addKeyPressHandler(checkForEnter);
		this.addFormItem(strandSelect);
	}
	
	protected Boolean processStrand() {
		return ((String) strandSelect.getValue()).equals("pos"); 
	}
	
	protected void addGoButton() {
		goButton = new ButtonItem("Go");
		goButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				processAllFields();								
			}
		});
		this.addFormItem(goButton);
	}
	
	
	protected void addZoomInButton() {
		
		zoomInButton = new ToolStripButton();
		zoomInButton.setIcon("zoom_plus.png");
		zoomInButton.setTooltip("Zoom in");
		zoomInButton.setActionType(SelectionType.BUTTON);
		zoomInButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				GenomicRegion currentRegion = viewPort.getMainGenomicRegion();
				GenomicRegion newRegion = currentRegion.getDuplicate();
				newRegion.setVisibleStart(currentRegion.getVisibleStart() + (long) Math.floor(.45*(currentRegion.getWidth())));
				newRegion.setVisibleEnd(currentRegion.getVisibleEnd() - (long) Math.floor(.45*(currentRegion.getWidth())));
				viewPort.setMainGenomicRegion(newRegion);
			}
		});
		this.addButton(zoomInButton);
	}
	
	protected void addZoomOutButton() {
			
		zoomOutButton = new ToolStripButton();
		zoomOutButton.setIcon("zoom_min.png");
		zoomOutButton.setTooltip("Zoom out");
		zoomOutButton.setActionType(SelectionType.BUTTON);
		zoomOutButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				GenomicRegion currentRegion = viewPort.getMainGenomicRegion();
				GenomicRegion newRegion = currentRegion.getDuplicate();
				newRegion.setVisibleStart(currentRegion.getVisibleStart() - (long) Math.floor(4.5*(currentRegion.getWidth())));
				newRegion.setVisibleEnd(currentRegion.getVisibleEnd() + (long) Math.floor(4.5*(currentRegion.getWidth())));
				viewPort.setMainGenomicRegion(newRegion);
			}
		});
		this.addButton(zoomOutButton);

	}
	
	GeneSelector geneSelector = null;
	protected void addGeneSearchItem() {
		geneSearchItem = new TextItem("geneSearch");
		geneSearchItem.setTitle("Search gene");
		geneSearchItem.setDisabled(true);
		geneSearchItem.setType("text");
		geneSearchItem.setKeyPressFilter("[a-z,A-z,0-9,\n]");
		geneSearchItem.addKeyPressHandler(new KeyPressHandler() {	
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName().equals(KeyNames.ENTER)) {
					String searchString = geneSearchItem.getValueAsString();
					EventBus.publish(new Event(EventbusTopic.START_WAIT),null);
					viewsServer.searchGenesFullText(searchString, new AsyncCallback<List<EnsemblGene>>() {
						
						@Override
						public void onSuccess(List<EnsemblGene> result) {
							EventBus.publish(new Event(EventbusTopic.END_WAIT), null);
							if (geneSelector == null) {
								geneSelector = new GeneSelector();
								geneSelector.setWidth(200);
							}
							geneSelector.setData(result);
							geneSelector.setLeft(geneSearchItem.getContainerWidget().getAbsoluteLeft());
							geneSelector.setTop(geneSearchItem.getContainerWidget().getAbsoluteTop()+geneSearchItem.getHeight() + 2);
							geneSelector.show();
						}
						
						@Override
						public void onFailure(Throwable caught) {
							EventBus.publish(new Event(EventbusTopic.END_WAIT), null);
							EventBus.publish(new Event(EventbusTopic.ERROR), "Could not perform fulltext search " + caught);
						}
					});
				} 
			}
		});
		this.addFormItem(geneSearchItem);
	}

	
	protected void processAllFields() {
		if (startPositionText.validate() && endPositionText.validate() && strandSelect.getValue() != null) {
			GenomicRegion newRegion = viewPort.getMainGenomicRegion();
			newRegion.setStrand(processStrand());
			newRegion.setVisibleStart(toLong(startPositionText.getValue()));
			newRegion.setVisibleEnd(toLong(endPositionText.getValue()));
			if (newRegion.complete()) viewPort.setMainGenomicRegion(newRegion);
		}
	}
	
	protected void addUserLabel() {
		userLabel = new Label();
		viewsServer.getCurrentUserName(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				userLabel.setContents("logged in as : <b>"+result+"</b>");
			}
			@Override
			public void onFailure(Throwable caught) {
				EventBus.publish(new Event(EventbusTopic.ERROR), "could not get username");				
			}
		});
		addMember(userLabel);
	}

	
	protected void addLogoutButton() {
		ButtonItem logoutButton = new ButtonItem();
		logoutButton.setTitle("logout");
		logoutButton.addClickHandler(new ClickHandler() {		
			@Override
			public void onClick(ClickEvent event) {
				viewsServer.logout(new AsyncCallback<Void>() {
					@Override
					public native void onSuccess(Void result) /*-{
						$wnd.location.reload(true);
					}-*/;
					@Override
					public void onFailure(Throwable caught) {
						EventBus.publish(new Event(EventbusTopic.ERROR), "Could not log out");
					}
				});
			}
		});
		addFormItem(logoutButton);
	}
	
	protected void addLoadingIndicator() {
		loadingLabel = new Label();
		loadingLabel.setIcon("/images/small_notloading.gif");
		loadingLabel.setIconSize(32);
		addMember(loadingLabel);
	}

}
