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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.genohm.viewsGWT.client.ViewsGWT;
import com.genohm.viewsGWT.client.eventbus.Event;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.client.formitem.ComparisonItem;
import com.genohm.viewsGWT.client.formitem.GenericTermItem;
import com.genohm.viewsGWT.client.formitem.OntologyTermItem;
import com.genohm.viewsGWT.client.records.MatchRecord;
import com.genohm.viewsGWT.shared.data.Term;
import com.genohm.viewsGWT.shared.fieldconfig.FieldType;
import com.genohm.viewsGWT.shared.fieldconfig.Target;
import com.genohm.viewsGWT.shared.query.MatchField;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.events.FocusChangedEvent;
import com.smartgwt.client.widgets.events.FocusChangedHandler;
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
import com.smartgwt.client.widgets.grid.ListGridEditorContext;
import com.smartgwt.client.widgets.layout.VLayout;

public class FieldMatchEditor extends CanvasItem {
	
	protected String entityIRI = null;
	
	protected DynamicForm theForm = null;
	protected SelectItem propertySelect = null;
	protected SelectItem targetTypeSelect = null;
	protected Map<String,Target> targetMap = null;
	protected List<Target> defaultTargets;
	protected FormItem valueItem = null;
	private Set<HandlerRegistration> handlerRegistrations = new HashSet<HandlerRegistration>();
	protected CanvasItem canvasItem;
	
	final protected DynamicMatchTree tree;
	final protected ListGridEditorContext context;
	
