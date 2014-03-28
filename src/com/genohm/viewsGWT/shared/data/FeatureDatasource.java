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
package com.genohm.viewsGWT.shared.data;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.genohm.viewsGWT.client.FeatureServer;
import com.genohm.viewsGWT.client.FeatureServerAsync;
import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;

@Entity()
@Table(name="featuredatasource")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="DSTYPE", discriminatorType=DiscriminatorType.STRING)
public abstract class FeatureDatasource implements IsSerializable {
	
	protected Integer id;
	protected String name;
	protected String description;
	protected Boolean canBeFiltered;
	protected Boolean isPublic;
	protected String chromosomePrefix;
	protected String owner;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name = "filterable")
	public Boolean getCanBeFiltered() {
		return canBeFiltered;
	}
	public void setCanBeFiltered(Boolean canBeFiltered) {
		this.canBeFiltered = canBeFiltered;
	}
	
	@Column(name = "ispublic")
	public Boolean getIsPublic() {
		return isPublic;
	}
	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}
	
	@Column(name="chromosomeprefix")
	public String getChromosomePrefix() {
		return chromosomePrefix;
	}
	public void setChromosomePrefix(String chromosomePrefix) {
		this.chromosomePrefix = chromosomePrefix;
	}
	
	@Column(name = "owner", nullable = false)
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	@Transient
	public abstract Feature getFeatureById(FeatureServer featureServer, String featureId) throws Exception;
	
	@Transient
	public abstract void getFeatureById(FeatureServerAsync featureServer, String featureId, AsyncCallback<Feature> callback) throws Exception;
	
	@Transient
	public abstract List<Feature> getFeaturesByRegion(FeatureServer featureServer, GenomicRegion region, String filterExpression, Integer minFeatureSize, Integer minDetailedSize) throws Exception;
	
	@Transient
	public abstract void getFeaturesByRegion(FeatureServerAsync featureServer, GenomicRegion region, String filterExpression, Integer minFeatureSize, Integer minDetailedSize, AsyncCallback<List<Feature>> callback) throws Exception;

}
