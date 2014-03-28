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

import com.genohm.viewsGWT.client.ResourceManager;
import com.genohm.viewsGWT.client.eventbus.Event;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.client.formitem.DynamicFormEditor;
import com.genohm.viewsGWT.client.records.MatchRecord;
import com.genohm.viewsGWT.shared.query.MatchField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGridEditorContext;
import com.smartgwt.client.widgets.grid.ListGridEditorCustomizer;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;

public class DynamicMatchTree extends TreeGrid {
	
	protected String featureTypeIRI = null;
	protected LinkedHashMap<String,String> matchMap = null;
	
	public String getFeatureTypeIRI() {
		return featureTypeIRI;
	}

	public void setFeatureTypeIRI(String featureTypeIRI) {
		this.featureTypeIRI = featureTypeIRI;
	}
	
	public DynamicMatchTree(String featureTypeIRI) {
		
		this.featureTypeIRI = featureTypeIRI;
		matchMap = new LinkedHashMap<String, String>();
		for (int i=0; i< MatchRecord.matchTypes.length; i++) {
			matchMap.put(Integer.toString(i), MatchRecord.matchTypes[i]);
		}

		setShowHeader(false);
		setNodeIcon("filter_blue.png");
		setFolderIcon("dropdown_filter_blue.png");
		setShowConnectors(false);
//		setAlwaysShowEditors(true);
		setShowRecordComponents(true);
		setShowRecordComponentsByCell(true);
		setCanResizeFields(true);
		
		TreeGridField typeField = new TreeGridField("matchClass");
		typeField.setCellFormatter(new CellFormatter() {
			
			@Override
			public String format(Object value, ListGridRecord record, int rowNum,
					int colNum) {
				return MatchRecord.matchTypes[(Integer) value];
			}
		});
		typeField.setWidth(150);
		
		
		TreeGridField parameterField = new TreeGridField("matchParameters");
		parameterField.setTitle("Parameters");
		parameterField.setCellFormatter(new CellFormatter() {
			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				MatchRecord matchRecord = (MatchRecord) record;
				return matchRecord.toString();
			}   
		});
		parameterField.setEscapeHTML(true);
		parameterField.setCanEdit(true);
		
		TreeGridField addButtonField = new TreeGridField("addrecord");
		addButtonField.setWidth(100);
		addButtonField.setCellAlign(Alignment.CENTER);
		
		
		TreeGridField removeButtonField = new TreeGridField("removerecord");
		removeButtonField.setWidth(40);
		removeButtonField.setType(ListGridFieldType.ICON);
		removeButtonField.setCellIcon(ResourceManager.IMAGE_REMOVE);
		removeButtonField.addRecordClickHandler(new RecordClickHandler() {
			
			@Override
			public void onRecordClick(RecordClickEvent event) {
				discardAllEdits();
				if (event.getRecord() != null) {
					getData().remove((TreeNode) event.getRecord());
				}
			}
		});
		
		setEditorCustomizer(new ListGridEditorCustomizer() {
		@Override
		public FormItem getEditor(ListGridEditorContext context) {
			ListGridField editField = context.getEditField();
			MatchRecord record = (MatchRecord) context.getEditedRecord();
			if (editField.getName().equals("matchParameters")) {
				switch (record.getMatchType()) {
				case MatchRecord.MATCH_FIELD:
					MatchField match = (MatchField) record.getMatch();
					if (match.getTypeIRI() != null) {
						return new FieldMatchEditor(match.getTypeIRI(), context, DynamicMatchTree.this);
					} else {
						return new FieldMatchEditor(DynamicMatchTree.this.featureTypeIRI, context, DynamicMatchTree.this);
					}
				case MatchRecord.MATCH_LOCATION:
					IntegerItem start = new IntegerItem("start");
					start.setTitle("Start position");
					IntegerItem end = new IntegerItem("end");
					end.setTitle("End position");
					SelectItem contig = new SelectItem("contig");
					contig.setValueMap("1","2","3","4","5","6","7","8","9","10",
							"11","12","13","14","15","16","17","18","19","20",
							"21","22","X","Y");
					RadioGroupItem strand = new RadioGroupItem("strand");
					strand.setValueMap("Forward","Reverse","Any");
					strand.setValue("Any");
					FormItem[] fields = new FormItem[] {start, end, contig, strand};
					return new DynamicFormEditor(fields);
				default:
					return null;
				}
			}
			return null;
		}});
				
