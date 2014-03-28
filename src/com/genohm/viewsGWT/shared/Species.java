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

import java.util.LinkedHashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum Species implements IsSerializable{
	HUMAN(9606,"Homo sapiens"),
	MOUSE(10090,"Mus musculus"),
	FRUITFLY(7227,"Drosophila melanogaster");
	
	protected Integer value = 0;
	protected String name = null;
	private Species() {}
	private Species(Integer value, String name) {
		this.value = value;
		this.name = name;
	}
	public Integer getValue() {
		return value;
	}
	public String getName() {
		return name;
	}
	public static Species getByID(Integer value) {
		for (Species species: Species.values()) {
			if (species.value.equals(value)) return species;
		}
		return null;
	}
	public static LinkedHashMap<String, String> asMap() {
		LinkedHashMap<String, String> resultMap = new LinkedHashMap<String, String>();
		//FIXME
//		for (Species species: Species.values()) {
//			resultMap.put(species.getValue().toString(), species.getName());
//		}
		resultMap.put(HUMAN.getValue().toString(), HUMAN.getName());
		return resultMap;
	}
}
