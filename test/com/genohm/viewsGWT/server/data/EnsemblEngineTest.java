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

import static org.junit.Assert.*;

import java.beans.XMLDecoder;
import java.net.URLDecoder;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.genohm.viewsGWT.server.twinql.SSBTools;
import com.genohm.viewsGWT.shared.data.feature.Gene;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

public class EnsemblEngineTest {

	private static SSBTools ssbTools;
	private static EnsemblEngine ensemblEngine;
	
	@BeforeClass
	public static void setUp() throws Exception {
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("WEB-INF/spring-service.xml");
		ensemblEngine = (EnsemblEngine) context.getBean("ensemblEngineHuman");
		ssbTools = (SSBTools) context.getBean("ssbTools");
	}

	public void testGetGenesByRegion() {
		fail("Not yet implemented");
	}

	public void testGetTranscripts() {
		fail("Not yet implemented");
	}

	public void testGetGeneData() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetGeneById() throws Exception {
		Gene pten = ensemblEngine.getGeneById("ENSG00000171862", 0);
		assertEquals(pten.getName(),"PTEN");
	}
	
	@Test
	public void testGetGOTermsByTranscriptID() throws Exception {
		List<String> goTerms = ensemblEngine.getGOTermsByTranscriptID("ENST00000371953");
		assertEquals(goTerms.size(),70);
		for (String term: goTerms) {
			System.out.print(term);
			assertTrue(term.startsWith("GO:"));
			Resource goRes = ssbTools.getGoByID(term.substring(3));
			System.out.println("\t" + StringEscapeUtils.unescapeXml(goRes.getProperty(RDFS.label).getString()));
		}
	}

}
