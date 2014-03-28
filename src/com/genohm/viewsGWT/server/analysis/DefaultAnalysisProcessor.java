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
package com.genohm.viewsGWT.server.analysis;

import static org.hibernate.criterion.Restrictions.eq;

import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.openjena.atlas.logging.Log;
import org.springframework.transaction.annotation.Transactional;

import com.genohm.viewsGWT.client.FeatureServer;
import com.genohm.viewsGWT.client.ViewsGWT;
import com.genohm.viewsGWT.server.data.ViewsDao;
import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.RegionOfInterest;
import com.genohm.viewsGWT.shared.analysis.Analysis;
import com.genohm.viewsGWT.shared.analysis.AnalysisProcessor;
import com.genohm.viewsGWT.shared.analysis.CountReadsPerRegion;
import com.genohm.viewsGWT.shared.analysis.RegionCount;
import com.genohm.viewsGWT.shared.data.TrackSpecification;
import com.genohm.viewsGWT.shared.data.feature.Feature;

public class DefaultAnalysisProcessor implements AnalysisProcessor {

	private ViewsDao viewsDao;
	private FeatureServer featureServer;
	private static Logger log = Logger.getLogger(DefaultAnalysisProcessor.class);
	
	public DefaultAnalysisProcessor(ViewsDao viewsDao, FeatureServer featureServer) {
		this.viewsDao = viewsDao;
		this.featureServer = featureServer;
	}
	
	@Override
	public void visitAnalysis(Analysis analysis) {
		
	}
	
	@Transactional("views")
	public void visitAnalysis(CountReadsPerRegion analysis) {
		analysis.setStatus(Analysis.STATUS_COMPUTING);
		analysis = viewsDao.merge(analysis);
		try {
		RegionOfInterest roi = (RegionOfInterest) viewsDao.getById(RegionOfInterest.class, analysis.getRoiId());
		TrackSpecification track = (TrackSpecification) viewsDao.getById(TrackSpecification.class, analysis.getTrackId());
		if (track != null && roi != null) {
			for (GenomicRegion region: roi.getRegions()) {
				try {
					//TODO: just count directly
					List<Feature> features = track.getFeatureDatasource().getFeaturesByRegion(featureServer, region, track.getFilterExpression(), 0, 100000);
					//TODO: is the region the one from the db ?
					RegionCount regionCount = new RegionCount();
					regionCount.setAnalysis(analysis);
					regionCount.setCount((long) features.size());
//					regionCount.setGenomicRegion(region);
					viewsDao.save(regionCount);
					if (analysis.getRegionCounts() == null) {
						analysis.setRegionCounts(new HashSet<RegionCount>());
					}
					analysis.getRegionCounts().add(regionCount);
					viewsDao.update(analysis);
				} catch (Throwable t) {
					log.error("Problem getting features while computing reads per region",t);
					throw(new Exception("Problem getting features while computing reads per region"));
				}
			}
			analysis.setStatus(Analysis.STATUS_SUCCESS);
			viewsDao.update(analysis);
		}
		} catch (Exception e) {
			log.error(e);
			analysis.setStatus(Analysis.STATUS_ERROR);
			analysis.setDescription(e.getMessage());
			try {
				analysis = viewsDao.merge(analysis);
			} catch (Exception f) {
				log.error("error while saving analysis " + analysis.getId(),f);
			}
		}
	}

}
