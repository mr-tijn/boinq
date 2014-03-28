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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gwt.user.client.rpc.IsSerializable;

@Entity @Table(name="genomicregion")
public class GenomicRegion implements IsSerializable {
	
	protected Long id;
	protected Long visibleStart;
	protected Long visibleEnd;
	protected String chromosome;
	//protected Species species;
	protected Boolean strand;
	protected Integer speciesId;
	protected String name;
	public GenomicRegion() {}
	public GenomicRegion(Long start, Long end, String chromosome, Species species, Boolean strand) {
		super();
		this.name = null;
		this.visibleStart = start;
		this.visibleEnd = end;
		this.chromosome = chromosome;
		//this.species = species;
		this.speciesId = species.getValue();
		this.strand = strand;
	}
	public GenomicRegion(String name, Long start, Long end, String chromosome, Species species, Boolean strand) {
		this(start, end, chromosome, species, strand);
		this.visibleStart = start;
		this.visibleEnd = end;
		this.chromosome = chromosome;
		//this.species = species;
		this.speciesId = species.getValue();
		this.strand = strand;
		this.name = name;
	}
	
	@Transient
	public GenomicRegion getDuplicate() {
		return new GenomicRegion(visibleStart,visibleEnd,chromosome,getSpecies(),strand);
	}
	@Transient
	public Boolean contains(GenomicRegion region) {
		if (chromosome.equalsIgnoreCase(region.getChromosome()) && getSpecies() == region.getSpecies()) {
			return (visibleStart <= region.visibleStart && visibleEnd >= region.visibleEnd);
		}
		return false;
	}
	@Transient
	public Long getRegionStart() {
		return visibleStart - getWidth();
	}
	@Transient
	public Long getRegionEnd() {
		return visibleEnd + getWidth();
	}
	

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	
	@Column(name="visible_start")
	public Long getVisibleStart() {
		return visibleStart;
	}
	public void setVisibleStart(Long start) {
		this.visibleStart = start;
	}
	@Column(name="visible_end")
	public Long getVisibleEnd() {
		return visibleEnd;
	}
	public void setVisibleEnd(Long end) {
		this.visibleEnd = end;
	}
	@Column(name="contig")
	public String getChromosome() {
		return chromosome;
	}
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}
	@Column(name="strand")
	public Boolean getStrand() {
		return strand;
	}
	public void setStrand(Boolean strand) {
		this.strand = strand;
	}
	@Column(name="species_id")
	public Integer getSpeciesId() {
		return speciesId;
	}
	public void setSpeciesId(Integer speciesId) {
		this.speciesId = speciesId;
	}
	@Column(name="name",length=50)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Transient
	public Species getSpecies() {
		return Species.getByID(speciesId);
	}
	public void setSpecies(Species species) {
		this.speciesId = species.getValue();
	}
	@Transient
	public Long getWidth() {
		return 1L + visibleEnd - visibleStart;
	}
	@Transient
	public Boolean complete() {
		return (getSpecies() != null && chromosome != null && visibleStart != null && visibleEnd != null);
	}
	@Transient
	public Boolean equals(GenomicRegion region) {
		return (strand=region.strand && equalsIgnoreStrand(region));
	}
	@Transient
	public Boolean equalsIgnoreStrand(GenomicRegion region) {
		return (visibleStart==region.visibleStart && visibleEnd==region.visibleEnd && chromosome.equals(region.chromosome) && getSpecies()==region.getSpecies());
	}
	@Override
	public String toString() {
		return "Chr "+chromosome+":"+visibleStart+"-"+visibleEnd;
	}
}
