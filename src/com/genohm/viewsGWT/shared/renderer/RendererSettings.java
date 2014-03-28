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
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.genohm.viewsGWT.client.util.ColorTools;
import com.google.gwt.user.client.rpc.IsSerializable;

@Entity
@Table(name="renderersettings")
@DiscriminatorColumn(name="RSTYPE")
@DiscriminatorValue("SUPER")
public class RendererSettings /*extends HashMap<String,Serializable>*/ implements IsSerializable {
	
	protected Integer id;
	protected String rendererName;
	protected String prefixChromosome = "";
	protected Integer featureHeight = 40;
	protected String featureColor = "green";
	protected String lineColor = "black";
	protected Integer lineWidth = 1;
	protected Integer gapWidth = featureHeight/5;
	protected Double minHue = 0.;
	protected Double maxHue = .9;
	protected Double minScore = .0;
	protected Double maxScore = 100.;
	protected String mainFeatureColor = "green";
	protected String subFeatureColor = "gray";

	private static final long serialVersionUID = 6751837382356463752L;

	public RendererSettings(String rendererName, /*String prefixChromosome, */Integer featureHeight,
			String featureColor, String lineColor, Integer linewidth, Integer gapWidth, Double minValue,
			Double maxValue, Double minHue, Double maxHue) {
		super();
		setRendererName(rendererName);
//		setPrefixChromosome(prefixChromosome);
		setFeatureHeight(featureHeight);
		setFeatureColor(featureColor);
		setLineColor(lineColor);
		setLineWidth(linewidth);
		setGapWidth(gapWidth);
		setMinHue(minHue);
		setMaxHue(maxHue);
		setMinScore(minValue);
		setMaxScore(maxValue);
	}

	public RendererSettings() {
		super();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="renderername",length=50)
	public String getRendererName() {
//		return (String) get("rendererName");
		return rendererName;
	}
	public void setRendererName(String rendererName) {
//		put("rendererName",rendererName);
		this.rendererName = rendererName;
	}
//	public String getPrefixChromosome() {
////		return (String) get("chromosome");
//		return prefixChromosome;
//	}
//	public void setPrefixChromosome(String prefixChromosome) {
////		put("chromosome",chromosome);
//		this.prefixChromosome = prefixChromosome;
//	}
	
	@Column(name="featureheight")
	public Integer getFeatureHeight() {
		//return (Integer) get("featureheight");
		return featureHeight;
	}
	public void setFeatureHeight(Integer featureHeight) {
//		put("featureheight", featureheight);
		this.featureHeight = featureHeight;
	}

	@Column(name="featurecolor")
	public String getFeatureColor() {
//		return (String) get("featureColor");
		return featureColor;
	}
	public void setFeatureColor(String featureColor) {
//		put("featureColor",featureColor);
		this.featureColor = featureColor;
	}
	
	@Column(name="linecolor")
	public String getLineColor() {
//		return (String ) get("lineColor");
		return lineColor;
	}
	public void setLineColor(String lineColor) {
//		put("lineColor",lineColor);
		this.lineColor = lineColor;
	}
	
	@Column(name="linewidth")
	public Integer getLineWidth() {
//		return (Integer) get("lineWidth");
		return lineWidth;
	}
	public void setLineWidth(Integer linewidth) {
//		put("linewidth",linewidth);
		this.lineWidth = linewidth;
	}
	
	@Column(name="gapwidth")
	public Integer getGapWidth() {
//		return (Integer) get("gapWidth");
		return gapWidth;
	}
	public void setGapWidth(Integer gapWidth) {
//		put("gapWidth",gapWidth);
		this.gapWidth = gapWidth;
	}

	@Column(name="minhue")
	public Double getMinHue(){
//		return (Double) get("minHue");
		return minHue;
	}
	public void setMinHue(Double minHue){
//		put("minHue",minHue);
		this.minHue = minHue;
	}

	@Column(name="maxhue")
	public Double getMaxHue(){
//		return (Double) get("maxHue");
		return maxHue;
	}
	public void setMaxHue(Double maxHue){
//		put("maxHue",maxHue);
		this.maxHue = maxHue;
	}

	@Column(name="minscore")
	public Double getMinScore(){
//		return (Double) get("minVal");
		return minScore;
	}
	public void setMinScore(Double minScore){
//		put("minVal",minVal);
		this.minScore = minScore;
	}

	@Column(name="maxscore")
	public Double getMaxScore(){
//		return (Double) get("maxVal");
		return maxScore;
	}
	public void setMaxScore(Double maxScore){
//		put("maxVal",maxVal);
		this.maxScore = maxScore;
	}

	@Column(name="mainfeaturecolor")
	public String getMainFeatureColor() {
		return mainFeatureColor;
	}
	public void setMainFeatureColor(String mainFeatureColor) {
		this.mainFeatureColor = mainFeatureColor;
	}

	@Column(name="subfeaturecolor")
	public String getSubFeatureColor() {
		return subFeatureColor;
	}
	public void setSubFeatureColor(String subFeatureColor) {
		this.subFeatureColor = subFeatureColor;
	}



	public String getColor(Double score) {
		return ColorTools.getColor(score, getMinScore(), getMaxScore(), getMinHue(), getMaxHue());
	}
 

}
