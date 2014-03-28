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

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.modify.request.QuadDataAcc;
import com.hp.hpl.jena.sparql.modify.request.UpdateDataInsert;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

public class TripleUploadservice {
	
	//set through spring
	private String sparqlEndpointURI;
	public TripleUploadservice(String sparqlEndpointURI) {
		super();
		this.sparqlEndpointURI = sparqlEndpointURI;
	}
	
	public void put(Node graphNode, Triple newTriple) {
		QuadDataAcc newData = new QuadDataAcc();
		newData.setGraph(graphNode);
		newData.addTriple(newTriple);
		UpdateDataInsert insertStatement = new UpdateDataInsert(newData);
		UpdateRequest req = new UpdateRequest(insertStatement);
		UpdateProcessor processor = UpdateExecutionFactory.createRemote(req, sparqlEndpointURI);
		processor.execute();
	}

}
