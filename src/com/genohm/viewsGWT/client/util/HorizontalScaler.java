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
package com.genohm.viewsGWT.client.util;

import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.Location;
import com.genohm.viewsGWT.shared.data.feature.Feature;

public class HorizontalScaler {
	
	protected static final int MIN_PIXELS_FOR_FEATURE = 1;
	protected static final int MIN_PIXELS_FOR_DETAIL = 100;
	protected ScreenRegion screenRegion;
	protected GenomicRegion genomicRegion;
	
	
	public HorizontalScaler(ScreenRegion screenRegion, GenomicRegion genomicRegion) {
		this.screenRegion = screenRegion;
		this.genomicRegion = genomicRegion;
	}
	public long getLowerGenomicPosition(int screenPosition) {
		return genomicRegion.getVisibleStart() + (genomicRegion.getWidth() * (screenPosition - screenRegion.getVisibleStart())) / screenRegion.getVisibleWidth(); 
	}
	public long getHigherGenomicPosition(int screenPosition) {
		return 1 + getLowerGenomicPosition(screenPosition);
	}
	public int nearestBaseBorder(int screenPosition) {
		return Math.round(getBaseWidth() * Math.round(screenPosition/getBaseWidth()));
	}
	public float getBaseWidth() {
		return (float) screenRegion.getVisibleWidth() / genomicRegion.getWidth();
	}
	public int getMinVisibleWidth() {
		return Math.round(MIN_PIXELS_FOR_FEATURE/getBaseWidth());
	}
	public int getMinDetailWidth() {
		return Math.round(MIN_PIXELS_FOR_DETAIL/getBaseWidth());
	}
	public long getVisibleStart(Feature feature) {
		return Math.max(genomicRegion.getRegionStart(),feature.getLoc().get(0).getStart());
	}
	public long getVisibleEnd(Feature feature) {
		return Math.min(feature.getLoc().get(0).getEnd(), genomicRegion.getRegionEnd()) ;
	}
	public int getVisibleLength(Feature feature) {
		return 1 + (int) (getVisibleEnd(feature) - getVisibleStart(feature));
	}
	public int getLeft(Feature feature) {
		if (genomicRegion.getStrand()) {
			return Math.max(screenRegion.getDrawStart(),Math.min(screenRegion.getDrawEnd(),(int) Math.floor((feature.getStart() - genomicRegion.getVisibleStart()) * getBaseWidth() )));
		} else {
			return Math.max(screenRegion.getDrawStart(),Math.min(screenRegion.getDrawEnd(),(int) Math.floor((genomicRegion.getVisibleEnd() - feature.getEnd()) * getBaseWidth() )));
		}
	}
	
	public int getLeft(Location loc) {
		if (genomicRegion.getStrand()) {
			return Math.max(screenRegion.getDrawStart(),Math.min(screenRegion.getDrawEnd(),(int) Math.floor((loc.getStart() - genomicRegion.getVisibleStart()) * getBaseWidth() )));
		} else {
			return Math.max(screenRegion.getDrawStart(),Math.min(screenRegion.getDrawEnd(),(int) Math.floor((genomicRegion.getVisibleEnd() - loc.getEnd()) * getBaseWidth() )));
		}
	}
	
	
	public int getRight(Feature feature) {
		if (genomicRegion.getStrand()) {
			return Math.min(screenRegion.getDrawEnd(), Math.max(screenRegion.getDrawStart(), (int) Math.floor((1 + feature.getEnd() - genomicRegion.getVisibleStart()) * getBaseWidth() )));
		} else {
			return Math.min(screenRegion.getDrawEnd(), Math.max(screenRegion.getDrawStart(), (int) Math.floor((1 + genomicRegion.getVisibleEnd() - feature.getStart()) * getBaseWidth() )));
		}
	}
	
	public int getRight(Location loc) {
		if (genomicRegion.getStrand()) {
			return Math.min(screenRegion.getDrawEnd(), Math.max(screenRegion.getDrawStart(), (int) Math.floor((1 + loc.getEnd() - genomicRegion.getVisibleStart()) * getBaseWidth() )));
		} else {
			return Math.min(screenRegion.getDrawEnd(), Math.max(screenRegion.getDrawStart(), (int) Math.floor((1 + genomicRegion.getVisibleEnd() - loc.getStart()) * getBaseWidth() )));
		}
	}
	
	public int getWidth(Feature feature) {
		return 1 + getRight(feature) - getLeft(feature);
	}
	
	public int getWidth(Location loc) {
		return 1 + getRight(loc) - getLeft(loc);
	}
	
	public Boolean getDirection(Feature feature) {
		return feature.getLoc().get(0).getStrand() == genomicRegion.getStrand();
	}
	
	public Boolean getDirection(Location loc) {
		return loc.getStrand() == genomicRegion.getStrand();
	}
	
	public Boolean withinRegion(Feature feature) {
		// draw when within visible region
		for (Location loc: feature.getLoc()) {
			if (withinRegion(loc)) return true;
		}
		return false;
 	}
	
