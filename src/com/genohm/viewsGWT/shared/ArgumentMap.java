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

import java.util.HashMap;

import com.genohm.viewsGWT.client.ViewPort;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ArgumentMap extends HashMap<String, String> implements IsSerializable {
	private static final long serialVersionUID = 3982900353856320859L;
	// convenience class with constructors to fill argument map
	// also necessary to wrap a map so it can implement IsSerializable (necessary for GWT RPC)
	public ArgumentMap() {
		super();
	}
//	public ArgumentMap(ViewPort viewPort) {
//		super();
//		put("start", viewPort.getGenomicStart().toString());
//		put("end", viewPort.getGenomicEnd().toString());
//		put("chromosome", viewPort.getChromosome());
//		put("species", viewPort.getSpecies().getValue().toString());
//		put("strand", viewPort.getStrand().toString());
//	}
	public ArgumentMap(GenomicRegion region) {
		super();
		setGenomicRegion(region);
	}
	public ViewPort getViewPort() {
		ViewPort viewPort = new ViewPort();
		viewPort.setGenomicStart(Long.parseLong(get("start")));
		viewPort.setGenomicEnd(Long.parseLong(get("end")));
		viewPort.setChromosome(get("chromosome"));
		viewPort.setSpecies(Species.getByID(Integer.parseInt(get("species"))));
		viewPort.setStrand(Boolean.parseBoolean(get("strand")));
		return viewPort;
	}
	public void setFilterExpression(String filterExpression) {
		put("filterExpression", filterExpression);
	}
	public String getFilterExpression() {
		return get("filterExpression");
	}
	public GenomicRegion getGenomicRegion() {
		GenomicRegion genomicRegion = new GenomicRegion();
		genomicRegion.setVisibleStart(Long.parseLong(get("start")));
		genomicRegion.setVisibleEnd(Long.parseLong(get("end")));
		genomicRegion.setChromosome(get("chromosome"));
		genomicRegion.setSpecies(Species.getByID(Integer.parseInt(get("species"))));
		genomicRegion.setStrand(Boolean.parseBoolean(get("strand")));
		return genomicRegion;
	}
	public void setGenomicRegion(GenomicRegion region) {
		put("start", region.getVisibleStart().toString());
		put("end", region.getVisibleEnd().toString());
		put("chromosome", region.getChromosome());
		put("species", region.getSpecies().getValue().toString());
		put("strand", region.getStrand().toString());
	}
	public void setMinFeatureWidth(Integer width) {
		put("minFeatureWidth",width.toString());
	}
	public Integer getMinFeatureWidth() {
		return Integer.parseInt(get("minFeatureWidth"));
	}
	public ZoomLevel getZoomLevel() {
		return ZoomLevel.parse(get("zoomLevel"));
	}
	public void setZoomLevel(ZoomLevel level) {
		put("zoomLevel",level.toString());
	}
	public String getId() {
		return get("id");
	}
	public void setId(String id) {
		put("id",id);
	}
	public Integer getMinDetailWidth() {
		return Integer.parseInt(get("minDetailWidth"));
	}
	public void setMinDetailWidth(Integer minDetailWidth) {
		put("minDetailWidth", minDetailWidth.toString());
	}
}
