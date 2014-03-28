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
package com.genohm.viewsGWT.client.util;

import java.util.Comparator;

import com.genohm.viewsGWT.shared.Location;
 

public class LocationComparator implements Comparator<Location>{

	public LocationComparator() {
		super();
	}

	@Override
	public int compare(Location firstLocation, Location secondLocation) {
		int result = firstLocation.getStart().compareTo(secondLocation.getStart());
 		return result;
	}
	 
}