	public Boolean withinRegion(Location location) {
		return (location.getEnd() >= genomicRegion.getVisibleStart() - genomicRegion.getWidth() && location.getStart() <= genomicRegion.getVisibleEnd() + genomicRegion.getWidth());
		/*&& (loc.getChr().equals(genomicRegion.getChromosome()) || loc.getChr().equals(Format1(genomicRegion.getChromosome())))||loc.getChr().equals(Format2(genomicRegion.getChromosome()))*/
	}
	
	public ScreenRegion getScreenRegion() {
		return screenRegion;
	}
	public void setScreenRegion(ScreenRegion screenRegion) {
		this.screenRegion = screenRegion;
	}
	public GenomicRegion getGenomicRegion() {
		return genomicRegion;
	}
	public void setGenomicRegion(GenomicRegion genomicRegion) {
		this.genomicRegion = genomicRegion;
	}
	
	
	public int getLeft(GroupData ch){ 
		return Math.max(screenRegion.getDrawStart(),Math.min(screenRegion.getDrawEnd(),(int) Math.floor((ch.getStart() - genomicRegion.getVisibleStart()) * getBaseWidth() )));
	}
	
	public int getRight (GroupData ch){ 
		return Math.min(screenRegion.getDrawEnd(), Math.max(screenRegion.getDrawStart(), (int) Math.floor((1+ (ch.getStart()+ch.getWidth()-1) - genomicRegion.getVisibleStart()) * getBaseWidth() )));
	}
	
	public int getWidth(GroupData ch){
		return 1+getRight(ch)-getLeft(ch); 
	}
	
	
	//FIXME: wtf ?
	public String Format1 (String chromosome){
		if (chromosome.contains("22")){chromosome = "chr22";}
		else if( chromosome.contains("21")){chromosome = "chr21";}
		else if( chromosome.contains("20")){chromosome = "chr20";}
		else if( chromosome.contains("19")){chromosome = "chr19";}
		else if( chromosome.contains("18")){chromosome = "chr18";}
		else if( chromosome.contains("17")){chromosome = "chr17";}
		else if( chromosome.contains("16")){chromosome = "chr16";}
		else if( chromosome.contains("15")){chromosome = "chr15";}
		else if( chromosome.contains("14")){chromosome = "chr14";}
		else if( chromosome.contains("13")){chromosome = "chr13";}
		else if( chromosome.contains("12")){chromosome = "chr12";}
		else if( chromosome.contains("11")){chromosome = "chr11";}
		else if( chromosome.contains("10")){chromosome = "chr10";}
		else if( chromosome.contains("9")){chromosome = "chr9";}
		else if( chromosome.contains("8")){chromosome = "chr8";}
		else if( chromosome.contains("7")){chromosome = "chr7";}
		else if( chromosome.contains("6")){chromosome = "chr6";}
		else if( chromosome.contains("5")){chromosome = "chr5";}
		else if( chromosome.contains("4")){chromosome = "chr4";}
		else if( chromosome.contains("3")){chromosome = "chr3";}
		else if( chromosome.contains("2")){chromosome = "chr2";}
		else if( chromosome.contains("1")){chromosome = "chr1";}
		else if( chromosome.contains("x")){chromosome = "chrx";}
		else if( chromosome.contains("y")){chromosome = "chry";}
		return chromosome;
	}
	public String Format2 (String chromosome){
		if (chromosome.contains("22")){chromosome = "Chr22";}
		else if( chromosome.contains("21")){chromosome = "Chr21";}
		else if( chromosome.contains("20")){chromosome = "Chr20";}
		else if( chromosome.contains("19")){chromosome = "Chr19";}
		else if( chromosome.contains("18")){chromosome = "Chr18";}
		else if( chromosome.contains("17")){chromosome = "Chr17";}
		else if( chromosome.contains("16")){chromosome = "Chr16";}
		else if( chromosome.contains("15")){chromosome = "Chr15";}
		else if( chromosome.contains("14")){chromosome = "Chr14";}
		else if( chromosome.contains("13")){chromosome = "Chr13";}
		else if( chromosome.contains("12")){chromosome = "Chr12";}
		else if( chromosome.contains("11")){chromosome = "Chr11";}
		else if( chromosome.contains("10")){chromosome = "Chr10";}
		else if( chromosome.contains("9")){chromosome = "Chr9";}
		else if( chromosome.contains("8")){chromosome = "Chr8";}
		else if( chromosome.contains("7")){chromosome = "Chr7";}
		else if( chromosome.contains("6")){chromosome = "Chr6";}
		else if( chromosome.contains("5")){chromosome = "Chr5";}
		else if( chromosome.contains("4")){chromosome = "Chr4";}
		else if( chromosome.contains("3")){chromosome = "Chr3";}
		else if( chromosome.contains("2")){chromosome = "Chr2";}
		else if( chromosome.contains("1")){chromosome = "Chr1";}
		else if( chromosome.contains("x")){chromosome = "Chrx";}
		else if( chromosome.contains("y")){chromosome = "Chry";}
		return chromosome;
	}
	
}
