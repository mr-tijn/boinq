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
package com.genohm.viewsGWT.shared.data.ensembl;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.WhereJoinTable;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import com.google.gwt.user.client.rpc.IsSerializable;

@Indexed
@Entity @Table(name="gene")
public class EnsemblGene implements IsSerializable {

	private Integer id;
	private String ensemblId;
	private Long startPos;
	private Long endPos;
	private EnsemblSeqRegion chromosome;
	private String description;
	private Set<EnsemblXref> ensemblXrefs;
	private EnsemblXref displayXref;
	
	@DocumentId
	@Id
	@Column(name="gene_id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	//@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
	@Field(index=Index.TOKENIZED, store=Store.NO)
	@Column(name="description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@IndexedEmbedded
	@ManyToOne()
	@JoinColumn(name="display_xref_id")
	public EnsemblXref getDisplayXref() {
		return displayXref;
	}
	public void setDisplayXref(EnsemblXref displayXref) {
		this.displayXref = displayXref;
	}
	
	//@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
	@Field(index=Index.TOKENIZED, store=Store.NO)
	@Column(name="stable_id")
	public String getEnsemblId() {
		return ensemblId;
	}
	public void setEnsemblId(String ensemblId) {
		this.ensemblId = ensemblId;
	}
	
	@Column(name="seq_region_start")
	public Long getStartPos() {
		return startPos;
	}
	public void setStartPos(Long startPos) {
		this.startPos = startPos;
	}
	
	@Column(name="seq_region_end")
	public Long getEndPos() {
		return endPos;
	}
	public void setEndPos(Long endPos) {
		this.endPos = endPos;
	}
	
	@ManyToOne()
	@JoinColumn(name="seq_region_id")
	public EnsemblSeqRegion getChromosome() {
		return chromosome;
	}
	public void setChromosome(EnsemblSeqRegion chromosome) {
		this.chromosome = chromosome;
	}
	
	@IndexedEmbedded
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name="object_xref", joinColumns = @JoinColumn(name="ensembl_id"), inverseJoinColumns= @JoinColumn(name="xref_id"))
	@WhereJoinTable(clause="ensembl_object_type = 'Gene'")
	public Set<EnsemblXref> getEnsemblXrefs() {
		return ensemblXrefs;
	}
	public void setEnsemblXrefs(Set<EnsemblXref> ensemblXrefs) {
		this.ensemblXrefs = ensemblXrefs;
	}
	
	public EnsemblGene deepCopy() {
		// returns a copy without hibernate implementations (notably of Set)
		EnsemblGene copy = new EnsemblGene();
		copy.setDescription(getDescription());
		copy.setEnsemblId(getEnsemblId());
		copy.setDisplayXref(getDisplayXref());
		copy.setStartPos(getStartPos());
		copy.setEndPos(getEndPos());
		copy.setChromosome(getChromosome());
		Set<EnsemblXref> xrefs = new HashSet<EnsemblXref>();
		xrefs.addAll(getEnsemblXrefs());
		copy.setEnsemblXrefs(xrefs);
		return copy;
	}
}
