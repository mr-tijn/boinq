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

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.genohm.viewsGWT.shared.data.ensembl.EnsemblGene;

public class EnsemblGeneDAOTest {

	public static EnsemblGeneDAO dao;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("WEB-INF/spring-service.xml");
		dao = (EnsemblGeneDAO) context.getBean("ensemblGeneDAO");
	}

	@Test
	public void testInitializeIndexes() {
		dao.initializeIndexes("1");
	}

	@Test
	public void testFindGenesByFullTextSearch() throws Exception {
		List<EnsemblGene> result = dao.findGenesByFullTextSearch("DDX11L1"); //"helicase");
		List<String> ensemblIds = new LinkedList<String>();
		for (EnsemblGene gene: result) {
			ensemblIds.add(gene.getEnsemblId());
		}
		assertTrue(ensemblIds.contains("ENSG00000223972"));
		result = dao.findGenesByFullTextSearch("k*se");
	}

}
