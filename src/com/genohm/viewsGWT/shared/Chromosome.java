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

public class Chromosome implements IsSerializable {
	protected String id;
	protected String name;
	protected Long startCoordinate;
	protected Long endCoordinate;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getStartCoordinate() {
		return startCoordinate;
	}
	public void setStartCoordinate(Long startCoordinate) {
		this.startCoordinate = startCoordinate;
	}
	public Long getEndCoordinate() {
		return endCoordinate;
	}
	public void setEndCoordinate(Long endCoordinate) {
		this.endCoordinate = endCoordinate;
	}
}
