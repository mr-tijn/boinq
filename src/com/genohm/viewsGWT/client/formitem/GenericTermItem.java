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
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemInitHandler;
import com.smartgwt.client.widgets.form.fields.events.ShowValueEvent;
import com.smartgwt.client.widgets.form.fields.events.ShowValueHandler;

public class GenericTermItem extends CanvasItem {

	SelectItem termType = null;
	TextItem termValue = null;
	DynamicForm theForm = null;
	
	
	public GenericTermItem(String name) {
		super(name);
	    setInitHandler(new FormItemInitHandler() {
			@Override
			public void onInit(FormItem item) {
				init(item);
			}
		});
	}
	
	protected void init(FormItem item) {
		termType = new SelectItem("termType");
		termValue = new TextItem("termValue");
		termType.setValueMap("URI","xsd:integer","xsd:long","xsd:string");
		termType.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				String newTermType = (String) event.getValue();
			}
		});
		theForm = new DynamicForm();
        setShouldSaveValue(true);
        setSaveOnEnter(true);
        theForm.setFields(termType, termValue);
		final CanvasItem canvasItem = new CanvasItem(item.getJsObj());
		canvasItem.setCanvas(theForm);
		canvasItem.addShowValueHandler(new ShowValueHandler() {
			
			@Override
			public void onShowValue(ShowValueEvent event) {
				Record parameters = event.getDataValueAsRecord();
				if (parameters != null) {
					theForm.editRecord(parameters);
				}
			}
		});
		theForm.addItemChangedHandler(new ItemChangedHandler() {
			
			@Override
			public void onItemChanged(ItemChangedEvent event) {
				Record changedParameters = theForm.getValuesAsRecord();
				String value = null;
				String termType = changedParameters.getAttribute("termType");
				if ("URI".equals(termType)) {
					value = bracket(changedParameters.getAttribute("termValue"));
				} else {
					value = quote(changedParameters.getAttribute("termValue"))+"^^"+"termType";
				}
				canvasItem.storeValue(value);
			}

			private String bracket(String input) {
				String output = input;
				if (!input.startsWith("\"")) output = "\""+output;
				if (!input.endsWith("\"")) output = output + "\"";
				return output;
			}

			private String quote(String input) {
				String output = input;
				if (!input.startsWith("<")) output = "<"+output;
				if (!input.endsWith(">")) output = output + ">";
				return output;
			}
		});

		
	}
	

}
