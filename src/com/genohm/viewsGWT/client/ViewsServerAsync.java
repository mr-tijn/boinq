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

import com.genohm.viewsGWT.shared.BrowserPerspective;
import com.genohm.viewsGWT.shared.RegionOfInterest;
import com.genohm.viewsGWT.shared.analysis.Analysis;
import com.genohm.viewsGWT.shared.data.FeatureDatasource;
import com.genohm.viewsGWT.shared.data.SPARQLResultSet;
import com.genohm.viewsGWT.shared.data.Term;
import com.genohm.viewsGWT.shared.data.TrackSpecification;
import com.genohm.viewsGWT.shared.data.ensembl.EnsemblGene;
import com.genohm.viewsGWT.shared.fieldconfig.FieldConfig;
import com.genohm.viewsGWT.shared.fieldconfig.Target;
import com.genohm.viewsGWT.shared.query.Match;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ViewsServerAsync {

	void getCurrentUserName(AsyncCallback<String> callback);

	void getFeatureDatasources(AsyncCallback<List<FeatureDatasource>> callback);

	void logout(AsyncCallback<Void> callback);

	void searchGenesFullText(String search, AsyncCallback<List<EnsemblGene>> callback);

	void getROIs(AsyncCallback<List<RegionOfInterest>> callback);

	void getTrackSpecs(AsyncCallback<List<TrackSpecification>> callback);

	void getDefaultPerspective(AsyncCallback<BrowserPerspective> callback);

	void saveDefaultPerspective(BrowserPerspective perspective, AsyncCallback<Void> callback);

	void sparqlQuery(String query,
			AsyncCallback<SPARQLResultSet> callback);

	void getFields(String featureTypeURI,
			AsyncCallback<List<FieldConfig>> callback);

	void getDatasources(AsyncCallback<List<Term>> callback);

	void getFeatureTypes(String datasourceURI,
			AsyncCallback<List<Term>> callback);

	void generateQuery(Match match, AsyncCallback<String> callback);

	void saveROI(RegionOfInterest roi, AsyncCallback<Void> callback);

	void getProperties(String entityIRI, AsyncCallback<List<Term>> callback);

	void getTargetTypes(String entityIRI, String propertyIRI,
			AsyncCallback<List<Target>> callback);

	void getAnalyses(AsyncCallback<List<Analysis>> callback);

	void removeRegionFromRoi(Long region_id, Long roi_id,
			AsyncCallback<Void> callback);

	void removeRoi(Long roi_id, AsyncCallback<Void> callback);

	void saveAnalysis(Analysis analysis, AsyncCallback<Void> callback);


}
