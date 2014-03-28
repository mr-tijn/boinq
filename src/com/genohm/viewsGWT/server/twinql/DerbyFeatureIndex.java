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
package com.genohm.viewsGWT.server.twinql;

import java.io.File;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


import org.apache.commons.io.FileUtils;
import org.apache.derby.impl.io.vfmem.PathUtil;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/*
 * build and access a feature index database in Derby
 * for fast access to line-based files 
 */


public class DerbyFeatureIndex implements FeatureIndex {
	
	private static Logger log = Logger.getLogger(DerbyFeatureIndex.class);
	protected JdbcTemplate jdbcTemplate;
	protected String baseDir = "/Users/martijn";
	
	public DerbyFeatureIndex(String fileName) {
		DriverManagerDataSource dataSource = new DriverManagerDataSource("jdbc:derby:directory:"+ baseDir +"/idx_" + PathUtil.getBaseName(fileName) + ";create=true", "", "");
		dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public void init() {
		try {
			jdbcTemplate.execute("create table chr(" +
					"chr_id integer not null generated always as identity (start with 1, increment by 1) constraint chr_pk primary key, " +
					"chr_name varchar(50) constraint chr_name_unique unique)");
			jdbcTemplate.execute("create table feature_index (" +
					"startpos bigint not null, " +
					"endpos bigint not null, " +
					"filepos bigint not null, " +
					"chr_id int constraint chr_fk references chr (chr_id))");
			jdbcTemplate.execute("create index startindex on feature_index (startpos)");
			jdbcTemplate.execute("create index endindex on feature_index (endpos)");
			
		} catch (DataAccessException e) {
			log.error("Could not create datasource", e);
		}
	}
	
	@Override
	public Boolean exists() {
		try {
			DatabaseMetaData meta = jdbcTemplate.getDataSource().getConnection().getMetaData();
			ResultSet rs = meta.getTables(null, "APP", null, null);
			int tablecount;
			for (tablecount = 0; rs.next(); tablecount++) {}
			if (tablecount > 0) return true;
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	

	@Override
	public List<Long> getPositionsBetween(Long start, Long end, String chr) {
		
		String queryString = "select filepos from feature_index inner join chr on feature_index.chr_id = chr.chr_id where startpos < ? and endpos > ? and chr_name = ? ";
		List<Long> lines = jdbcTemplate.query(queryString,new Object[] {end, start, chr},new RowMapper<Long>() {

			@Override
			public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getLong(1);
			}
			
		});
		
		
		return lines;
	}

	@Override
	public void storePosition(Long position, Long start, Long end, String chr) throws Exception {
		// attempt to insert - will fail if exists due to uniqueness constraint
		String insertChrString = "insert into chr(chr_name) values (?)";
		String queryChrString = "select chr_id from chr where chr_name = ?";
		String insertPosString = "insert into feature_index (startpos, endpos, filepos, chr_id) values (?,?,?,?)";
		try {
			jdbcTemplate.update(insertChrString, new Object[] {chr});
		} catch (DataAccessException e) {
			//eatme
		}
		List<Integer> idList = jdbcTemplate.query(queryChrString, new Object[] {chr}, new RowMapper<Integer>() {

			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getInt(1);
			}
			
		});
		if (idList.isEmpty() || idList.size() > 1) {
			throw new Exception("Problem storing/fetching chromosome name");
		}
		Integer chr_id = idList.get(0);
		Integer rows = jdbcTemplate.update(insertPosString, new Object[] {start, end, position, chr_id});
		if (rows != 1) throw new Exception("Could not insert position");
	}
}
