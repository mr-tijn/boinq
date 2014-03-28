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
package com.genohm.viewsGWT.client.formitem;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.genohm.viewsGWT.client.ViewsGWT;
import com.genohm.viewsGWT.client.components.DynamicMatchTree;
import com.genohm.viewsGWT.client.eventbus.Event;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.client.records.MatchRecord;
import com.genohm.viewsGWT.shared.data.SPARQLResultSet;
import com.genohm.viewsGWT.shared.fieldconfig.FieldConfig;
import com.genohm.viewsGWT.shared.fieldconfig.FieldType;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemInitHandler;
import com.smartgwt.client.widgets.form.fields.events.ShowValueEvent;
import com.smartgwt.client.widgets.form.fields.events.ShowValueHandler;
import com.smartgwt.client.widgets.form.validator.FloatRangeValidator;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;
import com.smartgwt.client.widgets.form.validator.Validator;
import com.smartgwt.client.widgets.grid.ListGridEditorContext;


public class MatchFieldEditor extends CanvasItem {
	
	
	public static final String defaultPrefixes = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
			"PREFIX track: <http://www.boinq.org/track/>\n"+
			"PREFIX obo: <http://purl.obolibrary.org/obo/>";

	protected SelectItem fieldSelect = new SelectItem("fieldIRI");
	protected LinkedHashMap<String,String> fields = new LinkedHashMap<String, String>();
	protected LinkedHashMap<String, FieldConfig> fieldConfigs = new LinkedHashMap<String, FieldConfig>();
	protected List<FieldConfig> fieldList = null;
	protected ListGridEditorContext context = null;
	protected DynamicMatchTree tree = null;
	
	public void setFeatureType(String featureTypeIRI) {
		//fetchProperties(featureTypeIRI);
		getFields(featureTypeIRI);
	}
	
	
	protected FormItem getItem(FieldConfig config) {
		FormItem item = null;
		if (config == null) return item;
		switch (config.getType()) {
		case STRING_TYPE:
			//TODO: remove submatch on simple types
			item = new TextItem("valueExpression");
			break;   
		case BOOLEAN_TYPE: 
			item = new BooleanItem("valueExpression");
			break;
		case INTEGER_TYPE:
			item = new TextItem("valueExpression");
			String numericExpression =  "\\s*[\\d\\.]+\\s*";
			String relationalExpression  = "^\\s*\\?value\\s*(=" + numericExpression + "|!=" + numericExpression + "|<" + numericExpression + "|>" + numericExpression + "|<=" + numericExpression + "|>=" + numericExpression +")\\s*$";
			Validator numericExpressionValidator = new RegExpValidator(relationalExpression);
			//integerValidator.setMin(0);
			numericExpressionValidator.setErrorMessage("Please use a relational expression using the string \"?value\" to denote the match. E.g. \"?value = 1000\" or \"?value >= 10\"");
			item.setValidators(numericExpressionValidator);
			item.setValidateOnExit(true);
			item.setShowErrorIcon(true);
			item.setValidateOnChange(true);
			//((TextItem) item).setMask("\\?v\\alue [<>=0-9 ][<>=0-9 ][<>=0-9 ][<>=0-9 ][<>=0-9 ][<>=0-9 ][<>=0-9 ][<>=0-9 ][<>=0-9 ][<>=0-9 ][<>=0-9 ][<>=0-9 ][<>=0-9 ][<>=0-9 ][<>=0-9 ][<>=0-9 ][<>=0-9 ][<>=0-9 ]");
			break;
		case DECIMAL_TYPE:
			item = new TextItem("valueExpression");
			FloatRangeValidator floatValidator = new FloatRangeValidator();
			item.setValidators(floatValidator);
			break;
		case TERM_TYPE:
			OntologyTermItem otItem = new OntologyTermItem("valueExpression");
			otItem.setTargetEndpoint(config.getTargetEndpoint());
			otItem.setTargetGraph(config.getTargetGraph());
			otItem.setTargetFilter(config.getTargetFilter());
			otItem.setMotherTerm(config.getMotherTerm());
			item = otItem;
			item.setImplicitSave(true);
			item.setShouldSaveValue(true);
			break;
		case INTERNAL_TERM_TYPE:
		case LOCATION_TYPE:
		case UNKNOWN_TYPE:
			GenericTermItem gtItem = new GenericTermItem("valueExpression");
			item = gtItem;
		default:
			item = new StaticTextItem("valueExpression");
			item.setValue("Unknown type");
			item.disable();
		}
		item.setShowTitle(false);
		return item;
	}
	
	
	public void getFields(final String entityIRI) {
		fieldSelect.disable();
		ViewsGWT.getViewsServer().getFields(entityIRI, new AsyncCallback<List<FieldConfig>>() {
			@Override
			public void onSuccess(List<FieldConfig> fieldList) {
				MatchFieldEditor.this.fieldList = fieldList;
				for (FieldConfig config: fieldList) {
					fields.put(config.getIRI(),(config.getName()!=null?config.getName():config.getIRI()));
					fieldConfigs.put(config.getIRI(), config);
				}
				fieldSelect.setValueMap(fields);
				fieldSelect.enable();
			}
			@Override
			public void onFailure(Throwable caught) {
				EventBus.publish(new Event(EventbusTopic.ERROR), "Could not fetch fields for " + entityIRI);
			}
		});
	}
	
