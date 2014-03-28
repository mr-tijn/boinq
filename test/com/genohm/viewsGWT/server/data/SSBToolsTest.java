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

import org.junit.BeforeClass;
import org.junit.Test;

import com.genohm.viewsGWT.server.twinql.SSBTools;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

public class SSBToolsTest {

	private static SSBTools ssbTools;
	
	@BeforeClass
	public static void beforeClass() {
		ssbTools = new SSBTools();
		ssbTools.setAssemblerFile("/ontologies/so.ttl");
		ssbTools.setGoUri("http://www.semantic-systems-biology.org/go");
		ssbTools.setSoUri("http://www.semantic-systems-biology.org/so");
	}
	
	@Test
	public void testGetMainDataset() {
		assertNotNull(ssbTools.getMainDataset());
	}

	@Test
	public void testGetSequenceOntology() {
		Model so = ssbTools.getSequenceOntology();
		assertNotNull(so);
	}

	@Test
	public void testGetSOFA() {
		Model sofa = ssbTools.getSOFA();
		assertNotNull(sofa);
		sofa.write(System.out);
	}
	
	@Test
	public void testGetSofaByLabel() {
		Resource test = ssbTools.getSofaByLabel("exon");
		assertNotNull(test);
		test = ssbTools.getSofaByLabel("does_not_exist");
		assertNull(test);
	}
	
	@Test
	public void testGetSofaByURI() {
		Resource test = ssbTools.getSofaByURI(SSBTools.SSB_BASE+"SO_0000147");
		assertNotNull(test);
		test = ssbTools.getSofaByURI(SSBTools.SSB_BASE+"should_not_contain");
		assertNull(test);
	}

	
	@Test
	public void testGetSofaByID() {
	}

	@Test
	public void testGetGoByID() {
		Resource term = ssbTools.getGoByID("0051800");
		assertNotNull(term);
		System.out.println(term.getProperty(RDFS.label).getString());
	}

}
