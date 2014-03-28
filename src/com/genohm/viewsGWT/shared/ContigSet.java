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
import java.util.List;
import java.util.Map;

public class ContigSet {
	protected Map<String,Chromosome> contigs;
	public ContigSet(List<Chromosome> contigs) {
		this.contigs = new LinkedHashMap<String, Chromosome>();
		for (Chromosome contig: contigs) {
			this.contigs.put(contig.name, contig);
		}
	}
	public Chromosome getContig(String chromosomeName) {
		return contigs.get(chromosomeName);
	}
	public LinkedHashMap<String,String> asMap() {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		for (String id: contigs.keySet()) {
			//result.put(id, contigs.get(id).name);
			result.put(contigs.get(id).name, contigs.get(id).name);
		}
		return result;
	}

}
