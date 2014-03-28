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

import com.genohm.viewsGWT.shared.ArgumentMap;
import com.genohm.viewsGWT.shared.Chromosome;
import com.genohm.viewsGWT.shared.ChromosomeDetail;
import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.Species;
import com.genohm.viewsGWT.shared.data.BBxAssemblyDatasource;
import com.genohm.viewsGWT.shared.data.FeatureDatasource;
import com.genohm.viewsGWT.shared.data.GFFDatasource;
import com.genohm.viewsGWT.shared.data.GeneDatasource;
import com.genohm.viewsGWT.shared.data.RefSeqDatasource;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.genohm.viewsGWT.shared.data.feature.MappedReadFeature;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FeatureServerAsync {
	void getFeaturesByRegion(String source, ArgumentMap arguments, AsyncCallback<List<Feature>> callback) throws Exception;
	void getFeatureById(String source, ArgumentMap arguments, AsyncCallback<Feature> callback);
	void getChromosomes(Species species, AsyncCallback<List<Chromosome>> callback) throws Exception;
	void getChromosomeDetail(Species species, String chromosome, AsyncCallback<ChromosomeDetail> callback) throws Exception;
	void getReferenceSequence(int species, String chromosome, long start, long end, AsyncCallback<MappedReadFeature> callback) throws Exception;
	void getFeaturesByRegion(FeatureDatasource ds, GenomicRegion region, String filterExpression, Integer minFeatureSize, Integer minDetailedSize, AsyncCallback<List<Feature>> callback) throws Exception;
	void getFeaturesByRegion(GFFDatasource ds, GenomicRegion region, String filterExpression, Integer minFeatureSize, Integer minDetailedSize, AsyncCallback<List<Feature>> callback) throws Exception;
	void getFeaturesByRegion(GeneDatasource ds, GenomicRegion region, String filterExpression, Integer minFeatureSize, Integer minDetailedSize, AsyncCallback<List<Feature>> callback) throws Exception;
	void getFeaturesByRegion(RefSeqDatasource ds, GenomicRegion region,	String filterExpression, Integer minFeatureSize, Integer minDetailedSize, AsyncCallback<List<Feature>> callback) throws Exception;
	void getFeaturesByRegion(BBxAssemblyDatasource ds, GenomicRegion region, String filterExpression, Integer minFeatureSize, Integer minDetailedSize, AsyncCallback<List<Feature>> callback);
	void getFeatureById(FeatureDatasource ds, String featureId, AsyncCallback<Feature> callback);
	void getFeatureById(GFFDatasource ds, String featureId,	AsyncCallback<Feature> callback);
	void getFeatureById(GeneDatasource ds, String featureId, AsyncCallback<Feature> callback);
	void getFeatureById(RefSeqDatasource ds, String featureId, AsyncCallback<Feature> callback);
	void getFeatureById(BBxAssemblyDatasource ds, String featureId, AsyncCallback<Feature> callback);
}
