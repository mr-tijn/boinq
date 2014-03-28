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
package com.genohm.viewsGWT.client.eventbus;

import com.genohm.viewsGWT.client.renderer.DrawFeature;
import com.genohm.viewsGWT.client.track.Track;

public class FeatureSelection {
	// includes source Track with event object
	
	protected Track sourceTrack;
	protected DrawFeature feature;
	
	public FeatureSelection(Track sourceTrack, DrawFeature feature) {
		super();
		this.sourceTrack = sourceTrack;
		this.feature = feature;
	}
	public Track getSourceTrack() {
		return sourceTrack;
	}
	public void setSourceTrack(Track sourceTrack) {
		this.sourceTrack = sourceTrack;
	}
	public DrawFeature getDrawFeature() {
		return feature;
	}
	public void setDrawFeature(DrawFeature feature) {
		this.feature = feature;
	}
	
	
}
