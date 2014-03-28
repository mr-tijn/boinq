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
package com.genohm.viewsGWT.tools.initializer;

import java.util.LinkedList;
import java.util.List;

import org.springframework.context.support.FileSystemXmlApplicationContext;


import com.genohm.viewsGWT.server.data.ViewsDao;
import com.genohm.viewsGWT.shared.BrowserPerspective;
import com.genohm.viewsGWT.shared.data.BBxAssemblyDatasource;
import com.genohm.viewsGWT.shared.data.GFFDatasource;
import com.genohm.viewsGWT.shared.data.GeneDatasource;
import com.genohm.viewsGWT.shared.data.RefSeqDatasource;
import com.genohm.viewsGWT.shared.data.TrackSpecification;
import com.genohm.viewsGWT.shared.renderer.RendererSettings;

public class InitialTrackSpecifications {
	
	
	protected static ViewsDao viewsDao;
	
	public static void main(String[] args) {
		try {
			FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("WEB-INF/spring-service.xml");
			viewsDao = (ViewsDao) context.getBean("viewsDao");
			InitialTrackSpecifications spec = new InitialTrackSpecifications();
			spec.createTrackspecs();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void createTrackspecs() {
		
		List<TrackSpecification> specs = new LinkedList<TrackSpecification>();
		
		RefSeqDatasource rsds = new RefSeqDatasource();
		rsds.setAssemblyName("GRCh37");
		rsds.setCanBeFiltered(false);
		rsds.setChromosomePrefix(null);
		rsds.setName("GRCh37 reference assembly");
		rsds.setDescription("The GRCh37 reference assembly");
		rsds.setIsPublic(true);
		rsds.setOwner("admin");
		viewsDao.saveOrUpdate(rsds);
		
		RendererSettings sequenceSettings = new RendererSettings();
		sequenceSettings.setRendererName("mappedread");
		sequenceSettings.setFeatureHeight(50);
		viewsDao.saveOrUpdate(sequenceSettings);
		
		TrackSpecification sequenceTrackSpec = new TrackSpecification();
		sequenceTrackSpec.setFeatureDatasource(rsds);
		sequenceTrackSpec.setIsPublic(true);
		sequenceTrackSpec.setTitle("Reference assembly");
		sequenceTrackSpec.setRendererSettings(sequenceSettings);		
		viewsDao.saveOrUpdate(sequenceTrackSpec);
		specs.add(sequenceTrackSpec);
		
		GeneDatasource gds = new GeneDatasource();
		gds.setCanBeFiltered(false);
		gds.setChromosomePrefix(null);
		gds.setName("Ensembl gene data");
		gds.setDescription("Gene info from ENSEMBL");
		gds.setIsPublic(true);
		gds.setOwner("admin");
		viewsDao.saveOrUpdate(gds);
	
		RendererSettings geneSettings = new RendererSettings();
		geneSettings.setRendererName("gene");
		geneSettings.setFeatureHeight(10);
		geneSettings.setGapWidth(2);
		viewsDao.saveOrUpdate(geneSettings);
		
		TrackSpecification geneTrackSpec = new TrackSpecification();
		geneTrackSpec.setFeatureDatasource(gds);
		geneTrackSpec.setIsPublic(true);
		geneTrackSpec.setTitle("Gene track");
		geneTrackSpec.setRendererSettings(geneSettings);
		viewsDao.saveOrUpdate(geneTrackSpec);
		specs.add(geneTrackSpec);
		
		RendererSettings transcriptSettings = new RendererSettings();
		transcriptSettings.setRendererName("transcript");
		viewsDao.saveOrUpdate(transcriptSettings);
		
		TrackSpecification transcriptSpec = new TrackSpecification();
		transcriptSpec.setIsPublic(true);
		transcriptSpec.setFeatureDatasource(null);
		transcriptSpec.setTitle("transcripts");
		transcriptSpec.setRendererSettings(transcriptSettings);
		viewsDao.saveOrUpdate(transcriptSpec);
		specs.add(transcriptSpec);

		GFFDatasource gfds = new GFFDatasource();
		gfds.setCanBeFiltered(false);
		gfds.setChromosomePrefix("chr");
		gfds.setName("Unfiltered GFF file");
		//TODO: parametrize base path
		gfds.setFilePath("/ontologies/transcripts.gff3");
		gfds.setDescription("Full gff3 dataset without filtering");
		gfds.setIsPublic(true);
		gfds.setOwner("admin");
		viewsDao.saveOrUpdate(gfds);
		
		RendererSettings gffSettings = new RendererSettings();
		gffSettings.setRendererName("block");
		viewsDao.saveOrUpdate(gffSettings);
		
		TrackSpecification gffTrackSpec = new TrackSpecification();
		gffTrackSpec.setFeatureDatasource(gfds);
		gffTrackSpec.setIsPublic(true);
		gffTrackSpec.setTitle("All gff data");
		gffTrackSpec.setRendererSettings(gffSettings);
		viewsDao.saveOrUpdate(gffTrackSpec);
		
		BBxAssemblyDatasource peds = new BBxAssemblyDatasource();
		peds.setCanBeFiltered(false);
		peds.setSampleId("1716");
		peds.setOwner("admin");
		peds.setIsPublic(false);
		viewsDao.saveOrUpdate(peds);		
		
		RendererSettings pairedEndSettings = new RendererSettings();
		pairedEndSettings.setRendererName("pairedend");
		pairedEndSettings.setFeatureHeight(10);
		pairedEndSettings.setGapWidth(2);
		viewsDao.saveOrUpdate(pairedEndSettings);
		
		TrackSpecification pairedEndTrackSpec = new TrackSpecification();
		pairedEndTrackSpec.setIsPublic(false);
		pairedEndTrackSpec.setOwner("admin");
		pairedEndTrackSpec.setFeatureDatasource(peds);
		pairedEndTrackSpec.setTitle("sample paired end track");
		pairedEndTrackSpec.setRendererSettings(pairedEndSettings);
		viewsDao.saveOrUpdate(pairedEndTrackSpec);
				
		BrowserPerspective systemDefaultPerspective = new BrowserPerspective();
		systemDefaultPerspective.setOwner("system");
		systemDefaultPerspective.setIsDefault(true);
		systemDefaultPerspective.setIsPublic(true);
		systemDefaultPerspective.setName("default");
		systemDefaultPerspective.setTracks(specs);
		viewsDao.saveOrUpdate(systemDefaultPerspective);
		
	}
	
	
}
