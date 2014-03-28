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

import com.genohm.viewsGWT.shared.data.ensembl.EnsemblGene;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class GeneRecord extends ListGridRecord {
	GeneRecord(EnsemblGene gene) {
		setChromosome(gene.getChromosome().getName());
		setStartPos(gene.getStartPos());
		setEndPos(gene.getEndPos());
		setName(gene.getDisplayXref().getDisplayLabel());
		setDescription(gene.getDescription());
		setEnsemblId(gene.getEnsemblId());
	}

	public void setName(String name) {
		setAttribute("name", name);
	}
	
	public void setDescription(String description) {
		setAttribute("description", description);
	}
	
	public void setEnsemblId(String ensemblId) {
		setAttribute("ensembl_id", ensemblId);
	}
	
	public Long getStartPos() {
		return getAttributeAsLong("startPos");
	}

	public void setStartPos(Long startPos) {
		setAttribute("startPos", startPos);
	}

	public Long getEndPos() {
		return getAttributeAsLong("endPos");
	}

	public void setEndPos(Long endPos) {
		setAttribute("endPos", endPos);
	}

	public String getChromosome() {
		return getAttributeAsString("chromosome");
	}

	public void setChromosome(String chromosome) {
		setAttribute("chromosome", chromosome);
	}

	public String getEnsemblId() {
		return getAttributeAsString("ensembl_id");
	}
}
