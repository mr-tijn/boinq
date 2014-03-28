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
package com.genohm.viewsGWT.shared.query.region;

import java.util.LinkedList;
import java.util.List;

import com.genohm.viewsGWT.shared.GenomicRegion;

public class Intersection implements RegionSelection {

	protected enum IntersectionType {
		SOURCE,
		TARGET,
		MINIMAL,
		MAXIMAL
	};
	
	protected List<GenomicRegion> regions = null;
	protected RegionSelection source = null; 
	protected RegionSelection target = null;
	protected IntersectionType type = null;
	
	public List<GenomicRegion> getRegions() {
		return regions;
	}

	public void setRegions(List<GenomicRegion> regions) {
		this.regions = regions;
	}

	public RegionSelection getSource() {
		return source;
	}

	public void setSource(RegionSelection source) {
		this.source = source;
	}

	public RegionSelection getTarget() {
		return target;
	}

	public void setTarget(RegionSelection target) {
		this.target = target;
	}
	
	public IntersectionType getType() {
		return type;
	}

	public void setType(IntersectionType type) {
		this.type = type;
	}

	public Intersection(RegionSelection source, RegionSelection target, IntersectionType type) {
		setSource(source);
		setTarget(target);
	}
	
	@Override
	public void materialize() {
		regions = new LinkedList<GenomicRegion>();
		try {
		switch (getType()) {
		case SOURCE :
			source.materialize();
			for (GenomicRegion sourceRegion: source.getRegions()) {
				if (!target.getRegionsIn(sourceRegion).isEmpty()) regions.add(sourceRegion);
			}
			break;
		case TARGET :
			target.materialize();
			for (GenomicRegion targetRegion: target.getRegions()) {
				if (!source.getRegionsIn(targetRegion).isEmpty()) regions.add(targetRegion);
			}
			break;
		case MINIMAL :
			source.materialize();
			for (GenomicRegion sourceRegion: source.getRegions()) {
				for (GenomicRegion targetRegion : target.getRegionsIn(sourceRegion)) {
					GenomicRegion clippedRegion = new GenomicRegion();
					clippedRegion.setChromosome(sourceRegion.getChromosome());
					clippedRegion.setStrand(sourceRegion.getStrand());
					clippedRegion.setSpeciesId(sourceRegion.getSpeciesId());
					clippedRegion.setVisibleStart(Math.max(sourceRegion.getVisibleStart(), targetRegion.getVisibleStart()));
					clippedRegion.setVisibleEnd(Math.min(sourceRegion.getVisibleEnd(),targetRegion.getVisibleEnd()));
					regions.add(clippedRegion);
				}
			}
			break;
		case MAXIMAL :
			source.materialize();
			for (GenomicRegion sourceRegion : source.getRegions()) {
				List<GenomicRegion> targetRegions = target.getRegionsIn(sourceRegion);
				if (!targetRegions.isEmpty()) {
					GenomicRegion clippedRegion = new GenomicRegion();
					clippedRegion.setChromosome(sourceRegion.getChromosome());
					clippedRegion.setStrand(sourceRegion.getStrand());
					clippedRegion.setSpeciesId(sourceRegion.getSpeciesId());
					Long start = sourceRegion.getVisibleStart();
					Long end = sourceRegion.getVisibleEnd();
					for (GenomicRegion targetRegion: targetRegions) {
						if (targetRegion.getVisibleStart() < start) start = targetRegion.getVisibleStart();
						if (targetRegion.getVisibleEnd() > end) end = targetRegion.getVisibleEnd();
					}
				}
			}
		}
		} catch (Exception e) {
			//swallow
		}
		
	}

	@Override
	public List<GenomicRegion> getRegionsIn(GenomicRegion region) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
