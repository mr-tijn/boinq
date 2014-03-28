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
package com.genohm.viewsGWT.server;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.genohm.viewsGWT.client.ViewsServer;
import com.genohm.viewsGWT.server.analysis.AnalysisComputation;
import com.genohm.viewsGWT.server.analysis.ComputationEngine;
import com.genohm.viewsGWT.server.analysis.ROIComputation;
import com.genohm.viewsGWT.server.data.EnsemblGeneDAO;
import com.genohm.viewsGWT.server.data.ViewsDao;
import com.genohm.viewsGWT.server.external.SPARQLClient;
import com.genohm.viewsGWT.server.ontologyclient.TrackClient;
import com.genohm.viewsGWT.server.query.ARQGenerator;
import com.genohm.viewsGWT.shared.BrowserPerspective;
import com.genohm.viewsGWT.shared.RegionOfInterest;
import com.genohm.viewsGWT.shared.analysis.Analysis;
import com.genohm.viewsGWT.shared.data.FeatureDatasource;
import com.genohm.viewsGWT.shared.data.SPARQLResultSet;
import com.genohm.viewsGWT.shared.data.Term;
import com.genohm.viewsGWT.shared.data.TrackSpecification;
import com.genohm.viewsGWT.shared.data.ensembl.EnsemblGene;
import com.genohm.viewsGWT.shared.fieldconfig.FieldConfig;
import com.genohm.viewsGWT.shared.fieldconfig.Target;
import com.genohm.viewsGWT.shared.query.Match;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ViewsServerImpl extends RemoteServiceServlet implements ViewsServer {

	private static final String myBeanName = "viewsServer"; 
	private static final long serialVersionUID = 3581259561607848700L;
	private static final Logger log = Logger.getLogger(ViewsServerImpl.class);
	
	protected ViewsDao viewsDao;
	protected EnsemblGeneDAO ensemblGeneDao;
	protected SPARQLClient sparqlClient;
	protected TrackClient trackClient;
	protected ComputationEngine computationEngine;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		AutowireCapableBeanFactory beanFactory = ctx.getAutowireCapableBeanFactory();
		beanFactory.applyBeanPropertyValues(this, myBeanName);
	}
	
	@Override
	public String getCurrentUserName() throws Exception {
		return viewsDao.getCurrentUser().getUsername();
	}

	public void setViewsDao(ViewsDao viewsDao) {
		this.viewsDao = viewsDao;
	}
	public void setEnsemblGeneDao(EnsemblGeneDAO ensemblGeneDao) {
		this.ensemblGeneDao = ensemblGeneDao;
	}
	public void setSparqlClient(SPARQLClient sparqlClient) {
		this.sparqlClient = sparqlClient;
	}
	public void setTrackClient(TrackClient trackClient) {
		this.trackClient = trackClient;
	}
	public ComputationEngine getComputationEngine() {
		return computationEngine;
	}
	public void setComputationEngine(ComputationEngine computationEngine) {
		this.computationEngine = computationEngine;
	}

	@Override
	public List<FeatureDatasource> getFeatureDatasources() throws Exception {
		log.debug("Getting public featuredatasources");
		List<FeatureDatasource> result = viewsDao.getPublicDatasources();
		result.addAll(viewsDao.getDatasourcesByUser(getCurrentUserName()));
		return result;
	}

	@Override
	public void logout() throws Exception {
		log.debug("attempting logout");
		viewsDao.logout();
	}

	@Override
	public List<EnsemblGene> searchGenesFullText(String searchString) throws Exception {
		List<EnsemblGene> orig = ensemblGeneDao.findGenesByFullTextSearch(searchString);
		List<EnsemblGene> result = new LinkedList<EnsemblGene>();
		// get rid of non serializable hibernate implementations
		for (EnsemblGene raw: orig) {
			result.add(raw.deepCopy());
		}
		return result;
	}

	@Override
	public List<RegionOfInterest> getROIs() throws Exception {
		List<RegionOfInterest> orig = viewsDao.getRegionsOfInterestByUser(getCurrentUserName());
		List<RegionOfInterest> result = new LinkedList<RegionOfInterest>();
		// get rid of non serializable hibernate implementations
		for (RegionOfInterest raw: orig) {
			result.add(raw.deepCopy());
		}
		return result;
	}

	@Override
	public List<TrackSpecification> getTrackSpecs() throws Exception {
		List<TrackSpecification> orig = viewsDao.getTrackSpecsByUser(getCurrentUserName());
		return orig;
	}

	@Override
	public BrowserPerspective getDefaultPerspective() throws Exception {
		BrowserPerspective orig = viewsDao.getDefaultPerspective(getCurrentUserName());
		List<TrackSpecification> specs = new LinkedList<TrackSpecification>();
		for (TrackSpecification spec: orig.getTracks()) {
			specs.add(spec);
		}
		orig.setTracks(specs);
		return orig;
	}

	@Override
	public void saveDefaultPerspective(BrowserPerspective perspective) throws Exception {
		viewsDao.merge(perspective);
	}

	@Override
	public SPARQLResultSet sparqlQuery(String query) throws Exception {
		return sparqlClient.query(query);
	}

	@Override
	public List<FieldConfig> getFields(String featureTypeURI) throws Exception {
		return trackClient.getFields(featureTypeURI);
	}

	@Override
	public List<Term> getDatasources() throws Exception {
		return trackClient.getDatasources();
	}
	
	@Override
	public List<Term> getFeatureTypes(String datasourceURI) throws Exception {
		return trackClient.getFeatureTypes(datasourceURI);
	}

	@Override
	public String generateQuery(Match match) throws Exception {
		//SPARQLGenerator generator = new NaiveSPARQLGenerator(match);
		//return match.generate(generator, "feature");
		ARQGenerator generator = new ARQGenerator();
		return generator.generateQuery(match);
		
	}

	@Override
	public void saveROI(RegionOfInterest roi) throws Exception {
		if (roi.getName() == null) {
			Date now = new Date();
			roi.setName("Generated by " + getCurrentUserName() +" on "+now.toString());
		}
		roi.setOwner(getCurrentUserName());
		viewsDao.saveOrUpdate(roi);
		ROIComputation comp = new ROIComputation(roi);
		computationEngine.add(comp);
	}
	
	@Override
	public List<Term> getProperties(String entityIRI) throws Exception {
		return trackClient.getProperties(entityIRI);
	}

	@Override
	public List<Target> getTargetTypes(String entityIRI, String propertyIRI) throws Exception {
		return trackClient.getTargets(entityIRI, propertyIRI);
	}

	@Override
	public List<Analysis> getAnalyses() throws Exception {
		List<Analysis> orig = viewsDao.getAnalyses();
		List<Analysis> result = new LinkedList<Analysis>();
		for (Analysis raw: orig) {
			result.add(raw.deepcopy());
		}
		return result;
	}

	@Override
	public void removeRegionFromRoi(Long regionId, Long roiId) {
		viewsDao.removerRegionFromRoi(regionId, roiId);
		
	}

	@Override
	public void removeRoi(Long roiId) {
		viewsDao.removeRegionOfInterest(roiId);
	}

	@Override
	public void saveAnalysis(Analysis analysis) throws Exception {
		if (analysis.getName() == null) {
			Date now = new Date();
			analysis.setName("Generated by " + getCurrentUserName() +" on "+now.toString());
		}
		analysis.setOwner(getCurrentUserName());
		viewsDao.saveOrUpdate(analysis);
		AnalysisComputation comp = new AnalysisComputation(analysis);
		computationEngine.add(comp);
	}

}
