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

import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemInitHandler;
import com.smartgwt.client.widgets.form.fields.events.ShowValueEvent;
import com.smartgwt.client.widgets.form.fields.events.ShowValueHandler;
import com.smartgwt.client.widgets.form.validator.Validator;

public class ComparisonItem extends CanvasItem {

	protected DynamicForm mainForm;
	protected SelectItem comparatorSelect;
	protected TextItem valueItem;
	// needed for manually putting result value
	protected CanvasItem targetItem;

	public ComparisonItem() {
		super();
		create();
	}
	
	public ComparisonItem(String name, CanvasItem targetItem) {
		super(name);
		this.targetItem = targetItem;
		create();
	}
	
	protected void create() {
		setAutoDestroy(true);
		mainForm = new DynamicForm();
		comparatorSelect = new SelectItem("comparator");
		comparatorSelect.setValueMap("=","<",">","<=",">=","!=");
		comparatorSelect.setShowTitle(false);
		comparatorSelect.setWidth(40);
		valueItem = new TextItem("value");
		valueItem.setShowTitle(false);
		mainForm.setItems(comparatorSelect,valueItem);
        //mainForm.setNumCols(4);
	    setInitHandler(new FormItemInitHandler() {
			@Override
			public void onInit(FormItem item) {
				init(item);
			}
		});
	}
	
	public void setValueValidators(Validator... validators) {
		valueItem.setValidators(validators);
	}
	
	protected Record parseVal(String value) {
		Record result = new Record();
		String comp = "";
		String val = "";
		int pos = 0;
		char token = value.charAt(pos++);
		while ("=<>! ".indexOf(token) >= 0 && pos < value.length()) {
			comp += token;
			token = value.charAt(pos++);
		}
		val = value.substring(pos-1);
		comp = comp.trim();
		val = val.trim();
		if (comp != "") result.setAttribute("comparator", comp);
		if (val != "") result.setAttribute("value",val);
		return result;
	}
	
	protected String renderVal(Record value) {
		return value.getAttributeAsString("comparator") + " " + (value.getAttributeAsString("value") == null ? "" : value.getAttributeAsString("value"));
	}
	
	protected void init(FormItem item) {
		final CanvasItem canvasItem = new CanvasItem(item.getJsObj());
		canvasItem.setCanvas(mainForm);
		canvasItem.addShowValueHandler(new ShowValueHandler() {
			@Override
			public void onShowValue(ShowValueEvent event) {
				String param = (String) event.getDataValue();
				if (param != null) {
					mainForm.editRecord(parseVal(param));
				}
//				Record parameters = event.getDataValueAsRecord();
//				if (parameters != null) {
//					mainForm.editRecord(parameters);
//				}
			}
		});
	
		mainForm.addItemChangedHandler(new ItemChangedHandler() {
			
			@Override
			public void onItemChanged(ItemChangedEvent event) {
				
//				DynamicForm form = (DynamicForm) event.getSource();
//				Record changedParameters = form.getValuesAsRecord();
//				targetItem.storeValue(changedParameters);

				
				String compString = renderVal(mainForm.getValuesAsRecord());
				DynamicForm motherForm = canvasItem.getForm();
//				motherForm.setValue("valueExpression", compString); //FIXME: not working ??
				Record changedParameters = motherForm.getValuesAsRecord();
				changedParameters.setAttribute(getName(), compString);
				motherForm.setValues(changedParameters.toMap());
				motherForm.saveData();
				targetItem.storeValue(changedParameters);

//				if (canvasItem.getForm().getCanvasItem() != null) {
//					CanvasItem motherItem = canvasItem.getForm().getCanvasItem();
//					Record changedParameters = motherForm.getValuesAsRecord();
//					changedParameters.setAttribute("valueExpression", compString);
//					String valueExpression = changedParameters.getAttribute("valueExpression");
//					motherItem.storeValue(changedParameters);
//				}
			}
		});
	}

	
	
}
