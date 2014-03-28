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
package com.genohm.viewsGWT.shared.data.feature;

import java.util.LinkedList;
import java.util.List;

import com.genohm.viewsGWT.shared.Location;

public class BigFeature extends Feature{
	protected List<Integer> position = new LinkedList<Integer>();
	protected List<Integer> startPosition = new LinkedList<Integer>();
	protected List<Float> data = new LinkedList<Float>();
	protected Float maxData;
	protected int span;



	public BigFeature() {
		super();
 	}
	public BigFeature(String id, String description, Double score, String name,
			Feature parent, List<Location> locList,
			List<? extends Feature> subFeatures, List<Integer> position, List<Integer> start, List<Float> data,Float maxData, int span) {
		super(id, description, score, name, parent, locList, subFeatures);

		if (data != null) {
			for (Float newdata: data) this.data.add(newdata);
		}
		if (position != null) {
			for (Integer newpos: position) this.position.add(newpos);
		}
		if (start!= null) {
			for (Integer newstart: start) this.startPosition.add(newstart);
		}
		this.maxData = maxData;
		this.span = span;
	}
	
	
	public Float getMaxData() {
		return maxData;
	}
	public void setMaxData(Float maxData) {
		this.maxData = maxData;
	}
	public List<Integer> getStartPosition() {
		return startPosition;
	}
	public void setStartPosition(List<Integer> start) {
		this.startPosition = start;
	}
	public List<Integer> getPosition() {
		return position;
	}

	public List<Float> getData() {
		return data;
	}
	public void setData(List<Float> data) {
		this.data = data;
	}
	public int getSpan() {
		return span;
	}
	public void setSpan(int span) {
		this.span = span;
	}

	public Float getMaximalData (List<Float> listData){
		float maxData=listData.get(0);
		for (int i=0; i<listData.size(); i++){
			if (listData.get(i)>maxData){
				maxData=listData.get(i);
			}	
		}
		return maxData;
	}


}
