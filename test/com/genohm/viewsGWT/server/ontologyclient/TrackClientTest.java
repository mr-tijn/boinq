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
package com.genohm.viewsGWT.server.ontologyclient;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.genohm.viewsGWT.server.data.SPARQLEngine;
import com.genohm.viewsGWT.server.external.SPARQLClient;
import com.genohm.viewsGWT.shared.data.Term;
import com.genohm.viewsGWT.shared.fieldconfig.FieldConfig;
import com.genohm.viewsGWT.shared.fieldconfig.FieldType;

public class TrackClientTest {

	protected TrackClient trackClient = new TrackClient();
	
	@Before
	public void setUp() throws Exception {
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("WEB-INF/spring-service.xml");
		trackClient.setSparqlClient((SPARQLClient) context.getBean("localSparqlClient"));
	}

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void test() throws Exception {
		List<Term> ds = trackClient.getDatasources();
		for (Term dsTerm: ds) {
			System.out.println("DS: "+(dsTerm.getName()==null?dsTerm.getIri():dsTerm.getName()));
			String dsIRI = dsTerm.getIri();
			List<Term> ft = trackClient.getFeatureTypes(dsIRI);
			for (Term ftTerm: ft) {
				System.out.println("\tFT: "+(ftTerm.getName()==null?ftTerm.getIri():ftTerm.getName()));
				String ftIRI = ftTerm.getIri();
				List<FieldConfig> fc = trackClient.getFields(ftIRI);
				for (FieldConfig config: fc) {
					System.out.println("\t\tFIELD: "+(config.getName()==null?config.getIRI():config.getName()));
					if (FieldType.INTERNAL_TERM_TYPE == config.getType()) {
						for (FieldConfig subConfig: config.getFields()) {
							System.out.println("\t\t\tFIELD: "+(subConfig.getName()==null?subConfig.getIRI():subConfig.getName()));
						}
					}
				}
			}
			
		}
	}

}
