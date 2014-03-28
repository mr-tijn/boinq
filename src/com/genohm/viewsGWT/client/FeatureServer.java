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
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("featureServer")
public interface FeatureServer extends RemoteService {
	List<Feature> getFeaturesByRegion(String source, ArgumentMap arguments) throws Exception;
	Feature getFeatureById(String source, ArgumentMap arguments) throws Exception;
	List<Chromosome> getChromosomes(Species species) throws Exception;
	ChromosomeDetail getChromosomeDetail(Species species, String chromosome) throws Exception;
	MappedReadFeature getReferenceSequence(int species, String chromosome, long start, long end) throws Exception;
	List<Feature> getFeaturesByRegion(FeatureDatasource ds, GenomicRegion region, String filterExpression, Integer minFeatureSize, Integer minDetailedSize) throws Exception;
	List<Feature> getFeaturesByRegion(GFFDatasource ds, GenomicRegion region, String filterExpression, Integer minFeatureSize, Integer minDetailedSize) throws Exception;
	List<Feature> getFeaturesByRegion(GeneDatasource ds, GenomicRegion region, String filterExpression, Integer minFeatureSize, Integer minDetailedSize) throws Exception;
	List<Feature> getFeaturesByRegion(RefSeqDatasource ds, GenomicRegion region, String filterExpression, Integer minFeatureSize, Integer minDetailedSize) throws Exception;
	List<Feature> getFeaturesByRegion(BBxAssemblyDatasource ds, GenomicRegion region, String filterExpression, Integer minFeatureSize, Integer minDetailedSize) throws Exception;
	Feature getFeatureById(FeatureDatasource ds, String featureId) throws Exception;
	Feature getFeatureById(GFFDatasource ds, String featureId) throws Exception;
	Feature getFeatureById(GeneDatasource ds, String featureId) throws Exception;
	Feature getFeatureById(RefSeqDatasource ds, String featureId) throws Exception;
	Feature getFeatureById(BBxAssemblyDatasource ds, String featureId) throws Exception;
}

