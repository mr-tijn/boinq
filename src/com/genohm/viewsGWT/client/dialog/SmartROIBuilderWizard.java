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
package com.genohm.viewsGWT.client.dialog;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.genohm.viewsGWT.client.ViewsGWT;
import com.genohm.viewsGWT.client.components.FilterSection;
import com.genohm.viewsGWT.client.eventbus.Event;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.client.util.ExternalHandler;
import com.genohm.viewsGWT.shared.RegionOfInterest;
import com.genohm.viewsGWT.shared.data.Term;
import com.genohm.viewsGWT.shared.query.Match;
import com.genohm.viewsGWT.shared.query.MatchAll;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.drawing.DrawPane;
import com.smartgwt.client.widgets.drawing.DrawRect;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
import com.smartgwt.client.widgets.layout.VLayout;

public class SmartROIBuilderWizard extends Window {
	
	
	public static final int UNKNOWN_TYPE = 0;
	public static final int INTEGER_TYPE = 1;
	public static final int DECIMAL_TYPE = 2;
	public static final int BOOLEAN_TYPE = 3;
	public static final int STRING_TYPE = 4;
	public static final int TERM_TYPE = 5;
	
//	public static final String defaultPrefixes = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
//			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
//			"PREFIX track: <http://www.boinq.org/track/>\n"+
//			"PREFIX obo: <http://purl.obolibrary.org/obo/>";

	
	protected VLayout mainLayout;
	protected SelectItem dataSourceSelect;
	protected SelectItem featureTypeSelect;
	protected Map<String,String> properties;
	protected ButtonItem dataUpload;
	protected Match mainMatch;
	protected FilterSection filterSection;
	protected DynamicForm regionalParameterForm;
	protected DrawRect selectRect;
	protected DrawRect featureRect;
	protected DrawRect upstreamRect;
	protected DrawRect downstreamRect;
	private DynamicForm generalParameterForm;
	
	public SmartROIBuilderWizard() {
		setTitle("Build your Region of Interest");
		addHandlers();
		addMainLayout();
		addGeneralParameters();
		addIllustration();
		addRegionalParameters();
		addTrackSelect();
		addFilterSection();
		fetchDatasources();
	}
	
	protected void addMainLayout() {
		mainLayout = new VLayout();
		addItem(mainLayout);
	}

