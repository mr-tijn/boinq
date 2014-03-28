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
package com.genohm.viewsGWT.server.twinql;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

// GFFIterator will use FALDO + SO

public class GFFVocab {
	public static final String PREFIX = "http://www.sequence-ontology.org/GFF#";
	
	public static final Resource GFFDataSourceResource = ResourceFactory.createResource(PREFIX + "GFFDataSource");
	
	public static final Property GFFSODefinitionFile = ResourceFactory.createProperty(PREFIX, "soDefinitionFile");
	public static final Property GFFSOGraphName = ResourceFactory.createProperty(PREFIX, "soGraphName");
	public static final Property GFFFilename = ResourceFactory.createProperty(PREFIX, "fileName");
	public static final Property id = ResourceFactory.createProperty(PREFIX, "id");
	public static final Property seqid =  ResourceFactory.createProperty(PREFIX, "seqid");
	public static final Property source =  ResourceFactory.createProperty(PREFIX, "source");
	public static final Property start = ResourceFactory.createProperty(PREFIX, "start");
	public static final Property end = ResourceFactory.createProperty(PREFIX, "end");
	public static final Property score = ResourceFactory.createProperty(PREFIX, "score");
	public static final Property strand = ResourceFactory.createProperty(PREFIX, "strand");
	public static final Property phase = ResourceFactory.createProperty(PREFIX, "phase");
	public static final Property assembly = ResourceFactory.createProperty(PREFIX, "assembly");
}
