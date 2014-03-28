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
package com.genohm.viewsGWT.shared.analysis;



public interface Analysis  {
	public static int STATUS_PENDING = 0;
	public static int STATUS_COMPUTING = 1;
	public static int STATUS_SUCCESS = 2;
	public static int STATUS_ERROR = 3;
//	public static String[] statusText = {
//		"Pending", "Computing", "Success", "Error"
//	};
	public Long getId();
	public Integer getStatus();
	public String getDescription();
//	public String getStatusText();
	public String getOwner();
	public void setOwner(String currentUserName);
	public String getName();
	public void setName(String string);
	public Boolean getIsPublic();
	public String getResultSummary();
	public void acceptVisualizer(AnalysisVisualizer visualizer);
	public void acceptProcessor(AnalysisProcessor processor);
	public Analysis deepcopy();
}
