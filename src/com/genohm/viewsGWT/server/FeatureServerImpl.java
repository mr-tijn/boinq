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

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.genohm.viewsGWT.client.FeatureServer;
import com.genohm.viewsGWT.server.data.AssemblyEngine;
import com.genohm.viewsGWT.server.data.BEDEngine;
import com.genohm.viewsGWT.server.data.BiobixEngine;
import com.genohm.viewsGWT.server.data.EnsemblEngine;
import com.genohm.viewsGWT.server.data.GFFEngine;
import com.genohm.viewsGWT.server.data.GraphEngine;
import com.genohm.viewsGWT.server.data.SPARQLEngine;
import com.genohm.viewsGWT.shared.ArgumentMap;
import com.genohm.viewsGWT.shared.Chromosome;
import com.genohm.viewsGWT.shared.ChromosomeDetail;
import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.Location;
import com.genohm.viewsGWT.shared.Species;
import com.genohm.viewsGWT.shared.ZoomLevel;
import com.genohm.viewsGWT.shared.data.BBxAssemblyDatasource;
import com.genohm.viewsGWT.shared.data.FeatureDatasource;
import com.genohm.viewsGWT.shared.data.GFFDatasource;
import com.genohm.viewsGWT.shared.data.GeneDatasource;
import com.genohm.viewsGWT.shared.data.RefSeqDatasource;
import com.genohm.viewsGWT.shared.data.feature.Feature;
import com.genohm.viewsGWT.shared.data.feature.Gene;
import com.genohm.viewsGWT.shared.data.feature.MappedReadFeature;
import com.genohm.viewsGWT.shared.renderer.RendererSettings;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FeatureServerImpl extends RemoteServiceServlet implements FeatureServer {
	/**
	 * 
	 */
	private static final String myBeanName = "featureServer"; 
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(FeatureServerImpl.class);

	protected AssemblyEngine assemblyEngineHuman;
	protected EnsemblEngine ensemblEngineHuman;
	protected EnsemblEngine ensemblEngineFruitFly;
	protected EnsemblEngine ensemblEngineMouse;
	protected GFFEngine gffEngine;
	protected GraphEngine graphEngine;
	protected BEDEngine bedEngine;
	protected SPARQLEngine sparqlEngine;
	protected BiobixEngine biobixEngine;
 
	protected RendererSettings settings = new RendererSettings();
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		WebApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(config.getServletContext());
		AutowireCapableBeanFactory beanFactory = ctx
				.getAutowireCapableBeanFactory();
		beanFactory.applyBeanPropertyValues(this, myBeanName);
	}


	//TODO: completely replace with datasource specific methods
	@Override
	//public List<Feature> getFeatures(String source, Map<String, String> arguments) throws Exception {
	public List<Feature> getFeaturesByRegion(String source, ArgumentMap arguments) throws Exception {
		log.debug("input was: ");
		for (String key: arguments.keySet()) log.debug(key + " : " + arguments.get(key));
		List<Feature> resultFeatures = new LinkedList<Feature>();
		GenomicRegion region = arguments.getGenomicRegion();
		if (source.equals("DS_example")) {
			resultFeatures.add(new Feature("1","before",null,"ste",null,null,null));
			resultFeatures.add(new Feature("2","starts too early",null,"ste",null,null,null));
			resultFeatures.add(new Feature("3","regular feature",null,"rf",null,null,null));
			resultFeatures.add(new Feature("4","too long",null,"cgt",null,null,null));
			resultFeatures.add(new Feature("5","after",null,"ste",null,null,null));
		} else if (source.equals("DS_refseq")) {
			String dnaString = null;
			if (arguments.getMinFeatureWidth() <= 1) {
				dnaString =  getAssemblyEngine(region.getSpecies()).getSeq(region.getChromosome(), region.getVisibleStart(), region.getVisibleEnd());
			}
			MappedReadFeature mappedRead = new MappedReadFeature("0", "Reference Sequence", (Double) null, "Assembly", null, dnaString, new LinkedList<Location>(), null);
			mappedRead.getLoc().add(new Location(region.getVisibleStart(),region.getVisibleEnd(),region.getChromosome(),true));
			resultFeatures.add(mappedRead);
		} else if (source.equals("DS_gene")) {
			List<Gene> genes = null;
			//genes = getEnsemblEngine (region.getSpecies()).getGenesByRegion(arguments.getZoomLevel(),arguments.getMinFeatureWidth(),region);
			genes = getEnsemblEngine (region.getSpecies()).getGenesByRegion(arguments.getMinDetailWidth(),arguments.getMinFeatureWidth(),region);
			resultFeatures.addAll(genes);
		} else if (source.equals("DS_GFF")) {
			resultFeatures = gffEngine.getFeatureGFF("/ontologies/transcripts.gff3");
		} else if (source.equals("DS_Wig")){
			resultFeatures = graphEngine.getWigFeatures(new File(source));
		} else if (source.equals("DS_Bed")){
			resultFeatures = bedEngine.getBedFeatures(new File(source));
		} else if (source.equals("DS_BigWig")){
			String  chromosome = null;
			chromosome = "chr".concat(region.getChromosome());
			resultFeatures = graphEngine.getBigFeatures(new File(source),chromosome,region.getVisibleStart(),region.getVisibleEnd());
		} else if (source.equals("DS_BigBed")){
			String chromosome = region.getChromosome();
			resultFeatures = graphEngine.getBigFeatures(new File(source),chromosome,region.getVisibleStart(),region.getVisibleEnd());
		} else if (source.equals("DS_SPARQL")) {
			String filterExpression = arguments.getFilterExpression();
			resultFeatures = sparqlEngine.getFeatures(filterExpression,region);
		}
		return resultFeatures;
	}






	@Override
	public Feature getFeatureById(String source, ArgumentMap arguments) throws Exception {
		log.debug("input was: ");
		for (String key: arguments.keySet()) log.debug(key + " : " + arguments.get(key));
		String id = arguments.getId();
		Species species = arguments.getGenomicRegion().getSpecies();
		if (source.equals("DS_gene")) {
			//return getEnsemblEngine(species).getGeneById(id, arguments.getZoomLevel());
			//FIXME
			return getEnsemblEngine(species).getGeneById(id, 0);
		} else if (source.equals("DS_example")) {
			if (!arguments.getId().equals("5")) return null;
			Feature result = new Feature("5","after",null,"ste",null,null,null);
			if (arguments.getZoomLevel() == ZoomLevel.DETAIL ) {
				List<Feature> subFeatures = new LinkedList<Feature>();
				subFeatures.add(new Feature("6","part1",null,"sf",null,null,null));
				subFeatures.add(new Feature("7","part1",null,"sf",null,null,null));
				result.setSubFeatures(subFeatures);
			}

			return result;
		}
		return null;
	}

	@Override
	public List<Chromosome> getChromosomes(Species species) throws Exception {
		return getEnsemblEngine(species).getChromosomes();
	}

	@Override
	public ChromosomeDetail getChromosomeDetail(Species species, String chromosome) throws Exception {
		return getEnsemblEngine(species).getChromosomeDetail(chromosome);
	}



	@Override
	public MappedReadFeature getReferenceSequence(int species, String chromosome,
			long start, long end) throws Exception {
		//TODO: fix species
		return new MappedReadFeature("0", "Reference Sequence", null, "Assembly", null,getAssemblyEngine(Species.HUMAN).getSeq(chromosome, start, end),null,null);
	}	

	protected EnsemblEngine getEnsemblEngine(Species species) {
		EnsemblEngine engine=null;
		switch (species) {
		case HUMAN: 	engine=ensemblEngineHuman;
		break;
		case FRUITFLY: 	engine=ensemblEngineFruitFly;
		break;
		case MOUSE: 	engine=ensemblEngineMouse;
		break;
		}
		return engine;
	}

	protected AssemblyEngine getAssemblyEngine(Species species) {
		AssemblyEngine engine=null;
		switch (species) {
		case HUMAN: engine=assemblyEngineHuman;
		break;
		//			case 7227: 	engine=assemblyEngineFruitFly;
		//						break;
		//			case 10090: engine=assemblyEngineMouse;
		//						break;
		}
		return engine;
	}

	public AssemblyEngine getAssemblyEngineHuman() {
		return assemblyEngineHuman;
	}

	public void setAssemblyEngineHuman(AssemblyEngine assemblyEngineHuman) {
		this.assemblyEngineHuman = assemblyEngineHuman;
	}

	public EnsemblEngine getEnsemblEngineHuman() {
		return ensemblEngineHuman;
	}

	public void setEnsemblEngineHuman(EnsemblEngine ensemblEngineHuman) {
		this.ensemblEngineHuman = ensemblEngineHuman;
	}

	public EnsemblEngine getEnsemblEngineFruitFly() {
		return ensemblEngineFruitFly;
	}

	public void setEnsemblEngineFruitFly(EnsemblEngine ensemblEngineFruitFly) {
		this.ensemblEngineFruitFly = ensemblEngineFruitFly;
	}

	public EnsemblEngine getEnsemblEngineMouse() {
		return ensemblEngineMouse;
	}

	public void setEnsemblEngineMouse(EnsemblEngine ensemblEngineMouse) {
		this.ensemblEngineMouse = ensemblEngineMouse;
	}

	public static void main(String[] args) {
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("WEB-INF/spring-service.xml");
		FeatureServerImpl fs = (FeatureServerImpl) context.getBean("featureServer");
		fs.test();
	}
	private void test() {
		try {
			System.out.println(assemblyEngineHuman.getSeq("1", 1, 20));
			System.out.println(assemblyEngineHuman.getSeq("1", 847, 853));
			System.out.println(assemblyEngineHuman.getSeq("1", 847, 847));
			System.out.println(assemblyEngineHuman.getSeq("1", 848, 848));
			System.out.println(assemblyEngineHuman.getSeq("1", 849, 849));
			System.out.println(assemblyEngineHuman.getSeq("1",15050,15099));
			System.out.println(assemblyEngineHuman.getSeq("1",9950,10049));
			System.out.println(assemblyEngineHuman.getSeq("1",20134556,20137899));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void setGffEngine(GFFEngine gffEngine) {
		this.gffEngine = gffEngine;
	}
	public void setGraphEngine(GraphEngine graphEngine) {
		this.graphEngine = graphEngine;
	}
	public void setBedEngine(BEDEngine bedEngine) {
		this.bedEngine = bedEngine;
	}
	public void setSparqlEngine(SPARQLEngine sparqlEngine) {
		this.sparqlEngine = sparqlEngine;
	}
	public BiobixEngine getBiobixEngine() {
		return biobixEngine;
	}
	public void setBiobixEngine(BiobixEngine biobixEngine) {
		this.biobixEngine = biobixEngine;
	}


	@Override
	public List<Feature> getFeaturesByRegion(FeatureDatasource ds, GenomicRegion region, String filterExpression, Integer minFeatureSize, Integer minDetailedSize) throws Exception {
		throw new Exception("Unhandled FeatureDatasource type. Use specific subtypes."); 
	}
	
	@Override
	public List<Feature> getFeaturesByRegion(RefSeqDatasource ds, GenomicRegion region, String filterExpression, Integer minFeatureSize, Integer minDetailedSize) throws Exception {
		List<Feature> resultFeatures = new LinkedList<Feature>();
		String assemblyName = ds.getAssemblyName();
		String dnaString = null;
		if (minFeatureSize <= .33) {
			dnaString =  getAssemblyEngine(region.getSpecies()).getSeq(region.getChromosome(), region.getVisibleStart(), region.getVisibleEnd());
		}
		MappedReadFeature mappedRead = new MappedReadFeature("0", "Reference Sequence", (Double) null, "Assembly", null, dnaString, new LinkedList<Location>(), null);
		mappedRead.getLoc().add(new Location(region.getVisibleStart(),region.getVisibleEnd(),region.getChromosome(),true));
		resultFeatures.add(mappedRead);
		return resultFeatures;
	}
	
	@Override
	public List<Feature> getFeaturesByRegion(GeneDatasource ds, GenomicRegion region, String filterExpression, Integer minFeatureSize, Integer minDetailedSize) throws Exception {
		List<Feature> resultFeatures = new LinkedList<Feature>();
		List<Gene> genes = null;
		//genes = getEnsemblEngine (region.getSpecies()).getGenesByRegion(minFeatureSize,minDetailedSize,region);
		genes = getEnsemblEngine(region.getSpecies()).getGenesByRegionFast(minFeatureSize,minDetailedSize,region);
		resultFeatures.addAll(genes);
		return resultFeatures;
	}
	
	@Override
	public List<Feature> getFeaturesByRegion(GFFDatasource ds, GenomicRegion region, String filterExpression, Integer minFeatureSize, Integer minDetailedSize) throws Exception {
		List<Feature> resultFeatures;
		if (ds.getCanBeFiltered()) {
			resultFeatures = sparqlEngine.getFeatures(filterExpression,region);
		} else {
			resultFeatures = gffEngine.getFeatureGFF(ds.getFilePath());
		}
		return resultFeatures;
	}


	@Override
	public List<Feature> getFeaturesByRegion(BBxAssemblyDatasource ds, GenomicRegion region, String filterExpression, Integer minFeatureSize, Integer minDetailedSize) throws Exception {
		String chromosome = null;
		if (ds.getChromosomePrefix() != null) {
			chromosome = ds.getChromosomePrefix() + region.getChromosome();
		} else {
			chromosome = region.getChromosome();
		}
		Long start = region.getVisibleStart();
		Long end = region.getVisibleEnd();
		return biobixEngine.getAssemblyData(start, end, chromosome, ds.getSampleId(), minFeatureSize);
	}


	@Override
	public Feature getFeatureById(FeatureDatasource ds, String featureId) throws Exception {
		throw new Exception("Unhandled FeatureDatasource type. Use specific subtypes."); 
	}


	@Override
	public Feature getFeatureById(GFFDatasource ds, String featureId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Feature getFeatureById(GeneDatasource ds, String featureId) throws Exception {
		return getEnsemblEngine(Species.HUMAN).getGeneByIdFast(featureId, 0);
	}


	@Override
	public Feature getFeatureById(RefSeqDatasource ds, String featureId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Feature getFeatureById(BBxAssemblyDatasource ds, String featureId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
