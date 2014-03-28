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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.NamedFrame;
import com.smartgwt.client.types.Encoding;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.UploadItem;
import com.smartgwt.client.widgets.layout.VLayout;

public class UploadDialog extends Window{
	
	private VLayout mainLayout = new VLayout();
	private DynamicForm uploadForm;
	private IButton uploadButton;
	private NamedFrame targetFrame;
	
	public UploadDialog() {
		initComplete(this);
		addItem(mainLayout);
		addUploadForm();
		addUploadButton();
		createHiddenTargetFrame();
	}
	
	public void uploadComplete(String message) {
		// response frame contains js that calls window uploadComplete which in turn calls this
		SC.say(message);
		destroy();
	}
	
	// point to uploadComplete method of this class
	private native void initComplete(UploadDialog upload) /*-{
	   $wnd.uploadComplete = function (message) {
	       upload.@com.genohm.viewsGWT.client.dialog.UploadDialog::uploadComplete(Ljava/lang/String;)(message);
	   };
	}-*/;
	
	private void createHiddenTargetFrame() {
		targetFrame = new NamedFrame("hidden_upload_target_frame");
		targetFrame.setWidth("1");
		targetFrame.setHeight("1");
		targetFrame.setVisible(false);
		mainLayout.addMember(targetFrame);
	}
	
	private void addUploadButton() {
		uploadButton = new IButton("Upload");
		uploadButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler()
		{
			@Override
			public void onClick(
					com.smartgwt.client.widgets.events.ClickEvent event) {
				// TODO Auto-generated method stub
				uploadForm.submitForm();
			}
		});
		mainLayout.addMember(uploadButton);
	}

	protected void addUploadForm() {
		uploadForm = new DynamicForm();
		uploadForm.setSize("54px", "147px");
		uploadForm.setEncoding(Encoding.MULTIPART);
		UploadItem fileItem = new UploadItem("select");
		uploadForm.setTarget("hidden_upload_target_frame");
		uploadForm.setAction(GWT.getModuleBaseURL()+"upload");
		uploadForm.setItems(fileItem);
		mainLayout.addMember(uploadForm);
	}     
         

}
