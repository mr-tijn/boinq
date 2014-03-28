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

public class EnsemblExonData implements IsSerializable {
	
	public EnsemblExonData() {}
	
	private String ensembl_id;
	private long startPos;
	private long endPos;
	private long tstartPos;
	private long tendPos;
	private int rank;
	
	public String getEnsembl_id() {
		return ensembl_id;
	}
	public void setEnsembl_id(String ensemblId) {
		ensembl_id = ensemblId;
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
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public long getTstartPos() {
		return tstartPos;
	}
	public void setTstartPos(long tstartPos) {
		this.tstartPos = tstartPos;
	}
	public long getTendPos() {
		return tendPos;
	}
	public void setTendPos(long tendPos) {
		this.tendPos = tendPos;
	}
	
}
