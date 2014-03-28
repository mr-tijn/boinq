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
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import com.genohm.viewsGWT.client.FeatureServer;
import com.genohm.viewsGWT.server.data.ViewsDao;
import com.genohm.viewsGWT.server.external.SPARQLClient;


import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;


public class ComputationEngine {

	private static final Logger log = Logger.getLogger(ComputationEngine.class);
	//spring managed - to be put in the scheduler context
	protected Scheduler mainScheduler = null;
	protected ViewsDao viewsDao;
	protected FeatureServer featureServer;
	protected SPARQLClient sparqlClient;
	
	ComputationEngine() {		
	}
	
	public void stop() throws SchedulerException {
		mainScheduler.shutdown();
	}

	public void add(Computation comp) throws SchedulerException {
		JobDataMap map = new JobDataMap();
		map.put("computation", comp);
		JobDetail runJob = newJob(ComputationRunner.class).withDescription(comp.getDescription()).usingJobData(map).build();
		Trigger runTrigger = newTrigger().startNow().build();
		mainScheduler.scheduleJob(runJob,runTrigger);
	}
	
	public static void main(String[] args) {
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("WEB-INF/spring-service.xml");
		ComputationEngine ce = (ComputationEngine) context.getBean("quartzEngine");

		try {
			ce.add(new TestComputation("Hello world"));
		} catch (SchedulerException e) {
			e.printStackTrace();
		} finally {
			try {
				ce.stop();
			} catch (SchedulerException e) {
				log.error(e);
			}
		}
	}

	public Scheduler getMainScheduler() {
		return mainScheduler;
	}

	public void setMainScheduler(Scheduler mainScheduler) {
		this.mainScheduler = mainScheduler;
	}

	public ViewsDao getViewsDao() {
		return viewsDao;
	}

	public void setViewsDao(ViewsDao viewsDao) {
		this.viewsDao = viewsDao;
		try {
			mainScheduler.getContext().put("viewsDao", viewsDao);
		} catch (Exception e) {
			log.error("Could not put viewsDao in scheduler context", e);
		}
	}

	public void setFeatureServer(FeatureServer featureServer) {
		this.featureServer = featureServer;
		try {
			mainScheduler.getContext().put("featureServer", featureServer);
		} catch (Exception e) {
			log.error("Could not put featureServer in scheduler context", e);
		}
	}
	
	public SPARQLClient getSparqlClient() {
		return sparqlClient;
	}

	public void setSparqlClient(SPARQLClient sparqlClient) {
		this.sparqlClient = sparqlClient;
		try {
			mainScheduler.getContext().put("sparqlClient", sparqlClient);
		} catch (Exception e) {
			log.error("Could not put sparqlClient in scheduler context", e);
		}	
	}
}


