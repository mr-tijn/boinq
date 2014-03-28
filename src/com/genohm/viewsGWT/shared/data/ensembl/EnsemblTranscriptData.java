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

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class EnsemblTranscriptData implements IsSerializable{
	
	public EnsemblTranscriptData() {}
	
	private String description;
	
	private String ensemblID;
	private String name;

	private String refseq;
	private String biotype;
	private String status;
	private long startPos;
	private long endPos;
	private List<EnsemblExonData> exons;
	
	public String getEnsemblID() {
		return ensemblID;
	}
	public void setEnsemblID(String ensemblID) {
		this.ensemblID = ensemblID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRefseq() {
		return refseq;
	}
	public void setRefseq(String refseq) {
		this.refseq = refseq;
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
	public List<EnsemblExonData> getExons() {
		return exons;
	}
	public void setExons(List<EnsemblExonData> exons) {
		this.exons = exons;
	}
	public String getBiotype() {
		return biotype;
	}
	public void setBiotype(String biotype) {
		this.biotype = biotype;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}

