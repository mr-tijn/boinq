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

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.genohm.viewsGWT.shared.Location;
import com.genohm.viewsGWT.shared.data.feature.Feature;

import edu.unc.genomics.BedEntry;
import edu.unc.genomics.io.BedFile;

public class BEDEngine {
	
	// TODO: see what's useable and kick the rest out
	
	public List<Feature> getBedFeatures (File fiLe) throws Exception {
		//		String chr = "chr1";
		//		int start = 4127;
		//		int stop = 4355;
		File file = new File("C:\\Users\\Alex\\workspace\\ViewsGWT\\war\\bedsmall.txt");
		java.nio.file.Path p =file.toPath();
		BedFile f = new BedFile(p);
		Iterator<BedEntry> iter = f.iterator();
		List <Feature> featureList = new LinkedList<Feature>(); 
		while (iter.hasNext()){
			BedEntry current = iter.next();
			boolean positive = true;
			if (current.strand().equals("-")){
				positive = false;
			}
			else {
				positive = true;
			}
			Feature feature = new Feature (current.getId(),current.getChr(),0.0,current.getId(),null,null,null);
			feature.getLoc().add(new Location((long)current.getStart(),(long)current.getStop(),current.getChr(),positive));
			featureList.add(feature);
			System.err.println(feature.getStart()+" "+feature.getEnd());		

		}
		for (int i=0; i<featureList.size();i++){
			System.err.println(featureList.get(i).getStart()+" "+featureList.get(i).getEnd());		}
		return featureList;
	}
}
