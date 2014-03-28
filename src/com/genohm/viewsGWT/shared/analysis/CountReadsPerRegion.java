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

import java.beans.Transient;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.gwt.user.client.rpc.IsSerializable;


@Entity
@Table(name="analysis")
public class CountReadsPerRegion implements Analysis, IsSerializable {

	private Long id;
	private String analysisType = "countreadsperregion";
	private Integer status;
	private String description;
	private String resultSummary;
	private String owner;
	private String name;
	private Boolean isPublic;

	private Set<RegionCount> regionCounts;
	private Long roiId;
	private Integer trackId;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	@Override
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	

	@Column(name="analysis_type", length=20)
	public String getAnalysisType() {
		return analysisType;
	}
	private void setAnalysisType(String analysisType) {
		this.analysisType = analysisType;
	}
	
	@Column(name="status")
	@Override
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Column(name="description")
	@Override
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
//	@Transient
//	@Override
//	public String getStatusText() {
//		return statusText[getStatus()];
//	}
//	private void setStatusText(String brol) {
//		//TODO: check why we need to put this bogus
//		//setter for a transient field
//	}
	

	@Column(name="resultsummary")
	@Override
	public String getResultSummary() {
		return resultSummary;
	}
	public void setResultSummary(String resultSummary) {
		this.resultSummary = resultSummary;
	}
	
	@Column(name="owner",length=50,nullable=false)
	@Override
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Column(name="name",length=100)
	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(name="is_public")
	@Override
	public Boolean getIsPublic() {
		return isPublic;
	}
	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}
	
	@OneToMany(mappedBy="analysis",fetch=FetchType.EAGER)
	public Set<RegionCount> getRegionCounts() {
		return regionCounts;
	}
	public void setRegionCounts(Set<RegionCount> regionCounts) {
		this.regionCounts = regionCounts;
	}
	
	@Column(name="roi_id")
	public Long getRoiId() {
		return roiId;
	}
	public void setRoiId(Long roiId) {
		this.roiId = roiId;
	}
	
	@Column(name="track_id")
	public Integer getTrackId() {
		return trackId;
	}
	public void setTrackId(Integer trackId) {
		this.trackId = trackId;
	}

	@Transient
	public CountReadsPerRegion deepCopy() {
		CountReadsPerRegion copy = new CountReadsPerRegion();
		copy.setId(id);
		copy.setIsPublic(isPublic);
		copy.setName(name);
		copy.setOwner(owner);
		copy.setStatus(status);
		copy.setDescription(description);
		copy.setRoiId(roiId);
		copy.setTrackId(trackId);
		Set<RegionCount> newRegionCounts = new HashSet<RegionCount>();
		for (RegionCount regionCount : regionCounts) {
			regionCount.setAnalysis(copy);
			newRegionCounts.add(regionCount);
		}
		copy.setRegionCounts(newRegionCounts);
		return copy;
	}

	@Transient
	@Override
	public void acceptVisualizer(AnalysisVisualizer visualizer) {
		visualizer.visitAnalysis(this);
	}

	@Transient
	@Override
	public void acceptProcessor(AnalysisProcessor processor) {
		processor.visitAnalysis(this);
	}
	@Override
	public Analysis deepcopy() {
		CountReadsPerRegion result = new CountReadsPerRegion();
		result.analysisType = analysisType;
		result.description = description;
		result.id = id;
		result.isPublic = isPublic;
		result.name = name;
		result.owner = owner;
		Set<RegionCount> newRegionCounts = new HashSet<RegionCount>();
		for (RegionCount orig: regionCounts) {
			orig.setAnalysis(result);
			newRegionCounts.add(orig); 
		}
		result.regionCounts = newRegionCounts;
		result.resultSummary = resultSummary;
		result.roiId = roiId;
		result.status = status;
		result.trackId = trackId;
		return result;
	}
	
	

}
