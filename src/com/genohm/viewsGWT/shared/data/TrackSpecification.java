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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.genohm.viewsGWT.shared.renderer.RendererSettings;
import com.google.gwt.user.client.rpc.IsSerializable;

@Entity()
@Table(name="trackspecification")
public class TrackSpecification implements IsSerializable {
	
	protected Integer id;
	protected String title;
	protected String description;
	protected FeatureDatasource featureDatasource;
	protected String filterExpression;
	protected RendererSettings rendererSettings;
	protected String owner;
	protected Boolean isPublic;
	protected Integer height = 50;
	
	public TrackSpecification() {}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="height")
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}

	@Column(name="title",length=50)
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name="description",length=500)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@OneToOne
	@JoinColumn(name="featuredatasource_id")
	public FeatureDatasource getFeatureDatasource() {
		return featureDatasource;
	}
	public void setFeatureDatasource(FeatureDatasource featureDatasource) {
		this.featureDatasource = featureDatasource;
	}

	@Column(name="filterexpression",length=1000)
	public String getFilterExpression() {
		return filterExpression;
	}
	public void setFilterExpression(String filterExpression) {
		this.filterExpression = filterExpression;
	}

	@OneToOne()
	@JoinColumn(name="renderersettings_id")
	public RendererSettings getRendererSettings() {
		return rendererSettings;
	}
	public void setRendererSettings(RendererSettings rendererSettings) {
		this.rendererSettings = rendererSettings;
	}

	@Column(name="owner")
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Column(name="ispublic")
	public Boolean getIsPublic() {
		return isPublic;
	}
	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}
	

}
