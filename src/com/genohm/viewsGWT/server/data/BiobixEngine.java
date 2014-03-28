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
package com.genohm.viewsGWT.server.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.genohm.viewsGWT.shared.Location;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.genohm.viewsGWT.shared.data.feature.PairedEndFeature;

public class BiobixEngine extends JdbcTemplate {
	
	private static final int POSITION_OFFSET = 1;
	private static final Logger log = Logger.getLogger(BiobixEngine.class);
	private int readLength = 100; // approximate length of reads
	
	public List<Feature> getAssemblyData(final Long start, final Long end, final String chromosome, final String sampleId, final int minSize) {
    	log.debug("Querying for reads");
    	Date startDate = new Date();
    	List<Feature> result = null;
		String tableName = String.format("%s_%s_%s", "alignment", sampleId, chromosome);
		if (minSize < 1) {
			// nucleotide level
			String queryString = String.format("select beg, sequence, quality, begR, sequenceR, qualityR from %s where begR >= ? and beg <= ? order by beg", tableName);
			result = super.query(queryString, new Object[] {start - readLength, end}, new RowMapper<Feature>() {
				@Override
				public PairedEndFeature mapRow(ResultSet rs, int rowNum) throws SQLException {
					Long startF = POSITION_OFFSET + rs.getLong(1);
					String sequenceF = rs.getString(2);
					Long endF = startF + sequenceF.length() - 1;
					Long startR = POSITION_OFFSET + rs.getLong(4);
					String sequenceR = rs.getString(5);
					Long endR = startR + sequenceR.length() - 1;
					List<Location> loc = new LinkedList<Location>();
					Location locFW = new Location(startF, endF, chromosome, true);
					Location locBW = new Location(startR, endR, chromosome, true);
					loc.add(locFW);
					loc.add(locBW);
					String codedQualityF = rs.getString(3);
					String codedQualityR = rs.getString(6);
					int[] scoreF = new int[codedQualityF.length()];
					int[] scoreR = new int[codedQualityR.length()];
					for (int i= 0; i < codedQualityF.length(); i++) {
						char encoded = codedQualityF.charAt(i);
						scoreF[i] = encoded - 33;
					}
					for (int i= 0; i < codedQualityR.length(); i++) {
						char encoded = codedQualityR.charAt(i);
						scoreR[i] = encoded - 33;
					}
					PairedEndFeature feature = new PairedEndFeature();
					feature.setLoc(loc);
					feature.setSequenceForward(sequenceF);
					feature.setSequenceBackward(sequenceR);
					feature.setQualityForward(scoreF);
					feature.setQualityBackward(scoreR);
					return feature;
				}
			});
		} else {
			String queryString = String.format("select beg, sequence, begR, sequenceR from %s where begR >= ? and beg <= ? and begR - beg > ? order by beg", tableName);
			result = super.query(queryString, new Object[] {start - readLength, end, minSize - readLength }, new RowMapper<Feature>() {
				@Override
				public PairedEndFeature mapRow(ResultSet rs, int rowNum) throws SQLException {
					Long startF = POSITION_OFFSET + rs.getLong(1);
					Long startR = POSITION_OFFSET + rs.getLong(3);
					Long endF = startF + rs.getString(2).length() - 1;
					Long endR = startR + rs.getString(4).length() - 1;
					List<Location> loc = new LinkedList<Location>();
					Location locFW = new Location(startF, endF, chromosome, true);
					Location locBW = new Location(startR, endR, chromosome, true);
					loc.add(locFW);
					loc.add(locBW);
					PairedEndFeature feature = new PairedEndFeature();
					feature.setLoc(loc);
					return feature;
				}
			});
		}
    	Date endDate = new Date();
    	log.debug("Query took "+(endDate.getTime() - startDate.getTime()) + " ms");
    	return result;
	}

	public int getReadLength() {
		return readLength;
	}

	public void setReadLength(int readLength) {
		this.readLength = readLength;
	}
	
	
}