	public FieldMatchEditor(String entityIRI, ListGridEditorContext context, DynamicMatchTree tree) {
		defaultTargets = new LinkedList<Target>();
		defaultTargets.add(new Target(FieldType.TERM_TYPE,"URI","<http://www.w3.org/2001/XMLSchema#anyURI>"));
		defaultTargets.add(new Target(FieldType.STRING_TYPE,"String literal","<http://www.w3.org/2001/XMLSchema#string>"));
		defaultTargets.add(new Target(FieldType.INTEGER_TYPE,"Integer literal","<http://www.w3.org/2001/XMLSchema#integer>"));
		this.entityIRI = entityIRI;
		this.context = context;
		this.tree = tree;
		setTitleAlign(Alignment.LEFT);
	    setInitHandler(new FormItemInitHandler() {
			@Override
			public void onInit(FormItem item) {
				init(item);
			}
		});
	}

	
	private void selectTarget(Target target) {
		addAttribute("targetFieldType", target.getType().value);
		switch (target.getType()) {
		case STRING_TYPE:
			//TODO: remove submatch on simple types
			valueItem = new TextItem("valueExpression");
			break;   
		case BOOLEAN_TYPE: 
			valueItem = new BooleanItem("valueExpression");
			break;
		case INTEGER_TYPE:
			ComparisonItem cItem = new ComparisonItem("valueExpression", this);
			cItem.setValueValidators(new IntegerRangeValidator());
			cItem.setValidateOnExit(true);
			cItem.setShowErrorIcon(true);
			cItem.setShowTitle(false);
			valueItem = cItem;
			break;
		case DECIMAL_TYPE:
			cItem = new ComparisonItem("valueExpression", this);
			cItem.setValueValidators(new FloatRangeValidator());
			cItem.setValidateOnExit(true);
			cItem.setShowErrorIcon(true);
			valueItem = cItem;
			break;
		case TERM_TYPE:
			addAttribute("targetFieldType",target.getType().value);
			OntologyTermItem otItem = new OntologyTermItem("valueExpression", this);
			otItem.setTargetEndpoint(target.getTargetEndpoint());
			otItem.setTargetGraph(target.getTargetGraph());
			otItem.setTargetFilter(target.getTargetFilter());
			//otItem.setMotherTerm(target.getMotherTerm());
			valueItem = otItem;
			valueItem.setImplicitSave(true);
			valueItem.setShouldSaveValue(true);
			break;
		case INTERNAL_TERM_TYPE:
			//should never get here
			
			addAttribute("targetFieldType", target.getType().value);
			MatchRecord newMatch = new MatchRecord();
			MatchRecord parentMatch = (MatchRecord)tree.getSelectedRecord();
			newMatch.setMatchType(MatchRecord.MATCH_FIELD);
			newMatch.setParameter("typeIRI", target.getIRI());
			tree.getData().add(newMatch, parentMatch);
			tree.getData().openFolder(parentMatch);

			
			
			return;
		case LOCATION_TYPE:
		case UNKNOWN_TYPE:
			GenericTermItem gtItem = new GenericTermItem("valueExpression");
			valueItem = gtItem;
		default:
			valueItem = new StaticTextItem("valueExpression");
			valueItem.setValue("Unknown type");
			valueItem.disable();
		}
		valueItem.setShowTitle(false);
		theForm.setItems(propertySelect,targetTypeSelect,valueItem);
		
	}

	
	protected void init(FormItem item) {
		propertySelect = new SelectItem("fieldIRI");
		propertySelect.setTitle("Property");
		propertySelect.disable();
		propertySelect.setWidth("*");
		propertySelect.setShowTitle(false);
		
		MatchRecord record = (MatchRecord) context.getEditedRecord();
		if (record.getMatchType() == MatchRecord.MATCH_FIELD) {
			MatchField match = (MatchField) record.getMatch();
			if (match.getTypeIRI() != null) {
				entityIRI = match.getTypeIRI();
			} 
		}
		
		propertySelect.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				getTargetTypes(entityIRI, (String) event.getValue());
			}
		});
				
		getProperties(entityIRI);
		
		targetTypeSelect = new SelectItem("type");
		targetTypeSelect.setTitle("Type");
		targetTypeSelect.disable();
		targetTypeSelect.setWidth("*");
		targetTypeSelect.setShowTitle(false);

		targetTypeSelect.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				Target target = targetMap.get(event.getValue().toString());
				if (target.getType() == FieldType.INTERNAL_TERM_TYPE) {
					MatchRecord newMatch = new MatchRecord();
					MatchRecord parentMatch = (MatchRecord) context.getEditedRecord();
					newMatch.setMatchType(MatchRecord.MATCH_FIELD);
					newMatch.setParameter("typeIRI", target.getIRI());
					tree.getData().add(newMatch, parentMatch);
					tree.getData().openFolder(parentMatch);
				} else {
					selectTarget(target);
				}
			}

			});
		
		setShouldSaveValue(true);
        setSaveOnEnter(true);

		
		theForm = new DynamicForm();
				
		theForm.setColWidths(new Object[]{"*"});
		theForm.setNumCols(1);
		theForm.setMargin(0);
		theForm.setPadding(0);
		theForm.setCellPadding(0);
		theForm.setWidth("*");
		theForm.setFields(propertySelect, targetTypeSelect);
		
        canvasItem = new CanvasItem(item.getJsObj());
        canvasItem.setAutoDestroy(true);
		canvasItem.setShouldSaveValue(true); 
		canvasItem.setRowSpan(1);
		//canvasItem.setColSpan("*");
		canvasItem.setWidth("100%");
		
		canvasItem.addShowValueHandler(new ShowValueHandler() {
			@Override
			public void onShowValue(ShowValueEvent event) {
				Record parameters = event.getDataValueAsRecord();
				if (parameters != null) {
					theForm.editRecord(parameters);
					// parameters should be : 
				}
			}
		});
		canvasItem.setCanvas(theForm);
		theForm.addItemChangedHandler(new ItemChangedHandler() {
			@Override
			public void onItemChanged(ItemChangedEvent event) {
				DynamicForm form = (DynamicForm) event.getSource();
				Record changedParameters = form.getValuesAsRecord();
				storeValue(changedParameters);
			}});
	}

	@Override
	public void storeValue(Record value) {
		canvasItem.storeValue(value);
//		canvasItem.getForm().setValue(canvasItem.getFieldName(), value);
//		canvasItem.getForm().saveData();
		context.getEditedRecord().setAttribute(canvasItem.getFieldName(), value);
	}
	
	public void addAttribute(String name, Object value) {
		Record values = context.getEditedRecord().getAttributeAsRecord(canvasItem.getFieldName());
		values.setAttribute(name,  value);
	}
	
	//TODO: put information about the target type in the matchField
	//so it has an easier time rendering it / or improve the valueExpression to be more complete
	
	private void getProperties(final String entityIRI) {
		propertySelect.disable();
		ViewsGWT.getViewsServer().getProperties(entityIRI, new AsyncCallback<List<Term>>() {
			@Override
			public void onSuccess(List<Term> result) {
				propertySelect.setValueMap(valueMapFromTermList(result));
				propertySelect.enable();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				EventBus.publish(new Event(EventbusTopic.ERROR), "Could not fetch properties for "+entityIRI);
			}
		});
		
	}

	private void getTargetTypes(final String entityIRI, final String propertyIRI) {
		targetTypeSelect.disable();
		ViewsGWT.getViewsServer().getTargetTypes(entityIRI, propertyIRI, new AsyncCallback<List<Target>>() {
			@Override
			public void onSuccess(List<Target> result) {
				if (result != null) {
					setTargetTypes(result);
					if (result.size() == 1) {
						Target target = result.get(0);
						targetTypeSelect.setValueMap(valueMapFromTargetList(result));
						targetTypeSelect.setValue(target.getIRI());

//						if (target.getType() == FieldType.INTERNAL_TERM_TYPE) {
//							addAttribute("targetFieldType", target.getType().value);
//							MatchRecord newMatch = new MatchRecord();
//							MatchRecord parentMatch = (MatchRecord)tree.getSelectedRecord();
//							newMatch.setMatchType(MatchRecord.MATCH_FIELD);
//							newMatch.setParameter("typeIRI", target.getIRI());
//							tree.getData().add(newMatch, parentMatch);
//							tree.getData().openFolder(parentMatch);
//						} else {
//							selectTarget(target);
//						}
						
						selectTarget(target);
						
//						
					} else if (result.size() > 1) {
						targetTypeSelect.setValueMap(valueMapFromTargetList(result));
						targetTypeSelect.enable();
					}
				} else {
					setTargetTypes(defaultTargets);
					targetTypeSelect.setValueMap(valueMapFromTargetList(defaultTargets));
					targetTypeSelect.enable();
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				EventBus.publish(new Event(EventbusTopic.ERROR), "Could not fetch target types for "+entityIRI+" over "+propertyIRI);
			}
		});
		
	}

	protected void setTargetTypes(List<Target> result) {
		targetMap = new HashMap<String, Target>();
		for (Target target: result) {
			targetMap.put(target.getIRI(), target);
		}
	}
	
	protected LinkedHashMap<String, String> valueMapFromTargetList(List<Target> termList) {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		for (Target target: termList) {
			result.put(target.getIRI(), target.getName());
		}
		return result;
	}

	protected LinkedHashMap<String, String> valueMapFromTermList(List<Term> termList) {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		for (Term term: termList) {
			result.put(term.getIri(), term.getName());
		}
		return result;
	}
	
	public void addHandlerRegistration(HandlerRegistration handlerRegistration) {
		handlerRegistrations.add(handlerRegistration);
	}
	

	
}
