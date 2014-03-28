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

import java.beans.Transient;

import org.quartz.JobExecutionContext;

import com.genohm.viewsGWT.client.analysis.GraphicalAnalysisVisualizer;

public class TestComputation implements Computation {

	protected Status status = Status.PENDING;
	protected String message = null;
	
	public TestComputation(String message) {
		this.message = message;
	}
	
	@Override
	//public void execute(JobExecutionContext context) throws Exception {
	public void execute(JobExecutionContext context) throws Exception {
		System.out.println(message);
		status = status.SUCCESS;
	}

	@Override
	public String getDescription() {
		return "A simple test job printing a message to screen";
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
