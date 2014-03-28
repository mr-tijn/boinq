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

import javax.persistence.Transient;

import org.springframework.transaction.annotation.Transactional;

import com.genohm.viewsGWT.shared.Location;
import com.google.gwt.user.client.rpc.IsSerializable;

public class Feature implements IsSerializable {

	protected String id;
	protected String description;
	protected Double score;
	protected String name;
	protected Feature parent;
	protected List<Location> loc = new LinkedList<Location>();
	protected List<? extends Feature> subFeatures;

	public Feature(String id, String description, Double score, String name,Feature parent, List<Location> locList,
			List<? extends Feature> subFeatures) {
		super();
		this.id = id;
		this.description = description;
		this.score = score;
		this.name = name;
		this.parent = parent;
		if (locList != null) {
			for (Location newLoc: locList) this.loc.add(newLoc);
		}
		this.subFeatures = subFeatures;
	}

	public Feature getParent() {
		return parent;
	}

	public void setParent(Feature parent) {
		this.parent = parent;
	}

	public Feature() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Location> getLoc(){
		return loc;

	}

	public void setLoc(List<Location> loc){
		this.loc = loc;
	}

	public String getChr() {
		return getLoc().get(0).getChr();
	}
	
	public Long getStart() {
		Long minStart = getLoc().get(0).getStart();
		for (Location loc: getLoc()) {if (loc.getStart() < minStart) minStart = loc.getStart();}
		return minStart;
	}

	public Long getEnd() {
		Long maxEnd = getLoc().get(0).getEnd();
		for (Location loc: getLoc()) {if (loc.getEnd() > maxEnd) maxEnd = loc.getEnd();}
		return maxEnd;
	}

	public List<? extends Feature> getSubFeatures() {
		return subFeatures;
	}

	public void setSubFeatures(List<? extends Feature> subFeatures) {
		this.subFeatures = subFeatures;
	}

	@Override
	public String toString() {
		String result = "["+getId();
		if (getLoc() != null && getLoc().size() > 0) {
			result += "@";
			for (Location loc : getLoc()) result += loc + ";";
			result = result.substring(0, result.length() - 1);
		}
		if (getSubFeatures() != null) {
			result += "{";
			for (Feature subFeature : getSubFeatures()) result += subFeature;
			result += "}";
		}
		result += "]";
		return result;
	}

}