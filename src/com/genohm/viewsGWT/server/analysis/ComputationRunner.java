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

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class ComputationRunner implements Job {

	// Mechanism to support instance based job runs
	private static final Logger log = Logger.getLogger(ComputationRunner.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Computation theComp = (Computation) context.getJobDetail().getJobDataMap().get("computation");
		try {
			theComp.execute(context);
		} catch (Exception e) {
			log.error("Problem while launching computation", e);
			throw new JobExecutionException("Problem while launching computation", e);
		}
	}

}
