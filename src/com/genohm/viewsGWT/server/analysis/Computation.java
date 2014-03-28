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
import com.google.gwt.user.client.rpc.IsSerializable;

public interface Computation extends IsSerializable {
	public static enum Status {
		PENDING(0), COMPUTING(1), SUCCESS(2), ERROR(3), UNKNOWN(4);
		public int value;
		private Status(int value) {
			this.value = value;
		}
	};
	public static String[] statusText = {
		"Pending", "Computing", "Success", "Error", "Unknown"
	};
	Status getStatus();
	String getDescription();
	String getStatusText();
	void execute(JobExecutionContext context) throws Exception;
}
