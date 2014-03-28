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
package com.genohm.viewsGWT.shared.fieldconfig;

import java.util.LinkedList;
import java.util.List;

public enum FieldType {
	UNKNOWN_TYPE(0),
	INTEGER_TYPE(1),
	DECIMAL_TYPE(2),
	BOOLEAN_TYPE(3),
	STRING_TYPE(4),
	TERM_TYPE(5),
	LOCATION_TYPE(6),
	INTERNAL_TERM_TYPE(7);
	
	public int value;
	FieldType(int value) {
		this.value = value;
	}
	public static FieldType parseInt(int i) {
		for (FieldType ft: values()) {
			if (ft.value == i) return ft;
		}
		return null;
	}
}
