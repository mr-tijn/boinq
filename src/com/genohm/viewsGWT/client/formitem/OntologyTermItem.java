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

import java.util.List;

import com.genohm.viewsGWT.client.util.ExternalHandler;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemInitHandler;

public class OntologyTermItem extends TextItem {
	
	protected OntologyTermEditor editor;
	
	public OntologyTermItem(String name) {
		super(name);
		editor = new OntologyTermEditor(name, this);
		super.setEditorType(editor);
		super.setShouldSaveValue(true);
		super.setImplicitSave(true);
	}
	
	public OntologyTermItem(String name, CanvasItem targetItem) {
		super(name);
		editor = new OntologyTermEditor(name, targetItem);
		super.setEditorType(editor);
		super.setShouldSaveValue(true);
		super.setImplicitSave(true);
	}
	
	public String getTargetGraph() {
		return editor.getTargetGraph();
	}
	public void setTargetGraph(String targetGraph) {
		editor.setTargetGraph(targetGraph);
	}
	public String getTargetEndpoint() {
		return editor.getTargetEndpoint();
	}
	public void setTargetEndpoint(String targetEndpoint) {
		editor.setTargetEndpoint(targetEndpoint);
	}
	public String getTargetFilter() {
		return editor.getTargetFilter();
	}
	public void setTargetFilter(String targetFilter) {
		editor.setTargetFilter(targetFilter);
	}
	public String getMotherTerm() {
		return editor.getMotherTerm();
	}
	public void setMotherTerm(String motherTerm) {
		editor.setMotherTerm(motherTerm);
	}
	
	class OntologyTermEditor extends FormItem {

		protected CanvasItem canvasItem;
		protected String targetGraph;
		protected String targetEndpoint;
		protected String targetFilter;
		protected String motherTerm;

		public OntologyTermEditor(final String name, final CanvasItem targetItem) {
		    setInitHandler(new FormItemInitHandler() {
		    	@Override
				public void onInit(final FormItem editItem) {
					OntologyTermPicker picker = new OntologyTermPicker(targetGraph, targetEndpoint, targetFilter, motherTerm);
					picker.setWidth("60%");
					picker.setHeight("60%");
					picker.setLeft("20%");
					picker.setTop("20%");
					picker.addExternalSelectHandler(new ExternalHandler() {
						@Override
						public void onSuccess(Object result) {
							List<String> terms = (List<String>) result;
							final StringBuffer termList = new StringBuffer();
							final String termArray[] = new String[terms.size()];
							int i = 0;
							for (String term: terms) {
								termList.append(term);
								termList.append(";");
								termArray[i++] = term;
							}
							try {
								termList.deleteCharAt(termList.length()-1);
							} catch (StringIndexOutOfBoundsException e) {
								//swallow
							}
							// form around editable item is initialized with last values
							DynamicForm motherForm = editItem.getForm();
							Record changedParameters = motherForm.getValuesAsRecord();
							changedParameters.setAttribute(name, termArray);
							motherForm.setValues(changedParameters.toMap());
							motherForm.saveData();
							// saving into the form is saving into the template !
							// we need a handle to the actual mother item of the form to be able to save:
							targetItem.storeValue(changedParameters);
						}
						@Override
						public void onFail(Throwable t) {
						}
					});
					picker.show();
				}
			});
		}
		
		public OntologyTermEditor(final String name, final FormItem targetItem) {
		    setInitHandler(new FormItemInitHandler() {
		    	@Override
				public void onInit(FormItem editItem) {
					OntologyTermPicker picker = new OntologyTermPicker(targetGraph, targetEndpoint, targetFilter, motherTerm);
					picker.setWidth("60%");
					picker.setHeight("60%");
					picker.setLeft("20%");
					picker.setTop("20%");
					picker.addExternalSelectHandler(new ExternalHandler() {
						@Override
						public void onSuccess(Object result) {
							List<String> terms = (List<String>) result;
							final StringBuffer termList = new StringBuffer();
							final String termArray[] = new String[terms.size()];
							int i = 0;
							for (String term: terms) {
								termList.append(term);
								termList.append(";");
								termArray[i++] = term;
							}
							try {
								termList.deleteCharAt(termList.length()-1);
							} catch (StringIndexOutOfBoundsException e) {
								//swallow
							}
							// setValue does not trigger save to form
//							item.setValue(termList.toString());
							//item.getForm().setValue("valueExpression", termList.toString());
							targetItem.getForm().setValue(name, termArray);
							targetItem.getForm().saveData();
						}
						@Override
						public void onFail(Throwable t) {
						}
					});
					picker.show();
				}
			});
		}
		
		public String getTargetGraph() {
			return targetGraph;
		}
		public void setTargetGraph(String targetGraph) {
			this.targetGraph = targetGraph;
		}
		public String getTargetEndpoint() {
			return targetEndpoint;
		}
		public void setTargetEndpoint(String targetEndpoint) {
			this.targetEndpoint = targetEndpoint;
		}
		public String getTargetFilter() {
			return targetFilter;
		}
		public void setTargetFilter(String targetFilter) {
			this.targetFilter = targetFilter;
		}
		public String getMotherTerm() {
			return motherTerm;
		}
		public void setMotherTerm(String motherTerm) {
			this.motherTerm = motherTerm;
		}


	}

}
