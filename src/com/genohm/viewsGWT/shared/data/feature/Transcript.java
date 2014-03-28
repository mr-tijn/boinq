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
package com.genohm.viewsGWT.shared.data.feature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import com.genohm.viewsGWT.shared.BioTools;
import com.genohm.viewsGWT.shared.Location;
import com.google.gwt.user.client.rpc.IsSerializable;

public class Transcript extends Feature implements IsSerializable {

	Long translationStart = null;
	Long translationEnd = null;
	
	protected String _mRNA = null;
	protected String _protein = null;
	protected Map<Exon,Integer> rnaStartIndex = null;
	protected Map<Exon,Integer> rnaEndIndex = null;
	
	public Long getTranslationStart() {
		return translationStart;
	}

	public void setTranslationStart(Long translationStart) {
		this.translationStart = translationStart;
	}

	public Long getTranslationEnd() {
		return translationEnd;
	}

	public void setTranslationEnd(Long translationEnd) {
		this.translationEnd = translationEnd;
	}

	@SuppressWarnings("unchecked")
	public List<Exon> getExons() {
		return (List<Exon>) subFeatures;
	}

	public void setExons(List<Exon> subFeatures) {
		this.subFeatures = subFeatures;
	}

	public Gene getGene() {
		return (Gene) getParent();
	}
	
	@Transient
	public String getmRNA() {
		if (_mRNA == null) {
			_mRNA = "";
			rnaStartIndex = new HashMap<Exon, Integer>();
			rnaEndIndex = new HashMap<Exon, Integer>();
			if (getGene().getDnaString() != null) {
				
				for (Exon exon: getExons()) {
					Location loc = exon.getLoc().get(0);
					Long startIndex, endIndex;
					if (loc.getStrand()) {
						if (loc.getEnd() >= getTranslationStart() && loc.getStart() <= getTranslationEnd()) {
							startIndex = Math.max(loc.getStart(), getTranslationStart()) - getGene().getStart();
							endIndex = Math.min(loc.getEnd(), getTranslationEnd()) - getGene().getStart();
							rnaStartIndex.put(exon, _mRNA.length()); 
							rnaEndIndex.put(exon, _mRNA.length() + (int) (endIndex - startIndex)); 
							_mRNA += getGene().getDnaString().substring(startIndex.intValue(), endIndex.intValue() + 1);
						} 
					} else {
						if (loc.getStart() <= getTranslationStart() && loc.getEnd() >= getTranslationEnd()) {
							startIndex = Math.max(loc.getStart(), getTranslationEnd()) - getGene().getStart();
							endIndex = Math.min(loc.getEnd(), getTranslationStart()) - getGene().getStart();
							rnaStartIndex.put(exon, _mRNA.length());
							rnaEndIndex.put(exon, _mRNA.length() + (int) (endIndex - startIndex));
							_mRNA += BioTools.reverseComplement(getGene().getDnaString().substring(startIndex.intValue(), endIndex.intValue() + 1));							
						}
					}
				}
			} 
		}
		return _mRNA;
	}
	
	@Transient
	public String getProtein(Exon exon) {
		if (_protein == null) {
			_protein = BioTools.translate(getmRNA());
		}
		if (rnaStartIndex.get(exon) == null) {
			return "";
		} else {
			Location loc = exon.getLoc().get(0);
			int start_idx = rnaStartIndex.get(exon) / 3;
			int end_idx = rnaEndIndex.get(exon) / 3;
			return _protein.substring(start_idx,end_idx+1);			
		}
	}
	
	@Transient
	public int getPhase(Exon exon) {
		if (rnaStartIndex == null) getmRNA();
		if (rnaStartIndex.get(exon) == null) return 0;
		else return rnaStartIndex.get(exon)%3;
	}
	
}
