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

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.vocabulary.RDF;

public class TripleUploadServiceTest {

	private TripleUploadservice service;
	@Before
	public void testTripleUploadservice() {
		service = new TripleUploadservice("http://localhost:3030/META_DYNAMIC/update");
	}
	@Test
	public void testPut() {
		Triple test = new Triple(Node.createURI("http://feature"), RDF.type.asNode(), Node.createURI("http://dikkevetten"));
		Node graph = Node.createURI("http://testgraph");
		service.put(graph, test);
	}

}
