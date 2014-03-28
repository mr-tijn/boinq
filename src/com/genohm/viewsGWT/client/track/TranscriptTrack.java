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
package com.genohm.viewsGWT.client.track;

import com.genohm.viewsGWT.client.ViewPort;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.client.eventbus.Subscription;
import com.genohm.viewsGWT.client.eventbus.TopicSubscriber;
import com.genohm.viewsGWT.shared.data.TrackSpecification;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.genohm.viewsGWT.shared.data.feature.Gene;
import com.smartgwt.client.util.SC;

public class TranscriptTrack extends Track {

	public TranscriptTrack(ViewPort viewPort, TrackSpecification trackSpec) {
		super(viewPort, trackSpec);
	}
	
	@Override
	protected void subscribe() {
		final Track thisTrack = this;
 		EventBus.subscribe(EventbusTopic.VIEWPORT_CHANGED, new TopicSubscriber<ViewPort>() {
			@Override
			public void onEvent(Subscription subscription, ViewPort viewPort) {
				try {
					draw();
				} catch (Exception e) {
					e.printStackTrace();
				}					
			}
			@Override
			public int getId() {
				return 0;
			}
		});
		EventBus.subscribe(EventbusTopic.SELECTED_FEATURE_FETCHED, new TopicSubscriber<Feature>() {
			@Override
			public void onEvent(Subscription subscription, final Feature selectedFeature) {
				if (selectedFeature instanceof Gene) {
					processSelection(selectedFeature);
				}
			}
			@Override
			public int getId() {
				return 0;
			}
		});
		EventBus.subscribe(EventbusTopic.DRAG_MOVE, new TopicSubscriber<Integer>() {
			@Override
			public void onEvent(Subscription subscription, Integer offset) {
				dragMove(offset);
			}
			@Override
			public int getId() {
				return 0;
			}
		});
	}

	protected void processSelection(Feature selectedFeature) {
		if (selectedFeature instanceof Gene) {
			features.clear();
			features.addAll(((Gene) selectedFeature).getTranscripts());
			try {
				draw();
			} catch (Exception e) {
				SC.logDebug(e.getMessage());
			}
		}
	}
}
