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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.broad.igv.bbfile.BBFileHeader;
import org.broad.igv.bbfile.BBFileReader;
import org.broad.igv.bbfile.BedFeature;
import org.broad.igv.bbfile.BigBedIterator;
import org.broad.igv.bbfile.BigWigIterator;
import org.broad.igv.bbfile.WigItem;

import com.affymetrix.genometryImpl.AnnotatedSeqGroup;
import com.affymetrix.genometryImpl.parsers.graph.WiggleParser;
import com.affymetrix.genometryImpl.symmetry.GraphSym;
import com.genohm.viewsGWT.shared.Location;
import com.genohm.viewsGWT.shared.data.feature.BigFeature;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.genohm.viewsGWT.shared.data.feature.GraphFeature;

// TODO: see what's useable and kick the rest out

public class GraphEngine {
	GraphFeature graph =new GraphFeature();
	BigFeature big =new BigFeature();

	public List<Feature> getWigFeatures (File fiLe) throws Exception { 

		File file = new File("C:\\Users\\Alex\\workspace\\ViewsGWT\\war\\file5.wig");
		DataInputStream stream = new DataInputStream(new FileInputStream(file));
		WiggleParser parser= new WiggleParser();
		List<GraphSym> list;
		list= parser.parse (stream,new AnnotatedSeqGroup("") ,false, "file5.wig");
		List<Feature> featureList = new LinkedList<Feature>();
		GraphFeature feature = null;
		for (int i=0; i<list.size(); i++){
			GraphSym currentFeature =list.get(i);
			feature = new GraphFeature(currentFeature.getGraphSeq().getID(),null,0.0,currentFeature.getGraphSeq().getID(),
					null,null, null,null,currentFeature.getGraphXCoords(),graph.getMinimalData(currentFeature.getGraphYCoords()),
					graph.getMaximalData(currentFeature.getGraphYCoords()),currentFeature.getGraphWidthCoord(i));
			List<Location> locations = feature.getLoc();
			locations.add(new Location((long)currentFeature.getMinXCoord(),(long)currentFeature.getMaxXCoord(),
					currentFeature.getGraphSeq().getID(),null));
			feature.setData(currentFeature.copyGraphYCoords());				
			featureList.add(feature);
		}
		return featureList;
	}



	public List<Feature> getBigFeatures (File fiLe,String chrom,long start,long end) throws Exception {

		boolean contained=true;
		String chr = "chr7";
		//		long start = 250000;
		//		long end=300000;
		List<Feature> featureList = new LinkedList<Feature>();
		BBFileReader reader=new BBFileReader("C:\\Users\\Alex\\workspace\\ViewsGWT\\war\\filee.bigWig");
		BBFileHeader bbFileHdr = reader.getBBFileHeader();

		if (!bbFileHdr.isHeaderOK()){
			throw new IOException("bad header for ");
		}
		if(!(bbFileHdr.isBigBed() || bbFileHdr.isBigWig()))
		{
			throw new IOException("undefined big type for ");
		}
		if(bbFileHdr.isBigBed())
		{
			BigBedIterator iter;
			if(chrom!=null)
			{
				iter=reader.getBigBedIterator(chr,(int)start,chr,(int)end,contained);
			}
			else
			{
				iter=reader.getBigBedIterator();
			}
			while(iter.hasNext())
			{
				BedFeature f=iter.next();
				Feature feature = new Feature(f.getRestOfFields()[0],f.getRestOfFields()[0],(double)(Integer.decode(f.getRestOfFields()[1])),f.getRestOfFields()[1],null,null,null);
				boolean positive=true;
				if(f.getRestOfFields()[2].equals("-")){
					positive=false;	
				}
				else {
					positive =true;
				}
				feature.getLoc().add(new Location ((long)f.getStartBase(),(long)f.getEndBase(),f.getChromosome(),positive));
 				featureList.add(feature);
			}
			for (int i =0; i<featureList.size(); i++){
				System.err.println(featureList.get(i).getStart()+" "+featureList.get(i).getEnd());
			}
		}		else if(bbFileHdr.isBigWig())
		{
			BigWigIterator iter;
			if(chrom!=null){

				iter=reader.getBigWigIterator(chrom,(int)start,chrom,(int)end,contained);

			}
			else
			{
				iter=reader.getBigWigIterator();
			}
			GraphFeature feature = new GraphFeature(null,null,0.0,null,null,null, null,
					null,null,null,null,0);
			List<Float> dataL = new LinkedList<Float>();
			List<Integer> positionL = new LinkedList<Integer>();
			boolean first = true;
			int previous = 0;
			while(iter.hasNext())
			{
				WigItem f=iter.next();
				feature.setId(f.getChromosome());
				feature.getLoc().add(new Location ((long)f.getStartBase(),(long)f.getEndBase(),f.getChromosome(),true));
				dataL.add(f.getWigValue());
				positionL.add(f.getStartBase());
				if (!first){
					feature.setSpan(f.getStartBase()-previous);
				}
				previous = f.getStartBase();
				first =false;
			} 
			float[] arrayData = new float[dataL.size()];
			for (int i = 0; i < dataL.size(); i++) {
				arrayData[i] = dataL.get(i); 
			}
			feature.setData(arrayData);
			int[] array = new int[positionL.size()];
			for (int i = 0; i < positionL.size(); i++) {
				array[i] = positionL.get(i);
			}
			feature.setPosition(array);
			feature.setMaxData(graph.getMaximalData(feature.getData()));
			featureList.add(feature);
		}
		return featureList;
	}


}



