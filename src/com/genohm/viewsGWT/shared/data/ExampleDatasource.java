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
package com.genohm.viewsGWT.shared.data;

import java.util.List;

import javax.persistence.Transient;

import com.genohm.viewsGWT.client.FeatureServer;
import com.genohm.viewsGWT.client.FeatureServerAsync;
import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ExampleDatasource extends FeatureDatasource {

	@Transient
	@Override
	public Feature getFeatureById(FeatureServer featureServer, String featureId) throws Exception {
		return null;
	}

	@Transient
	@Override
	public void getFeatureById(FeatureServerAsync featureServer,
			String featureId, AsyncCallback<Feature> callback) throws Exception {
		
	}

	@Transient
	@Override
	public List<Feature> getFeaturesByRegion(FeatureServer featureServer,
			GenomicRegion region, String filterExpression,
			Integer minFeatureSize, Integer minDetailedSize) throws Exception {
		return null;
	}

	@Transient
	@Override
	public void getFeaturesByRegion(FeatureServerAsync featureServer,
			GenomicRegion region, String filterExpression,
			Integer minFeatureSize, Integer minDetailedSize,
			AsyncCallback<List<Feature>> callback) throws Exception {
		
	}

}