	public void fetchDatasources() {
		ViewsGWT.getViewsServer().getDatasources(new AsyncCallback<List<Term>>() {
			@Override
			public void onSuccess(List<Term> datasources) {
				LinkedHashMap<String,String> ds = new LinkedHashMap<String, String>();
				if (datasources != null) {
					for (Term datasourceTerm: datasources) {
						ds.put(datasourceTerm.getIri(),datasourceTerm.getName());
					}
					dataSourceSelect.setValueMap(ds);
					dataSourceSelect.enable();
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				EventBus.publish(new Event(EventbusTopic.ERROR),"Could not fetch datasources from meta service\n"+caught.getMessage());
			}
		});
		
	}
	
	public void fetchFeatureTypes(String dataSource) {
		ViewsGWT.getViewsServer().getFeatureTypes(dataSource, new AsyncCallback<List<Term>>() {
			@Override
			public void onSuccess(List<Term> featureTypes) {
				LinkedHashMap<String,String> ft = new LinkedHashMap<String, String>();
				if (featureTypes != null) {
					for (Term ftTerm: featureTypes) {
						ft.put(ftTerm.getIri(),ftTerm.getName());
					}
					featureTypeSelect.setValueMap(ft);
					featureTypeSelect.enable();
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				EventBus.publish(new Event(EventbusTopic.ERROR),"Could not fetch feature types from meta ontology\n"+caught.getMessage());
			}
		});
	}
	
	
	public void addTrackSelect() {
		DynamicForm theForm = new DynamicForm();
		dataSourceSelect = new SelectItem();
		dataSourceSelect.setTitle("Select datasource");
		dataSourceSelect.disable();
		dataSourceSelect.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				String selectedDataSource = (String) event.getValue();
				mainMatch = new MatchAll();
				mainMatch.setSourceGraph(selectedDataSource);
				fetchFeatureTypes(selectedDataSource);
			}
		});
		featureTypeSelect = new SelectItem();
		featureTypeSelect.setTitle("Select feature type");
		featureTypeSelect.disable();
		featureTypeSelect.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				String selectedFeatureType = (String) event.getValue();
				filterSection.setFeatureTypeIRI(selectedFeatureType);
			}
		});
		theForm.setFields(dataSourceSelect, featureTypeSelect);
		mainLayout.addMember(theForm);
	}
	
	public void addFilterSection() {
		filterSection = new FilterSection();
		filterSection.addExternalHandler(new ExternalHandler() {
			@Override
			public void onSuccess(Object expression) {
				Boolean includeUpstream = (Boolean) regionalParameterForm.getValue("includeUpstream");
				Boolean includeDownstream = (Boolean) regionalParameterForm.getValue("includeDownstream");
				Integer upstreamBP = 0;
				if (includeUpstream) {
					try {
						upstreamBP = Integer.parseInt((String) regionalParameterForm.getValue("upstreamBP"));
					} catch (NumberFormatException e) {
						// swallow
					}
				}
				Integer downstreamBP = 0;
				if (includeDownstream) {
					try {
						downstreamBP = Integer.parseInt((String) regionalParameterForm.getValue("downstreamBP"));
					} catch (NumberFormatException e) {
						// swallow
					}
				}
				String featureRegion = (String) regionalParameterForm.getValue("featureRegion");
				Boolean none = "None".equals(featureRegion);
				Boolean full = "Full feature".equals(featureRegion);
				Boolean first = "First".equals(featureRegion);
				Boolean last = "Last".equals(featureRegion);
				Integer featureBP = 0;
				if (first || last) {
					try {
						featureBP = Integer.parseInt((String) regionalParameterForm.getValue("featureBP"));
					} catch (NumberFormatException e) {
						// swallow
					}
				}
				Boolean selectStartRefersToFeatureStart = null;
				Boolean selectEndRefersToFeatureStart = null;
				Integer selectStartOffset = null;
				Integer selectEndOffset = null;
				if (none) {
					if (includeUpstream) {
						selectStartRefersToFeatureStart = true;
						selectStartOffset = -upstreamBP;
						selectEndRefersToFeatureStart = true;
						selectEndOffset = 0;
					} else if (includeDownstream) {
						selectStartRefersToFeatureStart = false;
						selectStartOffset = 0;
						selectEndRefersToFeatureStart = false;
						selectEndOffset = downstreamBP;
					} else {
						EventBus.publish(new Event(EventbusTopic.ERROR), "No region selected");
					}
				} else if (first) {
					selectStartRefersToFeatureStart = true;
					selectEndRefersToFeatureStart = true;
					selectEndOffset = featureBP;
					if (includeUpstream) {
						selectStartOffset = -upstreamBP;
					} else if (includeDownstream) {
						EventBus.publish(new Event(EventbusTopic.ERROR), "Non contiguous regions not supported");
					} else {
						selectStartOffset = 0;
					}
				} else if (last) {
					selectStartRefersToFeatureStart = false;
					selectEndRefersToFeatureStart = false;
					selectStartOffset = -featureBP;
					if (includeUpstream) {
						EventBus.publish(new Event(EventbusTopic.ERROR), "Non contiguous regions not supported");
					} else if (includeDownstream) {
						selectEndOffset = downstreamBP;
					} else {
						selectEndOffset = 0;
					}
				} else if (full) {
					selectStartRefersToFeatureStart = true;
					selectEndRefersToFeatureStart = false;
					selectStartOffset = 0;
					selectEndOffset = 0;
					if (includeUpstream) {
						selectStartOffset = -upstreamBP;
					} 
					if (includeDownstream) {
						selectEndOffset = downstreamBP;
					} 
				}
				RegionOfInterest roi = new RegionOfInterest();
				roi.setIsPublic((Boolean) generalParameterForm.getValue("isPublic"));
				roi.setName((String) generalParameterForm.getValue("roiName"));
				roi.setExpression((String) expression);
				roi.setRegionStartRefersToFeatureStart(selectStartRefersToFeatureStart);
				roi.setRegionStartOffset(selectStartOffset);
				roi.setRegionEndRefersToFeatureStart(selectEndRefersToFeatureStart);
				roi.setRegionEndOffset(selectEndOffset);
				roi.setStatus(RegionOfInterest.STATUS_PENDING);
				ViewsGWT.getViewsServer().saveROI(roi, new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						EventBus.publish(new Event(EventbusTopic.REFRESH_ROI), null);
						close();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						EventBus.publish(new Event(EventbusTopic.ERROR), "Could not save region of interest");
						close();

					}
				});
			}
			@Override
			public void onFail(Throwable t) {}
		});
		mainLayout.addMember(filterSection);
	}
	
	
	protected void drawSelection() {
		if ("None".equals(regionalParameterForm.getValue("featureRegion"))) {
			selectRect.moveTo(featureRect.getLeft(), selectRect.getTop());
			selectRect.resizeTo(featureRect.getWidth(), selectRect.getHeight());
			selectRect.hide();
		} else if ("Full feature".equals(regionalParameterForm.getValue("featureRegion"))) {
			selectRect.moveTo(featureRect.getLeft(), selectRect.getTop());
			selectRect.resizeTo(featureRect.getWidth(), selectRect.getHeight());
			selectRect.show();
		} else if ("First".equals(regionalParameterForm.getValue("featureRegion"))) {
			selectRect.moveTo(featureRect.getLeft(), selectRect.getTop());
			selectRect.resizeTo(featureRect.getWidth()/3, selectRect.getHeight());
			selectRect.show();
		} else if ("Last".equals(regionalParameterForm.getValue("featureRegion"))) {
			selectRect.moveTo(featureRect.getLeft()+2*featureRect.getWidth()/3, selectRect.getTop());
			selectRect.resizeTo(featureRect.getWidth()/3, selectRect.getHeight());
			selectRect.show();
		}
		if ((Boolean) regionalParameterForm.getValue("includeUpstream")) {
			upstreamRect.moveTo(featureRect.getLeft() - upstreamRect.getWidth(), upstreamRect.getTop());
			upstreamRect.show();
		} else {
			upstreamRect.hide();
		}
		if ((Boolean) regionalParameterForm.getValue("includeDownstream")) {
			downstreamRect.moveTo(featureRect.getLeft() + featureRect.getWidth(), upstreamRect.getTop());
			downstreamRect.show();
		} else {
			downstreamRect.hide();
		}
	}
	

	public void addIllustration() {
		DrawPane drawPane = new DrawPane();
		drawPane.setWidth(400);
		drawPane.setHeight(100);
		int height = drawPane.getInnerContentHeight();
		int dpWidth = drawPane.getInnerContentWidth();
		featureRect = new DrawRect();
		featureRect.setDrawPane(drawPane);
		featureRect.setFillColor("grey");
		featureRect.setTop(height/4);
		featureRect.setLeft(dpWidth/4);
		featureRect.setWidth(dpWidth/2);
		featureRect.setHeight(height/2);
		featureRect.draw();
		selectRect = new DrawRect();
		selectRect.setDrawPane(drawPane);
		selectRect.setFillColor("red");
		selectRect.setFillOpacity(0.5f);
		selectRect.setLineWidth(0);
		selectRect.setTop(height/5);
		selectRect.setLeft(featureRect.getLeft());
		selectRect.setWidth(featureRect.getWidth());
		selectRect.setHeight(3*height/5);
		selectRect.draw();
		upstreamRect = new DrawRect();
		upstreamRect.setDrawPane(drawPane);
		upstreamRect.setFillColor("red");
		upstreamRect.setWidth(dpWidth/5);
		upstreamRect.setLeft(featureRect.getLeft() - upstreamRect.getWidth());
		upstreamRect.setHeight(selectRect.getHeight());
		upstreamRect.setTop(selectRect.getTop());
		upstreamRect.setFillColor("red");
		upstreamRect.setFillOpacity(0.5f);
		upstreamRect.setLineWidth(0);
		upstreamRect.hide();
		upstreamRect.draw();
		downstreamRect = new DrawRect();
		downstreamRect.setDrawPane(drawPane);
		downstreamRect.setFillColor("red");
		downstreamRect.setWidth(dpWidth/5);
		downstreamRect.setLeft(featureRect.getLeft() + featureRect.getWidth());
		downstreamRect.setHeight(selectRect.getHeight());
		downstreamRect.setTop(selectRect.getTop());
		downstreamRect.setFillColor("red");
		downstreamRect.setFillOpacity(0.5f);
		downstreamRect.setLineWidth(0);
		downstreamRect.hide();
		downstreamRect.draw();
		drawPane.draw();
		mainLayout.addMember(drawPane);
	}
	
	public void addGeneralParameters() {
		generalParameterForm = new DynamicForm();
		generalParameterForm.setNumCols(4);
		TextItem roiName = new TextItem("roiName");
		roiName.setTitle("Name");
		roiName.setTooltip("Choose a name for your region of interest. If none is entered, one will be generated for you.");
		BooleanItem isPublic = new BooleanItem("isPublic");
		isPublic.setTitle("is public");
		isPublic.setValue(true);
		generalParameterForm.setFields(roiName, isPublic);
		mainLayout.addMember(generalParameterForm);
	}
	
	public void addRegionalParameters() {
		regionalParameterForm = new DynamicForm();
		final BooleanItem includeUpstream = new BooleanItem("includeUpstream");
		includeUpstream.setTitle("Include upstream region");
		includeUpstream.setValue(false);
		final TextItem upstreamBP = new TextItem("upstreamBP");
		upstreamBP.setTitle("Base Pairs");
		IntegerRangeValidator irv1 = new IntegerRangeValidator();
		irv1.setMin(0);
		upstreamBP.setValidators(irv1);
		upstreamBP.disable();
		final BooleanItem includeDownstream = new BooleanItem("includeDownstream");
		includeDownstream.setTitle("Include downstream region");
		includeDownstream.setValue(false);
		final TextItem downstreamBP = new TextItem("downstreamBP");
		downstreamBP.setTitle("Base Pairs");
		IntegerRangeValidator irv2 = new IntegerRangeValidator();
		irv2.setMin(0);
		downstreamBP.setValidators(irv2);
		downstreamBP.disable();
		includeDownstream.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				Boolean selected = (Boolean) event.getValue();
				if (selected) downstreamBP.enable();
				else downstreamBP.disable();
				drawSelection();
			}
		});
		final RadioGroupItem featurePart = new RadioGroupItem("featureRegion");
		featurePart.setTitle("Feature region");
		featurePart.setValueMap("None","Full feature","First","Last");
		featurePart.setDefaultValue("Full feature");
		final TextItem featureBP = new TextItem("featureBP");
		featureBP.setTitle("Base pairs");
		IntegerRangeValidator irv3 = new IntegerRangeValidator();
		irv3.setMin(0);
		featureBP.setValidators(irv3);
		featureBP.disable();
		includeUpstream.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				Boolean selected = (Boolean) event.getValue();
				if (selected && ("Last".equals(featurePart.getValue()) || ("None".equals(featurePart.getValue()) && (Boolean) includeDownstream.getValue()))) {
						EventBus.publish(new Event(EventbusTopic.ERROR), "Can only select contiguous regions. Choose another option for feature region");
						includeUpstream.setValue(false);
				}
			}
		});
		includeUpstream.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				Boolean selected = (Boolean) event.getValue();
				if (selected) {
					upstreamBP.enable();
				}
				else upstreamBP.disable();
				drawSelection();
			}
		});
		includeDownstream.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				Boolean selected = (Boolean) event.getValue();
				if (selected && ("First".equals(featurePart.getValue()) || ("None".equals(featurePart.getValue()) && (Boolean) includeUpstream.getValue()))) {
						EventBus.publish(new Event(EventbusTopic.ERROR), "Can only select contiguous regions. Choose another option for feature region");
						includeDownstream.setValue(false);
				}
			}
		});
		includeDownstream.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				Boolean selected = (Boolean) event.getValue();
				if (selected) {
					downstreamBP.enable();
				}
				else downstreamBP.disable();
				drawSelection();
			}
		});
		featurePart.addChangedHandler(new ChangedHandler() {			
			@Override
			public void onChanged(ChangedEvent event) {
				String selected = (String) event.getValue();
				if ("Full feature".equals(selected)) {
					featureBP.disable();
				} else if ("None".equals(selected)) {
					featureBP.disable();
					if ((Boolean) includeDownstream.getValue() && (Boolean) includeUpstream.getValue()) {
						includeDownstream.setValue(false);
					}
				} else if ("First".equals(selected)) {
					includeDownstream.setValue(false);
					downstreamBP.disable();
					featureBP.enable();
				} else if ("Last".equals(selected)) {
					includeUpstream.setValue(false);
					upstreamBP.disable();
					featureBP.enable();
				}
				drawSelection();
			}
		});
		regionalParameterForm.setFields(includeUpstream,upstreamBP,includeDownstream,downstreamBP,featurePart,featureBP);
		mainLayout.addMember(regionalParameterForm);

	}
	
	
	protected void addHandlers() {
		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				close();
			}
		});
	}
	
	protected void close() {
		destroy();
	}

	
}
