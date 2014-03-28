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

import java.util.LinkedList;
import java.util.List;

import com.genohm.viewsGWT.client.eventbus.Event;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.shared.data.ensembl.EnsemblGene;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;


public class GeneSelector extends Window {

	VLayout mainLayout = new VLayout();
	ListGrid geneGrid = null;


	public GeneSelector() {
		super();
		setTitle("Select gene");
		setDismissOnEscape(true);
		setDismissOnOutsideClick(true);
		setCanDragResize(true);
		addMember(mainLayout);
		addGrid();
	}

	public void addGrid() {
		geneGrid = new ListGrid();
		geneGrid.setWidth100();
		ListGridField nameField = new ListGridField("name");
		ListGridField descriptionField = new ListGridField("description");
		ListGridField ensemblIdField = new ListGridField("ensembl_id");
		ensemblIdField.setHidden(true);
		geneGrid.setFields(nameField, descriptionField, ensemblIdField);
		geneGrid.addRecordClickHandler(new RecordClickHandler() {
			@Override
			public void onRecordClick(RecordClickEvent event) {
//				SC.say("You selected gene " + ((GeneRecord) event.getRecord()).getEnsemblId());
				EventBus.publish(new Event(EventbusTopic.GENE_SELECTED), event.getRecord());
				close();
			}
		});
		geneGrid.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				close();
			}
		});
		mainLayout.addChild(geneGrid);
	}

	public void close() {
		hide();
	}
	
	public void setData(List<EnsemblGene> genes) {
		GeneRecord records[] = new GeneRecord[genes.size()];
		int idx = 0;
		for (EnsemblGene gene: genes) {
			records[idx++] = new GeneRecord(gene);
		}
		geneGrid.setData(records);
	}
	
}

