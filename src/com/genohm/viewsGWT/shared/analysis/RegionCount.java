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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.genohm.viewsGWT.shared.GenomicRegion;
import com.google.gwt.user.client.rpc.IsSerializable;

@Entity
@Table(name="regioncount")
public class RegionCount implements IsSerializable {
	private Long id;
	private Long count;
	private GenomicRegion region;
	private CountReadsPerRegion analysis;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	
	
	@Column(name="count")
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}

	//TODO: check why this isn't working
//	@OneToOne(fetch=FetchType.EAGER)
//	@JoinColumn(name="genomicregion_id")
//	public GenomicRegion getGenomicRegion() {
//		return region;
//	}
//	public void setGenomicRegion(GenomicRegion region) {
//		this.region = region;
//	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="analysis_id")
	public CountReadsPerRegion getAnalysis() {
		return analysis;
	}
	public void setAnalysis(CountReadsPerRegion analysis) {
		this.analysis = analysis;
	}
}
