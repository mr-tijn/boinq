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
package com.genohm.viewsGWT.client.eventbus;



public enum EventbusTopic {
	
	CHANGE_GENOMICREGION("Change genomic region request"),
	VIEWPORT_CHANGED("Viewport changed"),
	VIEWCONTEXT_CHANGED("Viewcontext changed"),
	SPECIES_CHANGED("Species changed"),
	CONTIG_CHANGED("Chromosome changed"),
	FEATURE_SELECTED("Feature selected"),
	GENE_SELECTED("Gene selected"),
	SELECTED_FEATURE_FETCHED("The feature that was selected has been fetched"),
	FOCUSEDFEATURE_LOADED("Focused feature loaded"),
	SEARCH_FOR_GENES("Search for genes"),
	DRAG_SELECT("Drag select"),
	DRAG_MOVE("Drag move"),
	RESET_DRAWPANE("Reset drawpane"),
	MOVE_SELECT("Move select"),
	RESET_DRAG("Reset drag"),
	START_WAIT("Start waiting"),
	END_WAIT("End waiting"),
	ERROR("Error"),
	ADD_TRACK("Add track"),
	REFRESH_ROI("Refresh regions of interest"),
	REFRESH_ANALYSIS("Refresh analyses");
	
	protected String description;
	EventbusTopic(String description) {
		this.description = description;
	}
	
}
