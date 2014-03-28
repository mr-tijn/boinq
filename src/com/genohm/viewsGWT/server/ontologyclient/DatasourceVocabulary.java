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

public class DatasourceVocabulary {

	public static String baseURI = "http://www.boinq.org/datasource#";
	public static String ensemblURI = "http://www.boinq.org/homo_sapiens_core_71_37_simple#";
	public static String faldoURI = "http://biohackathon.org/resource/faldo#";
	public static String propertyHolderBaseURI = "http://www.boinq.org/PropertyHolder#";;
	public static Node datasourceGraph = Node.createURI("http://www.boinq.org/datasource");
	public static Node provides = Node.createURI(baseURI+"provides");
	public static Node datasource = Node.createURI(baseURI+"datasource");
	public static Node externalTerm = Node.createURI(baseURI+"externalTerm");
	public static Node hasGraph = Node.createURI(baseURI+"hasGraph");
	public static Node hasEndpoint = Node.createURI(baseURI+"hasEndpoint");
	public static Node targetGraph = Node.createURI(baseURI+"targetGraph");
	public static Node targetEndpoint = Node.createURI(baseURI+"targetEndpoint");
	
	//public static Node targetFilter = Node.createURI(baseURI + "targetFilter");
	//public static Node motherTerm = Node.createURI(baseURI+"motherTerm");

	
}
