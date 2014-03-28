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
package com.genohm.viewsGWT.tools.initializer;

import org.quartz.SchedulerException;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.genohm.viewsGWT.server.analysis.ComputationEngine;
import com.genohm.viewsGWT.server.data.EnsemblGeneDAO;

public class SearchInitializer {
	public static EnsemblGeneDAO dao;
	
	public static void main(String[] args) {
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("WEB-INF/spring-service.xml");
		dao = (EnsemblGeneDAO) context.getBean("ensemblGeneDAO");
		if (args.length < 1) System.err.println("Expecting chromosome as argument");
		else dao.initializeIndexes(args[0]);
		//shutdown quartz manually
		ComputationEngine ce = (ComputationEngine) context.getBean("quartzEngine");
		try {
			ce.stop();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

}