	public MatchFieldEditor(final ListGridEditorContext context, final DynamicMatchTree tree) {
		this.context = context;
		this.tree = tree;
		MatchRecord matchRecord = (MatchRecord) context.getEditedRecord();
		String featureTypeIRI = matchRecord.getAttributeAsString("typeIRI");
		getFields(featureTypeIRI);
		final DynamicForm form =  new DynamicForm();
		form.setFields(fieldSelect);
		fieldSelect.setShowTitle(false);
		fieldSelect.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				String fieldIRI = (String) event.getValue();
				FieldConfig config = fieldConfigs.get(fieldIRI);
				if (config.getType() != FieldType.INTERNAL_TERM_TYPE) {
					FormItem item = getItem(config);
					form.setFields(fieldSelect, item);
				} else {
					MatchRecord newMatch = new MatchRecord();
					MatchRecord parentMatch = (MatchRecord) context.getEditedRecord();
					newMatch.setMatchType(MatchRecord.MATCH_FIELD);
					newMatch.setAttribute("typeIRI", config.getRange());
					tree.getData().add(newMatch, parentMatch);
					tree.getData().openFolder(parentMatch);
				}
			}
		});
		setInitHandler(new FormItemInitHandler() {
			@Override
			public void onInit(FormItem template) {
				final CanvasItem canvasItem = new CanvasItem(template.getJsObj());
				canvasItem.setCanvas(form);
				canvasItem.addShowValueHandler(new ShowValueHandler() {					
					@Override
					public void onShowValue(ShowValueEvent event) {
						Record parameters = event.getDataValueAsRecord();
						if (parameters != null) {
							form.editRecord(parameters);
//							if (parameters.getAttributeAsString("valueExpression") != null) {
//								String fieldIRI = (String) fieldSelect.getValue();
//								FieldConfig config = fieldConfigs.get(fieldIRI);
//								// FIXME: getFields fills fieldConfigs too late
//								// also triggers two editors
////								if (config != null) {
////									form.setFields(fieldSelect,getItem(config));
////								} else {
//									TextItem item = new TextItem("valueExpression");
//									item.setShowTitle(false);
//									form.setFields(fieldSelect, item);
////								}
//								//Integer type = Integer.parseInt(fieldSelect.getValueAsString());
//								//form.setFields(fieldSelect,getItem(type));
//							} 
						}
					}
				});
				form.addItemChangedHandler(new ItemChangedHandler() {
					
					@Override
					public void onItemChanged(ItemChangedEvent event) {
						DynamicForm form = (DynamicForm) event.getSource();
						Record changedParameters = form.getValuesAsRecord();
						canvasItem.storeValue(changedParameters);
					}
				});
				canvasItem.setShouldSaveValue(true);
			}
	    });	}
}
