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
package com.genohm.viewsGWT.shared.data.ensembl;

import com.google.gwt.user.client.rpc.IsSerializable;

public class EnsemblGeneData implements IsSerializable {
	
	public EnsemblGeneData() {}
	
	private String ensemblID;
	private String geneID;
	private String label;
	private String description ;
	private long startPos;
	private long endPos;
	private int strand;
	private String type;
	private String chromosome;

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getStartPos() {
		return startPos;
	}
	public void setStartPos(long startPos) {
		this.startPos = startPos;
	}
	public long getEndPos() {
		return endPos;
	}
	public void setEndPos(long endPos) {
		this.endPos = endPos;
	}
	public int getStrand() {
		return strand;
	}
	public void setStrand(int strand) {
		this.strand = strand;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getChromosome() {
		return chromosome;
	}
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}
	public String getEnsemblID() {
		return ensemblID;
	}
	public void setEnsemblID(String ensemblID) {
		this.ensemblID = ensemblID;
	}
	public String getGeneID() {
		return geneID;
	}
	public void setGeneID(String geneID) {
		this.geneID = geneID;
	}
	
}
