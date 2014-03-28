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

import java.util.List;

import com.genohm.viewsGWT.shared.Location;


public class GraphFeature extends Feature{

	protected float[] data ;
	protected int [] position ; 
	protected Float minData; 
	protected Float maxData;
	protected int span;



	public GraphFeature() {
		super();

	}

	public GraphFeature(String id, String description, Double score,
			String name, Feature parent, List<Location> locList,
			List<? extends Feature> subFeatures, float[] data, int [] position, Float minData, Float maxData, int span) {
		super(id, description, score, name, parent, locList, subFeatures);
		this.data = data;
		this.position = position;
		this.minData = minData;
		this.maxData = maxData;
		this.span = span;
	}

	public Float getMinData() {
		return minData;
	}

	public void setMinData(Float minData) {
		this.minData = minData;
	}

	public Float getMaxData() {
		return maxData;
	}

	public void setMaxData(Float maxData) {
		this.maxData = maxData;
	}

	public float[] getData() {
		return data;
	}

	public void setData (float[] data){
		this.data=data;
	}



	public int getSpan() {
		return span;
	}

	public void setSpan(int span) {
		this.span = span;
	}

	public int[] getPosition() {
		return position;
	}

	public void setPosition(int[] position) {
		this.position = position;
	}

	public Float getMinimalData (Float[] listData){
		float minData=listData[0];
		for (int i=1; i<listData.length; i++){
			if (listData[i]<minData){
				minData=listData[i];
			}	
		}
		return minData;
	}
	public Float getMaximalData (Float[] listData){
		float maxData=listData[0];
		for (int i=1; i<listData.length; i++){
			if (listData[i]>maxData){
				maxData=listData[i];
			}	
		}
		return maxData;
	}

	public Float getMinimalData (float[] listData){
		float minData=listData[0];
		for (int i=1; i<listData.length; i++){
			if (listData[i]<minData){
				minData=listData[i];
			}	
		}
		return minData;
	}
	public Float getMaximalData (float[] listData){
		float maxData=listData[0];
		for (int i=1; i<listData.length; i++){
			if (listData[i]>maxData){
				maxData=listData[i];
			}	
		}
		return maxData;
	}
}
