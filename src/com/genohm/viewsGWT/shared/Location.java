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
package com.genohm.viewsGWT.shared;
import com.google.gwt.user.client.rpc.IsSerializable;


public class Location implements IsSerializable {
	
	protected Long  start;
	protected Long end;
	protected String chr;
	protected Boolean strand;

	public Location() {}
	
	public Location(Long start, Long end, String chr, Boolean strand) {
		super();
		this.start = start;
		this.end = end;
		this.chr = chr;
		this.strand = strand;
	}

	public Boolean equals(Location otherLoc) {
		return (start == otherLoc.start && end == otherLoc.end && chr == otherLoc.chr && strand == otherLoc.strand);
	}

	public Long getStart() {
		return start;
	}


	public void setStart(Long start) {
		this.start = start;
	}


	public Long getEnd() {
		return end;
	}


	public void setEnd(Long end) {
		this.end = end;
	}


	public String getChr() {
		return chr;
	}


	public void setChr(String chr) {
		this.chr = chr;
	}


	public Boolean getStrand() {
		return strand;
	}


	public void setStrand(Boolean strand) {
		this.strand = strand;
	}


	public Long getLength() {
		return getEnd() - getStart();
	}
	
	@Override
	public String toString() {
		return getChr() + ":" + getStart() + "-" + getEnd() + "(" + getStrand() +")";
	}
	
	public Location getDuplicate() {
		return new Location(start, end, chr, strand);
	}

}
