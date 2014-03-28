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
import java.util.Map;

import com.genohm.viewsGWT.server.analysis.Computation;
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
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("viewsServer")
public interface ViewsServer extends RemoteService {
	String getCurrentUserName() throws Exception;
	List<FeatureDatasource> getFeatureDatasources() throws Exception;
	List<EnsemblGene> searchGenesFullText(String search) throws Exception;
	List<RegionOfInterest> getROIs() throws Exception;
	List<TrackSpecification> getTrackSpecs() throws Exception;
	List<Analysis> getAnalyses() throws Exception;
	BrowserPerspective getDefaultPerspective() throws Exception;
	SPARQLResultSet sparqlQuery(String query) throws Exception;
	List<FieldConfig> getFields(String featureTypeURI) throws Exception;
	List<Term> getDatasources() throws Exception;
	List<Term> getFeatureTypes(String datasourceURI) throws Exception;
	List<Term> getProperties(String entityIRI) throws Exception;
	List<Target> getTargetTypes(String entityIRI, String propertyIRI) throws Exception;
	String generateQuery(Match match) throws Exception;
	void saveDefaultPerspective(BrowserPerspective perspective) throws Exception;
	void saveROI(RegionOfInterest roi) throws Exception;
	void saveAnalysis(Analysis analysis) throws Exception;
	void removeRegionFromRoi(Long region_id, Long roi_id);
	void removeRoi(Long roi_id);
	void logout() throws Exception;
}
