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
package com.genohm.viewsGWT.client.util;

public class GroupData {
	float var;
	int start=1;
	int width;
	int previousWidth; 
 
	public GroupData(float var, int start, int width,int previousWidth) {
		super();
		this.var = var;
		this.start = start;
		this.width = width;
		this.previousWidth = previousWidth;
 	}

	public GroupData() {
		super();
	}

	public float getVar() {
		return var;
	}
	public void setVar(float var) {
		this.var = var;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getPreviousWidth() {
		return previousWidth;
	}

	public void setPreviousWidth(int previousWidth) {
		this.previousWidth = previousWidth;
	}

}
