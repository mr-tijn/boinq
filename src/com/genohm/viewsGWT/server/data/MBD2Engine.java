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
import java.util.LinkedList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.genohm.viewsGWT.shared.Location;
import com.genohm.viewsGWT.shared.data.feature.ContinuousValuedFeature;
import com.genohm.viewsGWT.shared.data.feature.GraphSegment;

public class MBD2Engine extends JdbcTemplate {

	public List<ContinuousValuedFeature> getMBD2Features(Long startPos, Long endPos, String chromosome, final String sampleId, final int minSize) {
		List<Peak> peaks = null;

		String tableName = String.format("%s_%s_%s", "peaks", sampleId, chromosome);
		//TODO: make it adaptable to compute averages on larger scales
		String queryString = String.format("select beg, end, height from %s where end >= ? and beg <= ? and beg - end > ? order by beg", tableName);
		peaks = query(queryString, new Object[]{startPos, endPos, minSize}, new RowMapper<Peak>() {

			@Override
			public Peak mapRow(ResultSet rs, int row) throws SQLException {
				Peak peak = new Peak();
				peak.start = rs.getLong(1);
				peak.end = rs.getLong(2);
				peak.value = rs.getInt(3);
				return peak;
			}

		});

		ContinuousValuedFeature newFeat = new ContinuousValuedFeature();
		newFeat.setLoc(new LinkedList<Location>());
		List<Peak> contiguousPeaks = new LinkedList<Peak>();
		Peak prev = null;
		for (Peak peak: peaks) {
			if (prev == null || peak.start == prev.end + 1) contiguousPeaks.add(peak);
			else {
				// add a location and a graph segment
				newFeat.getLoc().add(new Location(contiguousPeaks.get(0).start,peak.end,chromosome,true));
				GraphSegment<Integer> gs = new GraphSegment<Integer>();
				gs.setPositions(new LinkedList<Long>()); gs.setValues(new LinkedList<Integer>());
				for (Peak p: contiguousPeaks) {
					gs.getPositions().add(p.start);
					gs.getValues().add(p.value);
				}
				newFeat.getGraphSegments().add(gs);
				contiguousPeaks = new LinkedList<Peak>();
				contiguousPeaks.add(peak);
			}
		}

		List<ContinuousValuedFeature> features = new LinkedList<ContinuousValuedFeature>();
		features.add(newFeat);
		return features;

	}


	class Peak {
		public Long start;
		public Long end;
		public Integer value;
	}

}
