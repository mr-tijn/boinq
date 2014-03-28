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
package com.genohm.viewsGWT.shared;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.google.gwt.user.client.rpc.IsSerializable;

@Entity @Table(name="roi")
public class RegionOfInterest implements IsSerializable {

	public static final int STATUS_PENDING = 0;
	public static final int STATUS_COMPUTING = 1;
	public static final int STATUS_READY = 2;
	public static final int STATUS_ERROR = 3;
	
	protected String name;
	protected Integer rank;
	protected Integer status = STATUS_PENDING;
	protected String expression;
	protected String owner;
	protected Boolean isPublic;
	protected Long id;
	protected Boolean regionStartRefersToFeatureStart;
	protected Boolean regionEndRefersToFeatureStart;
	protected Integer regionStartOffset;
	protected Integer regionEndOffset;
	
	
	protected List<GenomicRegion> regions;
	// todo: implement: needs to know its renderer
	protected Feature focusedFeature;
	
	public RegionOfInterest() {}
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	

	@Column(name="name",length=50)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="rank")
	public Integer getRank() {
		return rank;
	}
	public void setRank(Integer rank) {
		this.rank = rank;
	}
	
	@Column(name="status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name="expression")
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Column(name="owner",length=50)
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	@Column(name="is_public")
	public Boolean getIsPublic() {
		return isPublic;
	}
	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}
	
	
	@Column(name="regionStartFromStart")
	public Boolean getRegionStartRefersToFeatureStart() {
		return regionStartRefersToFeatureStart;
	}

	public void setRegionStartRefersToFeatureStart(
			Boolean regionStartRefersToFeatureStart) {
		this.regionStartRefersToFeatureStart = regionStartRefersToFeatureStart;
	}

	@Column(name="regionEndFromStart")
	public Boolean getRegionEndRefersToFeatureStart() {
		return regionEndRefersToFeatureStart;
	}

	public void setRegionEndRefersToFeatureStart(
			Boolean regionEndRefersToFeatureStart) {
		this.regionEndRefersToFeatureStart = regionEndRefersToFeatureStart;
	}

	@Column(name="regionStartOffset")
	public Integer getRegionStartOffset() {
		return regionStartOffset;
	}

	public void setRegionStartOffset(Integer regionStartOffset) {
		this.regionStartOffset = regionStartOffset;
	}

	@Column(name="regionEndOffset")
	public Integer getRegionEndOffset() {
		return regionEndOffset;
	}

	public void setRegionEndOffset(Integer regionEndOffset) {
		this.regionEndOffset = regionEndOffset;
	}

	@OneToMany(targetEntity=GenomicRegion.class, fetch=FetchType.EAGER) //cascade=CascadeType.ALL, orphanRemoval=true
	@JoinColumn(name="roi_id")
	public List<GenomicRegion> getRegions() {
		return regions;
	}
	public void setRegions(List<GenomicRegion> regions) {
		this.regions = regions;
	}

	@Transient
	public RegionOfInterest deepCopy() {
		RegionOfInterest copy = new RegionOfInterest();
		copy.setId(id);
		copy.setIsPublic(isPublic);
		copy.setName(name);
		copy.setOwner(owner);
		copy.setRank(rank);
		copy.setStatus(status);
		copy.setExpression(expression);
		List<GenomicRegion> newRegions = new LinkedList<GenomicRegion>();
		newRegions.addAll(regions);
		copy.setRegions(newRegions);
		return copy;
	}


}
