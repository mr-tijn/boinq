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
package com.genohm.viewsGWT.server.security;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.springframework.security.core.userdetails.UserDetails;

//TODO: fix schema stuff (postgres requires schema="public" vs mysql)
// Two user objects exist: a server object that implements the spring stuff
// and a shared object that is serializable
// they share the same table...
@Entity()
@Table(name="user")
public class ViewsUserDetails implements UserDetails {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8726635836093694519L;
	private Integer id;
	private String username;
	private String password;
	private Set<Authority> authorities;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Override
	@Column(name = "username", unique = true, nullable = false)
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Override
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "userId")
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	public Set<Authority> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(Set<Authority> authorities) {
		this.authorities = authorities;
	}
	@Override
	@Column(name = "password", nullable = false)
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	@Transient
	public boolean isAccountNonExpired() {
		return true;
	}
	@Override
	@Transient
	public boolean isAccountNonLocked() {
		return true;
	}
	@Override
	@Transient
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	@Transient
	public boolean isEnabled() {
		return true;
	}
}



