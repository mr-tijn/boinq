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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import javax.persistence.Transient;

import org.hibernate.Session;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

import com.genohm.viewsGWT.client.analysis.GraphicalAnalysisVisualizer;
import com.genohm.viewsGWT.server.data.ViewsDao;
import com.genohm.viewsGWT.server.external.SPARQLClient;
import com.genohm.viewsGWT.server.ontologyclient.FaldoVocabulary;
import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.RegionOfInterest;
import com.genohm.viewsGWT.shared.Species;
import com.genohm.viewsGWT.shared.data.SPARQLResultSet;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;

public class ROIComputation implements Computation {

	protected RegionOfInterest roi = null;
	protected Status status = Status.PENDING;
	
	public ROIComputation(RegionOfInterest roi) {
		setRoi(roi);
	}

	public RegionOfInterest getRoi() {
		return roi;
	}

	public void setRoi(RegionOfInterest roi) {
		this.roi = roi;
	}
	
	@Override
	public String getDescription() {
		return "Computation for ROI [" + roi.getName() +"]";
	}

	@Override
	synchronized
	public void execute(JobExecutionContext context) {
		status = Status.COMPUTING;
		ViewsDao viewsDao = null;
		try {
			viewsDao = (ViewsDao) context.getScheduler().getContext().get("viewsDao");
			//Session currentSession = viewsDao.getSessionFactory().getCurrentSession();
			roi.setStatus(RegionOfInterest.STATUS_COMPUTING);
			roi = (RegionOfInterest) viewsDao.merge(roi);
			SPARQLClient sparqlClient = (SPARQLClient) context.getScheduler().getContext().get("sparqlClient");
			SPARQLResultSet result = sparqlClient.query(roi.getExpression());
			for (Map<String,String> record: result.getRecords()) {
				Long featureStart = Long.parseLong(record.get("featureBeginPos"));
				Long featureEnd = Long.parseLong(record.get("featureEndPos"));
				Boolean featureStrand = Boolean.parseBoolean(record.get("featureStrand"));
				String featureReference = record.get("featureReferenceName");
				String featureId = record.get("featureId");
				Long regionStart;
				Long regionEnd;
				if (featureStrand) {
					if (roi.getRegionStartRefersToFeatureStart()) {
						regionStart = featureStart + roi.getRegionStartOffset();
					} else {
						regionStart = featureEnd + roi.getRegionStartOffset();
					}
					if (roi.getRegionEndRefersToFeatureStart()) {
						regionEnd = featureStart + roi.getRegionEndOffset();
					} else {
						regionEnd = featureEnd + roi.getRegionEndOffset();
					}
				} else {
					if (roi.getRegionStartRefersToFeatureStart()) {
						regionStart = featureStart - roi.getRegionStartOffset();
					} else {
						regionStart = featureEnd - roi.getRegionStartOffset();
					}
					if (roi.getRegionEndRefersToFeatureStart()) {
						regionEnd = featureStart - roi.getRegionEndOffset();
					} else {
						regionEnd = featureEnd - roi.getRegionEndOffset();
					}
				}
				GenomicRegion newRegion = new GenomicRegion(regionStart,regionEnd,featureReference,Species.HUMAN,featureStrand);
				newRegion.setName(featureId);
				viewsDao.save(newRegion);
				if (roi.getRegions() == null) {
//					roi.setRegions(new HashSet<GenomicRegion>());
					roi.setRegions(new LinkedList<GenomicRegion>());
				}
				roi.getRegions().add(newRegion);
				viewsDao.update(roi);
			}
			roi.setStatus(RegionOfInterest.STATUS_READY);
			roi = viewsDao.merge(roi);
			status = Status.SUCCESS;
		} catch (Exception e) {
			status = Status.ERROR;
			roi.setStatus(RegionOfInterest.STATUS_ERROR);
			try {
				if (viewsDao != null) viewsDao.merge(roi);
			} catch (Exception ee) {
				//TODO: write some error to the computation object
			}
		}
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public String getStatusText() {
		return statusText[status.value];
	}

}
