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

public class PairedEndFeature extends Feature {
	/* 
	 * first location is by convention always the forward sequence
	 */

	String sequenceForward;
	String sequenceBackward;
	int[] qualityForward;
	int[] qualityBackward;
	
	
	public String getSequenceForward() {
		return sequenceForward;
	}
	public void setSequenceForward(String sequenceForward) {
		this.sequenceForward = sequenceForward;
	}
	public String getSequenceBackward() {
		return sequenceBackward;
	}
	public void setSequenceBackward(String sequenceBackward) {
		this.sequenceBackward = sequenceBackward;
	}
	public int[] getQualityForward() {
		return qualityForward;
	}
	public void setQualityForward(int[] qualityForward) {
		this.qualityForward = qualityForward;
	}
	public int[] getQualityBackward() {
		return qualityBackward;
	}
	public void setQualityBackward(int[] qualityBackward) {
		this.qualityBackward = qualityBackward;
	}


}
