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
package com.genohm.viewsGWT.tools;

import java.io.StringWriter;

import com.genohm.viewsGWT.server.ontologyclient.DatasourceVocabulary;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class D2RQtoMetadataMapper {
	protected static String d2rqBase = "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#";
	protected static String propertyHolderBase = "http://www.boinq.org/PropertyHolder#";
	
	protected Resource getObjectOfProperty(Model model, Resource subject, Property property) throws Exception {
		NodeIterator objects = model.listObjectsOfProperty(subject, property);
		RDFNode object = null;
		if (objects.hasNext()) {
			object = objects.next();
		}
		if (objects.hasNext()) {
			throw new Exception("Multiple objects of "+subject.getLocalName()+" on property "+property.getLocalName());
		}
		return (object != null ? object.asResource() : null);
	}
	
	
	protected OntModel createModel(String modelName, Model d2rqModel) throws Exception {
		OntModel owl = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
//		Resource theDS = owl.createResource(DatasourceVocabulary.baseURI+modelName);
//		Resource dsType = owl.createResource(DatasourceVocabulary.datasource.getURI());
//		owl.add(owl.createStatement(theDS, RDF.type, dsType));
		Resource classMapType = d2rqModel.getResource(d2rqBase+"ClassMap");
		ResIterator classMaps = d2rqModel.listResourcesWithProperty(RDF.type, classMapType);
//		Property provides = owl.getProperty(DatasourceVocabulary.baseURI+"provides");
		while (classMaps.hasNext()) {
			Resource classMap = classMaps.next();
			Property d2rqClass = d2rqModel.getProperty(d2rqBase+"class");
			Resource sourceClass = getObjectOfProperty(d2rqModel, classMap, d2rqClass);
			if (sourceClass == null) continue;
//			owl.createResource(sourceClass.asResource());
//			Statement newStatement = owl.createStatement(theDS, provides, sourceClass);
//			owl.add(newStatement);
			Property belongsTo = d2rqModel.getProperty(d2rqBase+"belongsToClassMap");
			ResIterator propertyBridges = d2rqModel.listResourcesWithProperty(belongsTo, classMap);
			while (propertyBridges.hasNext()) {
				Resource propertyBridge = propertyBridges.next();
				Property d2rqProperty = d2rqModel.getProperty(d2rqBase+"property");
				Resource property = getObjectOfProperty(d2rqModel, propertyBridge, d2rqProperty);
				Resource havingProperty = d2rqModel.createProperty(propertyHolderBase + property.getLocalName());
				Statement 	newStatement = owl.createStatement(sourceClass, RDFS.subClassOf, havingProperty);
				owl.add(newStatement);
				newStatement = owl.createStatement(property,RDF.type, RDF.Property);
				owl.add(newStatement);
				newStatement = owl.createStatement(property, RDFS.domain, havingProperty);
				owl.add(newStatement);
				Property d2rqDatatype = d2rqModel.getProperty(d2rqBase+"datatype");
				Resource datatype = getObjectOfProperty(d2rqModel, propertyBridge, d2rqDatatype);
				if (datatype != null) {
					newStatement = owl.createStatement(property, RDFS.range, datatype);
					owl.add(newStatement);
				}
				Property d2rqTargetclass = d2rqModel.getProperty(d2rqBase+"refersToClassMap");
				Resource targetClassMap = getObjectOfProperty(d2rqModel, propertyBridge, d2rqTargetclass);
				if (targetClassMap != null) {
					Resource targetClass = getObjectOfProperty(d2rqModel, targetClassMap, d2rqClass);
					newStatement = owl.createStatement(property, RDFS.range, targetClass);
					owl.add(newStatement);
				}
			}
		}
		return owl;
	}

	public String convert(String fileName, String modelName) {
		Model d2rqModel = FileManager.get().loadModel(fileName);
		String model = null;
		try {
			OntModel result = createModel(modelName, d2rqModel);
			RDFWriter writer = result.getWriter("TTL");
			StringWriter swriter = new StringWriter();
			result.write(swriter);
			model = swriter.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}
}
