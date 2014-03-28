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
package com.genohm.viewsGWT.server.twinql;

import java.util.List;

// indexes position of lines in file based on start/end/chr
// for fast retrieval from tsv files

public interface FeatureIndex {
	List<Long> getPositionsBetween(Long start, Long end, String chr);
	void storePosition(Long position, Long start, Long end, String chr) throws Exception;
	void init() throws Exception;
	Boolean exists();
}
