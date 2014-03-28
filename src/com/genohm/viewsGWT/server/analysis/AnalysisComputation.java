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

import org.quartz.JobExecutionContext;

import com.genohm.viewsGWT.client.FeatureServer;
import com.genohm.viewsGWT.server.data.ViewsDao;
import com.genohm.viewsGWT.shared.analysis.Analysis;
import com.genohm.viewsGWT.shared.analysis.AnalysisProcessor;

public class AnalysisComputation implements Computation {

	private Analysis analysis;
	
	public AnalysisComputation(Analysis analysis) {
		this.analysis = analysis;
	}
	
	@Override
	public Status getStatus() {
		switch (analysis.getStatus()) {
		case Analysis.STATUS_PENDING: return Status.PENDING;
		case Analysis.STATUS_COMPUTING: return Status.COMPUTING;
		case Analysis.STATUS_SUCCESS: return Status.SUCCESS;
		case Analysis.STATUS_ERROR: return Status.ERROR;
		default: return Status.UNKNOWN;
		}
	}

	@Override
	public String getDescription() {
		return analysis.getDescription();
	}

	@Override
	public String getStatusText() {
		//FIXME: status of analysis and computation are not necessarily in sync
		return statusText[analysis.getStatus()];
	}

	@Override
	public void execute(JobExecutionContext context) throws Exception {
		ViewsDao viewsDao = (ViewsDao) context.getScheduler().getContext().get("viewsDao");
		FeatureServer featureServer = (FeatureServer) context.getScheduler().getContext().get("featureServer");
		AnalysisProcessor processor = new DefaultAnalysisProcessor(viewsDao, featureServer);
		analysis.acceptProcessor(processor);
	}

}
