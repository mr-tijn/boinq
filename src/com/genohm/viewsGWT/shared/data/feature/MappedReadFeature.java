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
package com.genohm.viewsGWT.shared.data.feature;

import java.util.List;

import javax.persistence.Transient;

import com.genohm.viewsGWT.shared.BioTools;
import com.genohm.viewsGWT.shared.Location;

public class MappedReadFeature extends Feature {
	
	protected String dnaString;
	protected String dnaStringRC = null;

	protected MappedReadFeature() {super();}
	
	public MappedReadFeature(String id, String description,
			 Double score, String name, Feature parent, String dnaString, List<Location> loc, List<Feature> subFeatures) {
		super(id, description, score, name, parent, loc, subFeatures);
		this.dnaString = dnaString;
	}

	@Transient
	public String getDnaStringReverseComplement() {
		if (dnaStringRC == null) dnaStringRC = BioTools.reverseComplement(dnaString);
		return dnaStringRC;
	}

	@Transient
	public String getDnaStringComplement() {
		return BioTools.complement(dnaString);
	}
	
	public String getDnaString() {
		return dnaString;
	}
	public void setDnaString(String dnaString) {
		this.dnaString = dnaString;
	}

}
