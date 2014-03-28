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
package com.genohm.viewsGWT.client;

import java.util.List;

import com.genohm.viewsGWT.client.eventbus.Event;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.shared.ArgumentMap;
import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.data.FeatureDatasource;
import com.genohm.viewsGWT.shared.data.GFFDatasource;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class CachedFeatureServerAsync {
	protected static FeatureServerAsync featureServer = GWT.create(FeatureServer.class);
	protected static FeatureCache featureCache = new FeatureCache();

	public static void getFeatureById(String source, ArgumentMap arguments, AsyncCallback<Feature> callback) {
		featureServer.getFeatureById(source, arguments, callback);
	}
	
	public static void getFeaturesByRegio(FeatureDatasource featureDS, GenomicRegion region, String filterExpression, Integer minFeatureSize, Integer minDetailedSize, AsyncCallback<List<Feature>> callback) throws Exception {
		//featureServer.getFeaturesByRegion(featureDS, region, filterExpression, minFeatureSize, minDetailedSize, callback);
		getFeatures(featureDS, region, filterExpression, minFeatureSize, minDetailedSize, callback);
	}

	public static void getFeaturesByRegion(GFFDatasource featureDS, GenomicRegion region, String filterExpression, Integer minFeatureSize, Integer minDetailedSize, AsyncCallback<List<Feature>> callback) throws Exception {
		//featureServer.getFeaturesByRegion(featureDS, region, filterExpression, minFeatureSize, minDetailedSize, callback);
		
	}
	
 	public static void getFeatures(final FeatureDatasource ds, final GenomicRegion region, final String filterExpression, final Integer minFeatureSize, final Integer minDetailedSize, final AsyncCallback<List<Feature>> callback) throws Exception {
		EventBus.publish(new Event(EventbusTopic.START_WAIT), null);
		if (featureCache.contains(ds, minDetailedSize, minFeatureSize, filterExpression, region)) {
			EventBus.publish(EventbusTopic.END_WAIT, null);
			callback.onSuccess(featureCache.getFeatures(ds, minDetailedSize, minFeatureSize, region));
		} else {
			try {
				// fetch slightly deeper zoomlevel
				// args.setMinFeatureWidth(args.getMinFeatureWidth()/2);
				featureServer.getFeaturesByRegion(ds, region, filterExpression, minFeatureSize, minDetailedSize, new AsyncCallback<List<Feature>>() {
					@Override
					public void onSuccess(List<Feature> result) {
						EventBus.publish(EventbusTopic.END_WAIT, null);
						featureCache.addFeatures(ds, minDetailedSize, minFeatureSize, filterExpression, region, result);
						callback.onSuccess(result);
					}
					@Override
					public void onFailure(Throwable caught) {
						EventBus.publish(EventbusTopic.END_WAIT, null);
						callback.onFailure(caught);
					}
				});
			} catch (Exception e) {
				EventBus.publish(EventbusTopic.END_WAIT, null);
				EventBus.publish(EventbusTopic.ERROR, e.getMessage());
			}
		}
	}
 	

}
