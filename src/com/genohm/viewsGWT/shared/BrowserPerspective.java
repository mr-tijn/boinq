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

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import com.genohm.viewsGWT.shared.data.TrackSpecification;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author martijn
 *
 */
@Entity @Table(name="browserperspective")
public class BrowserPerspective implements IsSerializable {
	protected Integer id;
	protected String name;
	protected String owner;
	protected Boolean isPublic = false;
	protected Boolean isDefault = false;
	protected List<TrackSpecification> tracks;
	//protected List<Integer> trackIds;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name="name",length=50)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(name="is_default")
	public Boolean getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="browserperspective_trackspecification", joinColumns={ @JoinColumn(name="perspective_id") },
			inverseJoinColumns = { @JoinColumn(name="track_id")})
	@OrderColumn(name="rank")
	public List<TrackSpecification> getTracks() {
		return tracks;
	}
	public void setTracks(List<TrackSpecification> tracks) {
		this.tracks = tracks;
	}
//	@ElementCollection
//	@CollectionTable(name="browserperspective_trackspecification", joinColumns=@JoinColumn(name="perspective_id"))
//	@Column(name="track_id")
//	public List<Integer> getTrackIds() {
//		return trackIds;
//	}
//	public void setTrackIds(List<Integer> trackIds) {
//		this.trackIds = trackIds;
//	}
	@Column(name="owner", length=50)
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
	
}
