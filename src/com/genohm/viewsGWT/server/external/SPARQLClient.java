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
package com.genohm.viewsGWT.server.external;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.genohm.viewsGWT.shared.data.RawSPARQLResultSet;
import com.genohm.viewsGWT.shared.data.SPARQLResultSet;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class SPARQLClient {
	
	private static final Logger log = Logger.getLogger(SPARQLClient.class);
	protected String serviceURL;
	
	public SPARQLClient(String serviceURL) {
		setServiceURL(serviceURL);
	}
	
	public SPARQLResultSet query(Query query) throws Exception {
		return query(query.toString(Syntax.syntaxSPARQL));
	}
	
	public RawSPARQLResultSet rawQuery(String queryString, Boolean subClassReasoning, Boolean subPropertyReasoning) throws Exception {
		QueryExecution qe = null;
		List<Map<String, RDFNode>> resultList = null;
		List<String> varList = null;
		
/////		
//		public ResultSet executeQuery(String queryString,String rules) throws Exception {
//			 Query query = QueryFactory.create(queryString) ;
//
//			 QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest(this.service, query);
//			 qexec.addParam("apikey", this.apikey);
//			 if (rules != null)
//				 qexec.addParam("rules", rules);
//			 ResultSet results = qexec.execSelect() ;
//			 return results;
//
//		}
	/////	
		String rules = "";
		if (subClassReasoning) rules += "SUBC+";
		if (subPropertyReasoning) rules += "SUBP";
		if (rules.endsWith("+")) rules = rules.substring(0, rules.length()-1);
		
		try {
			
//			Query query = QueryFactory.create(queryString);
//			qe = QueryExecutionFactory.createServiceRequest(serviceURL, query);
			qe = new QueryEngineHTTP(serviceURL, queryString);
			if (rules.length() > 0) {
				((QueryEngineHTTP) qe).addParam("rules", rules);
			}
			ResultSet rs = qe.execSelect();
			resultList = new LinkedList<Map<String,RDFNode>>();
			varList = rs.getResultVars();
			while (rs.hasNext()) {
				Map<String,RDFNode> result = new HashMap<String, RDFNode>();
				QuerySolution qs = rs.nextSolution();
				for (String var: varList) {
					result.put(var, qs.get(var));
				}
				resultList.add(result);
			}
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			e.printStackTrace(new PrintStream(baos));
			String error = "Could not perform query "+queryString+"\n"+e.getMessage()+"\n"+baos.toString();
			log.error(error);
			throw new Exception((Throwable) e); //recast to general type to ensure serialization
		} finally {
			if (qe != null) qe.close();
		}
		RawSPARQLResultSet srs = new RawSPARQLResultSet();
		srs.setRecords(resultList);
		srs.setVariableNames(varList);
		return srs;		
	}
	
	public SPARQLResultSet query(String queryString, Boolean subClassReasoning, Boolean subPropertyReasoning) throws Exception {
		RawSPARQLResultSet rrs = rawQuery(queryString, subClassReasoning, subPropertyReasoning);
		List<String> varList = rrs.getVariableNames();
		List<Map<String, String>> resultList = new LinkedList<Map<String,String>>();
		SPARQLResultSet rs = new SPARQLResultSet();
		rs.setVariableNames(rrs.getVariableNames());
		for (Map<String,RDFNode> record: rrs.getRecords()) {
			Map<String,String> result = new HashMap<String, String>();
			for (String var: varList) {
				if (record.get(var) != null) {
					if (record.get(var).isLiteral()) result.put(var,record.get(var).asLiteral().getValue().toString());
					else result.put(var, record.get(var).toString());
				}
			}
			resultList.add(result);
		}
		rs.setRecords(resultList);
		return rs;
	}
	
	public RawSPARQLResultSet rawQuery(Query query) throws Exception {
		return rawQuery(query.toString(Syntax.syntaxSPARQL), false, false);
	}

	public RawSPARQLResultSet rawQuery(String query) throws Exception {
		return rawQuery(query, false, false);
	}
	
	public SPARQLResultSet query(String query) throws Exception {
		return query(query, false, false);
	}
	
	public List<Map<String,RDFNode>> queryForListOfNodeMaps(String query) throws Exception {
		QueryExecution qe = null;
		List<Map<String, RDFNode>> resultList = null;
		try {
			qe = QueryExecutionFactory.sparqlService(serviceURL, query);
			ResultSet rs = qe.execSelect();
			resultList = new LinkedList<Map<String,RDFNode>>();
			List<String> varList = rs.getResultVars();
			while (rs.hasNext()) {
				Map<String,RDFNode> result = new HashMap<String, RDFNode>();
				QuerySolution qs = rs.nextSolution();
				for (String var: varList) {
					if (qs.get(var) != null) result.put(var, qs.get(var));
				}
				resultList.add(result);
			}
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			e.printStackTrace(new PrintStream(baos));
			String error = "Could not perform query "+query+"\n"+e.getMessage()+"\n"+baos.toString();
			log.error(error);
			throw new Exception((Throwable) e); //recast to general type to ensure serialization
		} finally {
			if (qe != null) qe.close();
		}
		return resultList;		
	}

	public String getServiceURL() {
		return serviceURL;
	}

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}
}
