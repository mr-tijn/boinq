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
package com.genohm.viewsGWT.shared.renderer;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.google.gwt.user.client.rpc.IsSerializable;

@Entity
@DiscriminatorValue("GRAPH")
public class RendererGraphSettings extends RendererSettings implements IsSerializable{

	protected String typeGraph = "bar";
	protected Integer graphHeight = 40;
	protected String graphColor = "green";
	protected String frameworkColor = "black";
	protected Integer graphLineWidth = 1;
	protected Boolean showGrid;

	private static final long serialVersionUID = 6751837382356463752L; 

	public RendererGraphSettings(String rendererName, Integer featureheight,
			String featureColor, String lineColor, Integer linewidth, Integer gapWidth, Double minValue,
			Double maxValue, Double minHue, Double maxHue, String typeGraph,Integer graphHeight,String frameworkColor, String graphColor, Integer GraphlineWidth, boolean showGrid) {
		super(rendererName,featureheight,featureColor,lineColor,linewidth,gapWidth,minValue,maxValue,minHue,maxHue);
		setRendererName(rendererName);
		setTypeGraph(typeGraph);
		setGraphHeight(graphHeight);
		setFrameworkColor(frameworkColor);
		setGraphColor(graphColor);
		setGraphLineWidth(GraphlineWidth);
		setShowGrid(showGrid);
	}

	public RendererGraphSettings() {
		super();
	}

	
	
	@Column(name="typegraph")
	public String getTypeGraph() {
//		return (String) get("typeGraph");
		return typeGraph;
	}
	public void setTypeGraph(String typeGraph) {
//		put("typeGraph",typeGraph);
		this.typeGraph = typeGraph;
	}

	@Column(name="graphheight")
	public Integer getGraphHeight() {
//		return (Integer) get("graphHeight");
		return graphHeight;
	}
	public void setGraphHeight(Integer graphHeight) {
//		put("graphHeight",graphHeight);\
		this.graphHeight = graphHeight;
	}

	@Column(name="graphcolor")
	public void setGraphColor(String graphColor) {
//		put("graphColor",graphColor);
		this.graphColor = graphColor;
	}
	public String getGraphColor (){
//		return (String) get("graphColor");
		return graphColor;
	}

	@Column(name="frameworkcolor")
	public void setFrameworkColor(String frameworkColor){
//		put("frameworkColor", frameworkColor);
		this.frameworkColor = frameworkColor;
	}
	public String getFrameworkColor (){
//		return (String) get("frameworkColor");
		return frameworkColor;
	}

	@Column(name="graphlinewidth")
	public Integer getGraphLineWidth (){
//		return (Integer) get("graphLineWidth");
		return graphLineWidth;
	}
	public void setGraphLineWidth(Integer graphLineWidth) {
		//put("graphlineWidth",graphlineWidth);
		this.graphLineWidth = graphLineWidth;
	}
	
	
	@Column(name="showgrid")
	public Boolean getShowGrid (){
//		return (Boolean) get("showGrid");
		return showGrid;
	}
	public void setShowGrid(Boolean showGrid) {
//		put("showGrid",showGrid);
		this.showGrid = showGrid;
	}

}
