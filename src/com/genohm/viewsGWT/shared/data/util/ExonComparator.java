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
package com.genohm.viewsGWT.shared.data.util;

import java.util.Comparator;

import com.genohm.viewsGWT.shared.data.feature.Exon;

public class ExonComparator implements Comparator<Exon> {

	public ExonComparator(boolean strand) {
		super();
		this.strand = strand;
	}

	private boolean strand;
	
	@Override
	public int compare(Exon o1, Exon o2) {
		if (strand) {
			if (o1.getLoc().get(0).getStart() != o2.getLoc().get(0).getStart()) return ((int) (o1.getLoc().get(0).getStart() - o2.getLoc().get(0).getStart()));
			else return ((int) (o1.getLoc().get(0).getEnd() - o2.getLoc().get(0).getEnd()));
		}
		else {
			if (o1.getLoc().get(0).getStart() != o2.getLoc().get(0).getStart()) return ((int) (o2.getLoc().get(0).getStart() - o1.getLoc().get(0).getStart()));
			else return ((int) (o2.getLoc().get(0).getEnd() - o1.getLoc().get(0).getEnd()));
		}
	}

}
