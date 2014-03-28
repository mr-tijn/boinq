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

import java.util.ArrayList;
import java.util.List;

import com.genohm.viewsGWT.client.ResourceManager;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VLayout;

public abstract class AWindow extends Window{

	private int width;
	private int height;
	private String title = null;
	private String backgroundImage = null;
	private String confirmText = "confirm";
	private String cancelText = "cancel";
	
	private boolean addConfirmButton = true;
	private boolean addCancelButton = true;
	private boolean destroyed = false;
	
	public AWindow(int width, int height, String title) {
		this(width, height, title, ResourceManager.IMAGE_TOP_BACK_LARGE);
	}
	
	public AWindow(int width, int height, String title, String backGroundImage) {
		this(width, height, title, backGroundImage, true);
	}
	
	public AWindow(int width, int height, String title, String backGroundImage, boolean addCancelButton) {
		this.width = width;
		this.height = height;
		this.title = title;
		this.backgroundImage = backGroundImage;
		this.addCancelButton = addCancelButton;
	}
	
	public void setConfirmText(String confirmText) {
		this.confirmText = confirmText;
	}

	public void setCancelText(String cancelText) {
		this.cancelText = cancelText;
	}

	public void show() {
		if (!destroyed) {
			init();
			super.show();
			postInitialize();
		}
	}
	
	private void init() {
		initialize();
		createLayout();
		addHandlers();
	}
	
	protected abstract void initialize();
	protected abstract void addContentToLayout(VLayout mainLayout);
	
	protected void postInitialize() {
		//default do nothing;
	}
	
	private void addHandlers() {
		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				cancel();
			}
		});
	}
	
	protected void createLayout() {
		final VLayout mainLayout = new VLayout();
		mainLayout.setWidth100();
		mainLayout.setHeight100();
		mainLayout.setMargin(0);
		mainLayout.setOverflow(Overflow.HIDDEN);
		
		final VLayout contentLayout = new VLayout();
		contentLayout.setOverflow(Overflow.AUTO);
		
		mainLayout.setMembersMargin(0);
		if (backgroundImage != null) {
			setBackgroundImage(backgroundImage);
		}
		setWidth(width);
		setHeight(height);
		setTitle(title);
		setShowMinimizeButton(false);
		setShowResizer(true);
		setCanDragResize(true);
		setShowModalMask(true);
		setIsModal(true);
		setShowShadow(true);
		setShowCloseButton(addCancelButton);
		this.setBorder("0px");
		centerInPage();

		addContentToLayout(contentLayout);
		mainLayout.addMember(contentLayout);
		mainLayout.addMember(createButtonStack());				
		
		addItem(mainLayout);
	}
		
	private Canvas createButtonStack() {
		List<IButton> buttonList = getAllButtons();

		HStack buttonStack = new HStack();
		buttonStack.setWidth100();
		buttonStack.setAlign(Alignment.RIGHT);
		buttonStack.setHeight(22);
		buttonStack.setMembersMargin(5);
		buttonStack.setMargin(5);
		for (IButton button : buttonList) {
			if (button != null) {
				buttonStack.addMember(button);
			}
		}
		return buttonStack;
	}

	protected List<IButton> getAllButtons() {
		List<IButton> buttonList = new ArrayList<IButton>();
		
		if (addConfirmButton) {
			IButton confirmButton = getConfirmButton();
			if (confirmButton != null) {
				confirmButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
					public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
						confirm();
					}
				});
			}
			buttonList.add(confirmButton);
		}
		if (addCancelButton) {
			IButton cancelButton = getCancelButton();
			cancelButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
				public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
					cancel();
				}
			});
			buttonList.add(cancelButton);
		}

		return buttonList;
	}
	
	protected IButton getConfirmButton() {
		IButton confirmButton = new IButton(confirmText);
		confirmButton.setIcon(ResourceManager.IMAGE_OK);
		return confirmButton;
	}
	
	protected IButton getCancelButton() {
		IButton cancelButton = new IButton(cancelText);
		cancelButton.setIcon(ResourceManager.IMAGE_CANCEL);
		return cancelButton;
	}
	
	protected void confirm() {
		destroy();
	}
	
	protected void cancel() {
		destroy();
	}
	
	@Override
	public void destroy() {
		destroyed = true;
		super.destroy();
	}
	
	public int getId() {
		return hashCode();
	}

	public void setAddConfirmButton(boolean addConfirmButton) {
		this.addConfirmButton = addConfirmButton;
	}

	public void setAddCancelButton(boolean addCancelButton) {
		this.addCancelButton = addCancelButton;
	}
}