		setFields(typeField, parameterField, addButtonField, removeButtonField);
//		setTitleField("matchParameters");
			

	}
	
//	@Override
//	protected boolean canEditCell(int rowNum, int colNum) {
//		final MatchRecord matchRecord = (MatchRecord) this.getRecord(rowNum);
//		if (matchRecord != null) {
//			if (matchRecord.canHaveChildren() && "addrecord".equals(getFieldName(colNum))) {
//				return true;
//			}
//			if (matchRecord.getMatchType() ==  MatchRecord.MATCH_FIELD && colNum == 1) {
//				return true;
//			}
//		}
//		return false;
//	}
	
	@Override
	protected Canvas createRecordComponent(ListGridRecord record, Integer colNum) {
		super.createRecordComponent(record, colNum);
	        String fieldName = this.getFieldName(colNum);
	        final MatchRecord matchRecord = (MatchRecord) record;
//	        if (fieldName != null && fieldName.equals("remove record")) {
//	    		
//	    		ImgButton removeButton = new ImgButton();
//	    		removeButton.setWidth(16);
//	    		removeButton.setHeight(16);
//	    		removeButton.setSrc(ResourceManager.IMAGE_REMOVE);
//	    		removeButton.addClickHandler( new ClickHandler() {
//
//	    			@Override
//	    			public void onClick(ClickEvent event) {
//	    				getData().remove((TreeNode) record);
//	    			}
//	    		});
//
//	    		return removeButton;
//	        }
////	        if (fieldName != null && fieldName.equals("matchClass")) {
////	        	DynamicForm matchSelectForm = new DynamicForm();
////	        	SelectItem matchSelector = new SelectItem();
////	        	LinkedHashMap<String, Integer> classes = new LinkedHashMap<String, Integer>();
////	        	classes.put("MatchAll", MatchRecord.MATCH_ALL);
////	        	classes.put("MatchField", MatchRecord.MATCH_FIELD);
////	        	matchSelector.setValueMap(classes);
////	        	matchSelector.addChangedHandler(new ChangedHandler() {
////					@Override
////					public void onChanged(ChangedEvent event) {
////						Integer selectedType = (Integer) event.getValue();
////						matchRecord.setMatchType(selectedType);
////					}
////				});
////	        	return matchSelectForm;
////	        }
	        if (fieldName != null && fieldName.equals("addrecord")) {
	        	if (matchRecord.canHaveChildren() != null && matchRecord.canHaveChildren())
	        	{

	        		DynamicForm aForm = new DynamicForm();
	        		aForm.setHeight100();
	        		aForm.setWidth100();
	        		
	        		final SelectItem selectChildNodeType = new SelectItem();
	        		selectChildNodeType.setValueMap(matchMap);
	        		selectChildNodeType.setEmptyDisplayValue("Add child");
	        		selectChildNodeType.setName("selectChildNodeType");
	        		selectChildNodeType.setShowTitle(false);
	        		selectChildNodeType.addChangedHandler(new ChangedHandler() {
						
						@Override
						public void onChanged(ChangedEvent event) {
							String selectedType = (String) event.getValue();
							MatchRecord newMatch = new MatchRecord();
							newMatch.setMatchType(Integer.parseInt(selectedType));
							//TODO: make the matchfield editor more intelligent so it can handle typeless
							// fieldmatches => more generic selector possible without fixing featuretype in advance
							if (featureTypeIRI != null) newMatch.setAttribute("typeIRI", featureTypeIRI);
							else EventBus.publish(new Event(EventbusTopic.ERROR), "first select feature type");
							getData().add(newMatch, matchRecord);
							selectChildNodeType.setValue((String) null);
							getData().openFolder(matchRecord);
							int idx = DynamicMatchTree.this.getRecordIndex(newMatch);
							DynamicMatchTree.this.startEditing(idx, 1, false);
						}
					});
	        		selectChildNodeType.setWidth("100%");
	        		aForm.setItems(selectChildNodeType);
	        		aForm.setPadding(0);
	        		return aForm;

	        	} 
	        }
	        return null;

	}
	

}




