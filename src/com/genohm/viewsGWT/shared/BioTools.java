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
import java.util.Map;

public class BioTools {
	
	public static Map<String, String> aminoAcid = new HashMap<String, String>() {
		{
			put("TCA","S"); put("TCC","S"); put("TCG","S"); put("TCT","S"); put("TTC","F"); put("TTT","F"); put("TTA","L"); put("TTG","L"); put("TAC","Y"); put("TAT","Y"); 
			put("TAA","_"); put("TAG","_"); put("TGC","C"); put("TGT","C"); put("TGA","_"); put("TGG","W"); put("CTA","L"); put("CTC","L"); put("CTG","L"); put("CTT","L"); 
			put("CCA","P"); put("CCC","P"); put("CCG","P"); put("CCT","P"); put("CAC","H"); put("CAT","H"); put("CAA","Q"); put("CAG","Q"); put("CGA","R"); put("CGC","R");
			put("CGG","R"); put("CGT","R"); put("ATA","I"); put("ATC","I"); put("ATT","I"); put("ATG","M"); put("ACA","T"); put("ACC","T"); put("ACG","T"); put("ACT","T");
			put("AAC","N"); put("AAT","N"); put("AAA","K"); put("AAG","K"); put("AGC","S"); put("AGT","S"); put("AGA","R"); put("AGG","R"); put("GTA","V"); put("GTC","V");
			put("GTG","V"); put("GTT","V"); put("GCA","A"); put("GCC","A"); put("GCG","A"); put("GCT","A"); put("GAC","D"); put("GAT","D"); put("GAA","E"); put("GAG","E");
			put("GGA","G"); put("GGC","G"); put("GGG","G"); put("GGT","G");
		}
	};
	
	public static String complement(String orig) {
		if (orig == null) return null;
		String complement = orig;
		complement = complement.replaceAll("[Aa]", "D");
		complement = complement.replaceAll("[Tt]", "A"); // [TtUu]
		complement = complement.replaceAll("D", "T");
		complement = complement.replaceAll("[Cc]", "H");
		complement = complement.replaceAll("[Gg]", "C");
		complement = complement.replaceAll("H", "G");
		return complement;
	}
	public static String reverseComplement(String dnaString) {
		return complement(reverse(dnaString));
	}
	public static String reverse(String orig) {
		// stringbuffer.reverse not working in client
		String result = "";
		for (int i = orig.length()-1; i >= 0; i--) {
			result += orig.charAt(i);
		}
		return result;
	}
	public static String translate(String orig) {
		StringBuffer protein = new StringBuffer();
		for (int i = 0; i <= orig.length()-3; i+=3) {
			protein.append(aminoAcid.get(orig.substring(i,i+3).toUpperCase()));
		}
		return protein.toString();
	}
}
