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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import scala.actors.threadpool.Arrays;

import com.genohm.viewsGWT.shared.Chromosome;
import com.genohm.viewsGWT.shared.ChromosomeDetail;
import com.genohm.viewsGWT.shared.GenomicRegion;
import com.genohm.viewsGWT.shared.Location;
import com.genohm.viewsGWT.shared.ZoomLevel;
import com.genohm.viewsGWT.shared.data.ensembl.EnsemblChromosomeData;
import com.genohm.viewsGWT.shared.data.ensembl.EnsemblExonData;
import com.genohm.viewsGWT.shared.data.ensembl.EnsemblFeatureData;
import com.genohm.viewsGWT.shared.data.ensembl.EnsemblGeneData;
import com.genohm.viewsGWT.shared.data.ensembl.EnsemblTranscriptData;
import com.genohm.viewsGWT.shared.data.ensembl.EnsemblTranslationData;
import com.genohm.viewsGWT.shared.data.feature.Exon;
import com.genohm.viewsGWT.shared.data.feature.Gene;
import com.genohm.viewsGWT.shared.data.feature.Transcript;


public class EnsemblEngine extends NamedParameterJdbcTemplate {
    
	//defined spring
	private int ensemblCoordinateID=0;
	private int ensemblCpGAnalysisID=0;
	private String genomicSequenceTable="";
	private int speciesId;
	private AssemblyEngine assemblyEngine;
	
	private static final Logger log = Logger.getLogger(EnsemblEngine.class);
    
    public EnsemblEngine(BasicDataSource ds) {
    	super(ds);
    }
    
    public List<Chromosome> getChromosomes() throws Exception {
    	String queryString = "SELECT DISTINCT name,seq_region_id,length FROM seq_region WHERE coord_system_id= :coord_system ORDER BY name";
		List<Chromosome> result = new LinkedList<Chromosome>();
    	try {
    		log.debug("getting chromosome info");
    		SqlRowSet rs = super.queryForRowSet(queryString, Collections.singletonMap("coord_system", ensemblCoordinateID));
    		while (rs.next()) {
    			Chromosome chr = new Chromosome();
    			chr.setName(rs.getString(1));
    			chr.setId(rs.getString(2));
    			chr.setStartCoordinate(1L);
    			chr.setEndCoordinate(rs.getLong(3));
    			result.add(chr);
    		}
    	} catch (Exception e) {
    		log.error("Could not get chromosomes ",e);
    		throw new Exception("SQL error getting chromosomes");
    	}
    	Collections.sort(result, new Comparator<Chromosome>() {
			@Override
			public int compare(Chromosome o1, Chromosome o2) {
				try {
					return (Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName()));
				} catch (NumberFormatException e) {
					return o1.getName().compareTo(o2.getName());
				}
			}
		});
    	return result;
    }
    
    public ChromosomeDetail getChromosomeDetail(String chromosomeId) throws Exception {
    	String queryString= "SELECT name,length FROM seq_region WHERE coord_system_id= :coord_system AND seq_region_id= :seq_region_id";
    	ChromosomeDetail chr = null;
		try {
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			parameterMap.put("coord_system", ensemblCoordinateID);
			parameterMap.put("seq_region_id", chromosomeId);
			log.debug("getting chromosome info for "+chromosomeId);
        	SqlRowSet rs = super.queryForRowSet(queryString, parameterMap) ;
			if (rs.next()) {
				chr = new ChromosomeDetail();
				chr.setName(rs.getString(1));
				chr.setId(chromosomeId);
				chr.setStartCoordinate(1L);
				chr.setEndCoordinate(rs.getLong(2));
			} else {
				String error = "No chromosome found with name "+chromosomeId;
				log.error(error);
				throw new Exception(error);
			}
			if (rs.next()) {
				String error = "Multiple chromosomes found with name "+chromosomeId;
				log.error(error);
				throw new Exception(error);
			}
			log.debug("success");
		}	
		catch (Exception e) {
			String error = "SQL error getting chromosome "+chromosomeId;
			log.error(error,e);
			throw new Exception(error,e);
		} 
		List<Gene> genesList = getGenesByChromosomeId(chromosomeId);
		chr.setGenes(genesList);
		return chr;
    }
    
    public EnsemblChromosomeData getChromosome(String chromosome) throws Exception {
    	String queryString= "SELECT seq_region_id,length FROM seq_region WHERE coord_system_id= :coord_system AND name= :chromosome ";
    	EnsemblChromosomeData chr = new EnsemblChromosomeData();
    	
		try {
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			parameterMap.put("coord_system", ensemblCoordinateID);
			parameterMap.put("chromosome", chromosome);
			log.debug("getting chromosome info for "+chromosome);
        	SqlRowSet rs = super.queryForRowSet(queryString, parameterMap) ;
			while (rs.next()) {
				chr.setName(chromosome);
				chr.setLength(rs.getLong(2));
				chr.setSpeciesID(speciesId);
			}
			log.debug("success");
		}	
		catch (Exception e) {
			log.error("Error getting chromsome "+chromosome,e);
			throw new Exception("SQL error getting chromosome "+chromosome);
		} finally {
		}
    	
    	return chr;
    }
    
    public List<EnsemblFeatureData> getCpGIslandsByChromosome(String chromosome) throws Exception {
    	String queryString = "SELECT simple_feature.seq_region_start,simple_feature.seq_region_end," +
    			"simple_feature.display_label,simple_feature.score FROM simple_feature,seq_region " +
    			"WHERE seq_region.seq_region_id = simple_feature.seq_region_id AND " +
    			"seq_region.coord_system_id= :coord_system AND seq_region.name= :chromosome AND analysis_id= :analysis_id";

		List<EnsemblFeatureData> result = new LinkedList<EnsemblFeatureData>();
		try {
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			parameterMap.put("coord_system", ensemblCoordinateID);
			parameterMap.put("chromosome", chromosome);
			parameterMap.put("analysis_id", ensemblCpGAnalysisID);
			log.debug("getting genes for chromosome "+chromosome);
        	SqlRowSet rs = super.queryForRowSet(queryString, parameterMap) ;
			while (rs.next()) {
				EnsemblFeatureData cpg = new EnsemblFeatureData();
				cpg.setChromosome(chromosome);
				cpg.setStart(rs.getLong(1));
				cpg.setEnd(rs.getLong(2));
				cpg.setName(rs.getString(3));
				cpg.setScore(rs.getFloat(4));
				cpg.setDescription(cpg.getName()+" Score:"+rs.getFloat(4));
				result.add(cpg);
			}
			log.debug("success");
		}	
		catch (Exception e) {
			log.error("Error getting genes for chromsome "+chromosome,e);
			throw new Exception("SQL error getting genes for chromosome "+chromosome);
		} finally {
		}
		
		return result;
    }
    
    public List<EnsemblGeneData> getGenesByName(String name) throws Exception {
    	String queryString = "SELECT  xref.display_label, gene.description, gene.seq_region_start,"+
		 "        gene.seq_region_end, gene.seq_region_strand, gene.biotype, seq_region.name, gene.stable_id"+
		 " FROM   gene, xref, seq_region"+
		 " WHERE  gene.display_xref_id=xref.xref_id"+
		 " AND	  gene.seq_region_id=seq_region.seq_region_id"+
		 " AND    seq_region.coord_system_id= :coord_system"+
		 " AND    xref.display_label LIKE '%"+name+"%'";

		List<EnsemblGeneData> result = new LinkedList<EnsemblGeneData>();
		try {
			log.debug("getting genes with name "+name);
        	SqlRowSet rs = super.queryForRowSet(queryString, Collections.singletonMap("coord_system", ensemblCoordinateID)) ;
			while (rs.next()) {
				EnsemblGeneData gene = new EnsemblGeneData();
				gene.setLabel(rs.getString(1));
				gene.setDescription(rs.getString(2));
				gene.setStartPos(rs.getLong(3));
				gene.setEndPos(rs.getLong(4));
				gene.setStrand(rs.getInt(5));
				gene.setType(rs.getString(6));
				gene.setChromosome(rs.getString(7));
				gene.setEnsemblID(rs.getString(8));
				result.add(gene);
			}
			log.debug("success");
		}	
		catch (Exception e) {
			log.error("Error getting genes with name "+name,e);
			throw new Exception("SQL error getting genes with name "+name);
		} finally {
		}
		
		return result;
    }
    
    
    public List<Gene> getGenesByRegionFast(final Integer minFeatureWidth, final Integer minDetailWidth, final GenomicRegion region) throws Exception {
    	log.debug("Querying for genes in region "+region);
    	Date start = new Date();
    	// get gene skeleton for genes > minFeatureWidth
    	
    	String geneQuery = "" +
    	" SELECT xref.display_label, gene.stable_id, gene.description, gene.seq_region_start,"+ //1 2 3 4
    	" 		 gene.seq_region_end, gene.seq_region_strand, gene.biotype, seq_region.name, gene.gene_id" + //5 6 7 8 9
    	" FROM	 gene, seq_region, xref"  +
    	" WHERE	 gene.seq_region_end >= :start AND gene.seq_region_start <= :end" +
    	" AND    gene.seq_region_end - gene.seq_region_start >= :minSize" +
		" AND	 gene.seq_region_id=seq_region.seq_region_id"+
		" AND    seq_region.name= :chromosome"+
		" AND    seq_region.coord_system_id= :coord_system"+
		" AND	 gene.display_xref_id=xref.xref_id";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("coord_system", ensemblCoordinateID);
		parameterMap.put("chromosome", region.getChromosome());
		parameterMap.put("start", region.getRegionStart());
		parameterMap.put("end", region.getRegionEnd());
		parameterMap.put("minSize", minFeatureWidth);

    	final List<Integer> detailedIds = new LinkedList<Integer>(); 
    	// if problems arise here we might split the list and iterate over smaller "in" clauses
    	final Map<String,Gene> geneMap = super.query(geneQuery, parameterMap, new ResultSetExtractor<Map<String,Gene>>() {

			@Override
			public Map<String, Gene> extractData(ResultSet rs) throws SQLException, DataAccessException {
		    	Map<String, Gene> geneMap = new HashMap<String, Gene>();
		    	while (rs.next()) {
					Gene gene = new Gene();
					String geneId = rs.getString(2);
					String chrom = rs.getString(8);
					Boolean strand = rs.getInt(6) == 1;
					gene.setId(geneId);
					gene.setName(rs.getString(1));
					gene.setLoc(new LinkedList<Location>());
					Location loc = new Location();
					loc.setChr(chrom);
					loc.setStart(rs.getLong(4));
					loc.setEnd(rs.getLong(5));
					loc.setStrand(strand);
					gene.getLoc().add(loc);
					gene.setDescription(rs.getString(3));
					if (gene.getEnd() - gene.getStart() >= minDetailWidth) detailedIds.add(rs.getInt(9));
					geneMap.put(geneId, gene);
		    	}
		    	return geneMap;
			}
    	});
    	Date first = new Date();
    	// now get detail for genes > minDetailWidth
    	if (detailedIds.size() ==0 ) {
        	log.debug("Done fetching genes: query took "+(first.getTime() - start.getTime()) + " ms");
        	return new LinkedList<Gene>(geneMap.values());
    	}
    	log.debug("Querying for gene detail ");

    	String queryString = "" +
    	" SELECT gene.stable_id, " + //1
    	"		 transcript.stable_id, transcript.seq_region_start, transcript.seq_region_end," + //2 3 4
    	"		 transcript.biotype, transcript.status," + //5 6
    	"		 translation.stable_id, translation.seq_start, translation.start_exon_id = exon.exon_id as translation_starts," + //7 8 9
    	"		 translation.seq_end, translation.end_exon_id = exon.exon_id as translation_ends," + //10 11
    	"		 exon.stable_id, exon.seq_region_start, exon.seq_region_end, exon_transcript.rank"+ //12 13 14 15
    	" FROM	 gene, translation, transcript, exon_transcript, exon" +
    	" WHERE	 gene.gene_id in ( :ids )" +
		" AND	 translation.transcript_id = transcript.transcript_id" +
		" AND	 gene.gene_id=transcript.gene_id" +
		" AND	 exon_transcript.exon_id = exon.exon_id" +
		" AND	 exon_transcript.transcript_id = transcript.transcript_id" +
		" AND	 transcript.gene_id = gene.gene_id" +
		" ORDER BY gene.stable_id, transcript.stable_id, exon_transcript.rank";
    	final Set<Gene> detailedGenes = new HashSet<Gene>();
    	final Map<String, Transcript> transcriptMap = new HashMap<String, Transcript>();
    	final Map<String, Exon> exonMap = new HashMap<String, Exon>();
    	super.query(queryString, Collections.singletonMap("ids", detailedIds), new ResultSetExtractor<Map<String,Gene>>() {
			@Override
			public Map<String,Gene> extractData(ResultSet rs) throws SQLException, DataAccessException {
				while(rs.next()) {
					String geneId = rs.getString(1);
					Gene gene = geneMap.get(geneId);
					detailedGenes.add(gene);
					String transcriptId = rs.getString(2);
					String exonId = rs.getString(12);
					String chrom = gene.getLoc().get(0).getChr();
					Boolean strand = gene.getLoc().get(0).getStrand();
					Transcript transcript = null;
					Exon exon = null;
					if (gene.getTranscripts()==null) gene.setTranscripts(new LinkedList<Transcript>());
					if (transcriptMap.containsKey(transcriptId)) {
						transcript = transcriptMap.get(transcriptId);
					} else {
						transcript = new Transcript();
						transcript.setId(transcriptId);
						transcript.setName(transcriptId);
						transcript.setParent(gene);
						transcript.setLoc(new LinkedList<Location>());
						Location loc = new Location();
						loc.setChr(chrom);
						loc.setStart(rs.getLong(3));
						loc.setEnd(rs.getLong(4));
						loc.setStrand(strand);
						transcript.getLoc().add(loc);
						transcript.setExons(new LinkedList<Exon>());
						transcriptMap.put(transcriptId, transcript);
					}
					if (!gene.getTranscripts().contains(transcript)) gene.getTranscripts().add(transcript);
					if (exonMap.containsKey(exonId)) {
						exon = exonMap.get(exonId);
					} else {
						exon = new Exon();
						exon.setId(exonId);
						exon.setName(exonId);
						exon.setLoc(new LinkedList<Location>());
						Location loc = new Location();
						loc.setChr(chrom);
						loc.setStart(rs.getLong(13));
						loc.setEnd(rs.getLong(14));
						loc.setStrand(strand);
						exon.getLoc().add(loc);
						exon.setParent(transcript);
						exonMap.put(exonId, exon);
					}
					if (!transcript.getExons().contains(exon)) transcript.getExons().add(exon);
					if (rs.getBoolean(9)) {
						if (strand)
							transcript.setTranslationStart(exon.getStart() + rs.getLong(8) - 1);
						else
							transcript.setTranslationStart(exon.getEnd() - rs.getLong(8) + 1);
					}
					if (rs.getBoolean(11)) {
						if (strand)
							transcript.setTranslationEnd(exon.getStart() + rs.getLong(10) - 1);
						else
							transcript.setTranslationEnd(exon.getStart() - rs.getLong(10) + 1);
					}
				}
				return geneMap;
			}
    	});
    	Date second = new Date();
    	for (Gene gene: detailedGenes) {
    		fetchSequence(gene);
    	}
    	Date third = new Date();
    	log.debug("Done fetching genes: first query took "+(first.getTime() - start.getTime()) + "ms\tSecond query took "+(second.getTime() - first.getTime())+ "ms\tFetching sequences took "+(third.getTime() -second.getTime())+ " ms");
    	return new LinkedList<Gene>(geneMap.values());
    	
    }
    
    public void fetchSequence(Gene gene) throws Exception {
    	gene.setDnaString(assemblyEngine.getSeq(gene.getChr(), gene.getStart(), gene.getEnd()));
    }
    
    
    public List<Gene> getGenesByRegion(Integer minFeatureWidth, Integer minDetailWidth, GenomicRegion region) throws Exception {
    	
    	String queryString = "SELECT  gene.stable_id"+
		 					 " FROM   gene, xref, seq_region"+
		 					 " WHERE  gene.display_xref_id=xref.xref_id"+
		 					 " AND	  gene.seq_region_id=seq_region.seq_region_id"+
		 					 " AND    seq_region.coord_system_id= :coord_system"+
		 					 //" AND    seq_region.seq_region_id=?"+
		 					 " AND    seq_region.name= :chromosome"+
		 					 " AND 	  gene.seq_region_end >= :start"+
		 					 " AND 	  gene.seq_region_start <= :end"+
		 					 " AND    gene.seq_region_end - seq_region_start > :minWidth";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("coord_system", ensemblCoordinateID);
		parameterMap.put("chromosome", region.getChromosome());
		parameterMap.put("start", region.getRegionStart());
		parameterMap.put("end", region.getRegionEnd());
		parameterMap.put("minSize", minFeatureWidth);
		List<Gene> result = new LinkedList<Gene>();
		try {
			log.debug("getting genes for region "+region);
        	SqlRowSet rs = super.queryForRowSet(queryString, parameterMap);
			while (rs.next()) {
				Gene gene = getGeneById(rs.getString(1), minDetailWidth);
				result.add(gene);
			}
			log.debug("success");
		}	
		catch (Exception e) {
			String error = "Error getting genes for region "+region;
			log.error(error,e);
			throw new Exception(error,e);
		} 
		return result;

    }

    
    public List<Gene> getGenesByChromosomeId(String chromosomeId) throws Exception {
    	 
    	String queryString = "SELECT  xref.display_label, gene.description, gene.seq_region_start,"+
		 					 "        gene.seq_region_end, gene.seq_region_strand, gene.biotype, seq_region.name, gene.stable_id"+
		 					 " FROM   gene, xref, seq_region"+
		 					 " WHERE  gene.display_xref_id=xref.xref_id"+
		 					 " AND	  gene.seq_region_id=seq_region.seq_region_id"+
		 					 " AND    seq_region.coord_system_id= :coord_system"+
		 					 " AND    seq_region.seq_region_id= :chromosomeId";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("coord_system", ensemblCoordinateID);
		parameterMap.put("chromosomeId", chromosomeId);
	
		List<Gene> result = new LinkedList<Gene>();
		try {
			log.debug("getting genes for chromosome "+chromosomeId);
        	SqlRowSet rs = super.queryForRowSet(queryString, parameterMap);
			while (rs.next()) {
				Gene gene = new Gene();
				gene.setName(rs.getString(1));
				gene.setDescription(rs.getString(2));
				gene.getLoc().add( new Location(rs.getLong(3),rs.getLong(4),rs.getString(5),rs.getInt(6)==1));
				gene.setScore(rs.getDouble(7));
				gene.setId(rs.getString(8));
				result.add(gene);
			}
			log.debug("success");
		}	
		catch (Exception e) {
			String error = "Error getting genes for chromsome "+chromosomeId;
			log.error(error,e);
			throw new Exception(error,e);
		} 
		return result;

    }
    
    public List<EnsemblGeneData> getGenesByChromosome(String chromosome) throws Exception {
    	String queryString = "SELECT  xref.display_label, gene.description, gene.seq_region_start,"+
		 "        gene.seq_region_end, gene.seq_region_strand, gene.biotype, seq_region.name, gene.stable_id"+
		 " FROM   gene, xref, seq_region"+
		 " WHERE  gene.display_xref_id=xref.xref_id"+
		 " AND	  gene.seq_region_id=seq_region.seq_region_id"+
		 " AND    seq_region.coord_system_id= :coord_system"+
		 " AND    seq_region.name= :chromosome";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("coord_system", ensemblCoordinateID);
		parameterMap.put("chromosome", chromosome);

		List<EnsemblGeneData> result = new LinkedList<EnsemblGeneData>();
		try {
			log.debug("getting genes for chromosome "+chromosome);
        	SqlRowSet rs = super.queryForRowSet(queryString, parameterMap) ;
			while (rs.next()) {
				EnsemblGeneData gene = new EnsemblGeneData();
				gene.setLabel(rs.getString(1));
				gene.setDescription(rs.getString(2));
				gene.setStartPos(rs.getLong(3));
				gene.setEndPos(rs.getLong(4));
				gene.setStrand(rs.getInt(5));
				gene.setType(rs.getString(6));
				gene.setChromosome(rs.getString(7));
				gene.setEnsemblID(rs.getString(8));
				result.add(gene);
			}
			log.debug("success");
		}	
		catch (Exception e) {
			log.error("Error getting genes for chromsome "+chromosome,e);
			throw new Exception("SQL error getting genes for chromosome "+chromosome);
		} finally {
		}
		
		return result;
    }
    
    
//    mysql> select count(*) from exon_transcript where transcript_id in (select transcript_id from transcript where gene_id in (select gene_id from gene where seq_region_end - seq_region_start > 1000000));
//    +----------+
//    | count(*) |
//    +----------+
//    |     9778 |
//    +----------+
//    1 row in set (19.37 sec)
//
//    mysql> select count(*) from exon_transcript,transcript,gene where (exon_transcript.transcript_id = transcript.transcript_id and transcript.gene_id = gene.gene_id and gene.seq_region_end - gene.seq_region_start > 1000000);
//    +----------+
//    | count(*) |
//    +----------+
//    |     9778 |
//    +----------+
//    1 row in set (0.13 sec)

    
    public List<EnsemblTranscriptData> getTranscripts(String gene_id) throws Exception {
		String queryString = "SELECT  transcript.stable_id, " +
							 "		  transcript.seq_region_start, " +
							 "		  transcript.seq_region_end, " +
							 "        transcript.biotype, " +
							 "        transcript.status," +
							 " 		  xref.display_label," +
							 "	  	  xref.description " +
							 " FROM   gene, transcript LEFT JOIN xref ON transcript.display_xref_id=xref.xref_id"+
							 " WHERE  gene.gene_id=transcript.gene_id"+
							 " AND	  gene.stable_id= :geneId";
    	List<EnsemblTranscriptData> list = new LinkedList<EnsemblTranscriptData>();
		try {
			log.debug("getting  transcripts for gene "+gene_id);
			SqlRowSet rs = super.queryForRowSet(queryString, Collections.singletonMap("geneId", gene_id)) ;
			//if (!rs.next()) throw new MutaException("could not exons for transcript "+transcript_id); // you skip first exon if you do this !
			while(rs.next()) {
				EnsemblTranscriptData transcript = new EnsemblTranscriptData();
				transcript.setEnsemblID(rs.getString(1));
				transcript.setStartPos(rs.getLong(2));
				transcript.setEndPos(rs.getLong(3));
				transcript.setBiotype(rs.getString(4));
				transcript.setStatus(rs.getString(5));
				transcript.setName(rs.getString(6));
				transcript.setDescription(rs.getString(7));
				transcript.setExons(this.getExonData(transcript.getEnsemblID()));
				list.add(transcript);
			}
			log.debug("success");
		}	
		catch (Exception e) {
			//SQLException ee=(SQLException)e;
			//log.error(ee.getMessage()+" error getting exons for transcript "+transcript_id);
			throw new Exception("SQL error while getting transcript for "+gene_id);
		} finally {
		}
    	return list;
    }
    
    
    
    
    public List<EnsemblExonData> getExonData(String transcript_id) throws Exception {
    	String queryString = "SELECT exon.exon_id," +
    						 	   " translation.seq_start," +
    						 	   " translation.start_exon_id," +
    						 	   " translation.seq_end," +
    						 	   " translation.end_exon_id, " +
    						 	   " exon.seq_region_start," +
    						 	   " exon_transcript.rank," +
    						 	   " exon.seq_region_end," +
    						 	   " exon_transcript.rank," +
    						 	   " transcript.seq_region_strand," +
    						 	   " exon.stable_id"	+
    						  " FROM exon, exon_transcript, transcript" +
    						 	   " LEFT JOIN  translation ON translation.transcript_id=transcript.transcript_id " +
    						 " WHERE exon_transcript.transcript_id = transcript.transcript_id " +
    						   " AND exon_transcript.exon_id = exon.exon_id " +
    						   " AND transcript.stable_id = :transcriptId" +
    					  " ORDER BY exon_transcript.rank ASC";

    	List<EnsemblExonData> result = new LinkedList<EnsemblExonData>();
		try {
			log.debug("getting exons for transcript "+transcript_id);
			
			
			
			SqlRowSet rs = super.queryForRowSet(queryString, Collections.singletonMap("transcriptId", transcript_id)) ;
			int stage=0;
			while(rs.next()) {
				EnsemblExonData newexon = new EnsemblExonData();
				newexon.setStartPos(rs.getLong(6));
				newexon.setEndPos(rs.getLong(8));
				newexon.setRank(rs.getInt(7));
				long translationStart=0;
				long translationEnd=0;
				int strand=rs.getInt(10);
				translationStart=0;
				translationEnd=0;
				//String log= "";
				int translationStartExonId =rs.getInt(3)*1;
				int translationEndExonId =rs.getInt(5)*1;
				int exonId=rs.getInt(1)*1;
				
				if(translationStartExonId==0 && translationEndExonId==0) { 
					translationStart=0; 
					translationEnd=0;
				} else {
					if(strand==1) {
						translationStart=0;translationEnd=0;
						if(stage==1) { translationStart=newexon.getStartPos();translationEnd=newexon.getEndPos(); }
						if(exonId==translationStartExonId) { translationStart=newexon.getStartPos()+rs.getInt(2)-2;stage=1; };
						if(exonId==translationEndExonId) { translationEnd=newexon.getStartPos()+rs.getInt(4)-2; stage=2;};
						if(stage==1 && translationEnd==0 && translationStart!=0) { translationEnd=newexon.getEndPos(); }
						if(stage==2 && translationStart==0 && translationEnd!=0) { translationStart=newexon.getStartPos(); }
					} else {
						translationStart=0;translationEnd=0;
						if(stage==1) { translationStart=newexon.getEndPos();translationEnd=newexon.getStartPos(); }
						if(exonId==translationStartExonId) { translationStart=newexon.getEndPos()-rs.getInt(2)+1;stage=1; };
						if(exonId==translationEndExonId) { translationEnd=newexon.getEndPos()-rs.getInt(4)+1;stage=2; };   
						if(stage==1 && translationEnd==0 && translationStart!=0) { translationEnd=newexon.getStartPos(); }
						if(stage==2 && translationStart==0 && translationEnd!=0) { translationStart=newexon.getEndPos(); }
					}
				}
				newexon.setTstartPos(translationStart);
				newexon.setTendPos(translationEnd);
				newexon.setEnsembl_id(rs.getString(11));
				result.add(newexon);
			}
			log.debug("success");
		}	
		catch (Exception e) {
			log.error("error getting exons for transcript "+transcript_id,e);
			throw new Exception("SQL error while getting exons for "+transcript_id);
		} 
		return result;
    }
    
    private long getExonStart(String exon_id) throws SQLException {
    	String queryString = "SELECT  exon.seq_region_start"+
		 					 " FROM	  exon"+
		 					 " WHERE  exon.exon_id = :exonId";
    	
    	SqlRowSet rs = super.queryForRowSet(queryString, Collections.singletonMap("exonId", exon_id)) ;
    	if (rs.next()) {
    		return rs.getLong(1);
    	} else {
    		return 0;
    	}
    }
    private long getExonEnd(String exon_id) throws SQLException {
    	String queryString = "SELECT  exon.seq_region_end"+
		 					 " FROM	  exon"+
		 					 " WHERE  exon.exon_id =:exonId";
    	
    	SqlRowSet rs = super.queryForRowSet(queryString, Collections.singletonMap("exonId", exon_id)) ;
    	if (rs.next()) {
    		return rs.getLong(1);
    	} else {
    		return 0;
    	}
    }
    public EnsemblTranslationData getTranslation(String transcript_id, Boolean strand) throws Exception {
    	String queryString = "SELECT  translation.stable_id," +
    						 "		  translation.seq_start,"+
    						 "		  translation.start_exon_id,"+ 
    						 "		  translation.seq_end,"+ 
    						 "		  translation.end_exon_id"+
    						 " FROM	  translation, transcript"+
    						 " WHERE  translation.transcript_id = transcript.transcript_id AND " +
    						 "		  transcript.stable_id = :transcriptId";

    	EnsemblTranslationData result = null;
    	try {
    		log.debug("getting translation info for"+transcript_id);

        	SqlRowSet rs = super.queryForRowSet(queryString,  Collections.singletonMap("transcriptId", transcript_id)) ;
    		if (rs.next()) {
    			result = new EnsemblTranslationData();
    			if (strand) {
    				long start = getExonStart(rs.getString(3))+rs.getInt(2)-1;
    				long end = getExonStart(rs.getString(5))+rs.getInt(4)-1;
    				result.setStartPos(Math.min(start, end));
    				result.setEndPos(Math.max(start, end));
    			} else {
       				long start = getExonEnd(rs.getString(3))-rs.getInt(2)+1;
    				long end = getExonEnd(rs.getString(5))-rs.getInt(4)+1;
    				result.setStartPos(Math.min(start, end));
    				result.setEndPos(Math.max(start, end));
 
    			}
    			result.setEnsembl_id(rs.getString(1));
    			if (rs.next()) {
    				throw new Exception("Transcript with multiple translations : "+transcript_id);
    			}
    		}
		} catch (SQLException e) {
    		log.error("error getting translation data for "+transcript_id,e);
			throw new Exception("SQL error while getting translation info for "+transcript_id);   		
    	}
    	return result;
    }
    
    public List<String> getTranscriptIds(String ensembl_id) throws Exception {
		String queryString = "SELECT  transcript.stable_id"+
							 " FROM   gene, transcript "+
							 " WHERE  gene.gene_id=transcript.gene_id"+
							 " AND	 gene.stable_id= :ensemblId";

		List<String> result = new LinkedList<String>();
		try {
			log.debug("getting transcript ids for "+ensembl_id);
        	SqlRowSet rs = super.queryForRowSet(queryString,  Collections.singletonMap("ensemblId",ensembl_id)) ;
			while (rs.next()) {
				result.add(rs.getString(1));
			}
			log.debug("success");
		}	
		catch (Exception e) {
			log.error("error getting transcripts for "+ensembl_id,e);
			throw new Exception("SQL error while getting transcripts for "+ensembl_id);
		} finally {
		}
		
		return result;
    }
    public List<String> getRefseqIdFromEnsemblId(String ensembl_id) throws Exception
    {
    	List<String> result = new LinkedList<String>();
    	String queryString = "SELECT display_label " +
    						 "FROM xref " +
    						 "WHERE xref_id IN (" +
    						 	"SELECT xref_id " +
    						 	"FROM object_xref " +
    						 	"WHERE ensembl_id IN (" +
    						 		"SELECT transcript_id " +
    						 		"FROM transcript" +
    						 		"WHERE stable_id= :ensemblId )) " +
    						 	"AND external_db_id = (" +
    						 		"SELECT external_db_id " +
    						 		"FROM external_db " +
    						 		"WHERE db_name = 'RefSeq_dna')";
    	try {
    		SqlRowSet rs = super.queryForRowSet(queryString,Collections.singletonMap("ensemblId",ensembl_id));
    		log.debug("Finding refseq id for ensembl id: "+ensembl_id);
    		while (rs.next()) {
    			result.add(rs.getString(1));
    		}
    		log.debug("success");
    	} catch (Exception e) {
    		log.error("error getting refseq ids for ensembl id: "+ensembl_id);
    		throw new Exception("SQL error while getting refseq ids for: "+ensembl_id);
    	}
    	return result;
    }
    public List<String> getIdFromRefseqId(String refseq_id) throws Exception
    {
    	List<String> result = new LinkedList<String>();
    	
    	String queryString = "SELECT transcript.stable_id " +
    						 "FROM transcript,object_xref,xref " +
    						 "WHERE transcript.transcript_id = object_xref.ensembl_id " +
    						   "AND object_xref.xref_id = xref.xref_id " +
    						   "AND xref.display_label = :refseqId " +
    						   "AND xref.external_db_id = (" +
    						   		"SELECT external_db_id " +
    						   		"FROM external_db " +
    						   		"WHERE db_name = 'RefSeq_dna')";
    	try {
    		SqlRowSet rs = super.queryForRowSet(queryString, Collections.singletonMap("refseqId",refseq_id));
    		log.debug("Finding ensembl id for refseq id: "+refseq_id);
    		while (rs.next()) {
    			result.add(rs.getString(1));
    		}
    		log.debug("success");
    	} catch (Exception e) {
    		log.error("error getting ensembl ids for refseq id: "+refseq_id);
    		throw new Exception("SQL error while getting ensembl ids for: "+refseq_id);
    	}
    	return result;
    }
    public List<String> getGeneIdsByChromosome(String chromosome) throws Exception {
    	String queryString = "SELECT  gene.stable_id"+
    						 " FROM   gene, seq_region"+
    						 " WHERE  seq_region.seq_region_id = gene.seq_region_id"+
    						 " AND    seq_region.coord_system_id= :coord_system"+
    						 " AND    seq_region.name= :chromosome";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("coord_system", ensemblCoordinateID);
		parameterMap.put("chromosome", chromosome);

		List<String> result = new LinkedList<String>();
		try {
			log.debug("getting gene ids for chromosome "+chromosome);
        	SqlRowSet rs = super.queryForRowSet(queryString, parameterMap) ;
			while (rs.next()) {
				result.add(rs.getString(1));
			}
			log.debug("success");
		}	
		catch (Exception e) {
			log.error("Error getting genes for chromosome "+chromosome,e);
			throw new Exception("SQL error getting genes for chromosome "+chromosome);
		} finally {
		}
		
		return result;
    }
    public EnsemblGeneData getGeneData(String ensembl_id) throws Exception {
		String queryString = "SELECT  xref.display_label, gene.description, gene.seq_region_start,"+
		 					 "        gene.seq_region_end, gene.seq_region_strand, gene.biotype, seq_region.name"+
		 					 " FROM   gene, xref, seq_region"+
		 					 " WHERE  gene.display_xref_id=xref.xref_id"+
		 					 " AND	  gene.seq_region_id=seq_region.seq_region_id"+
		 					 " AND    seq_region.coord_system_id= :coord_system"+
		 					 " AND    gene.stable_id= :ensemblId";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("coord_system", ensemblCoordinateID);
		parameterMap.put("ensemblId", ensembl_id);

		EnsemblGeneData result = null;
		try {
			log.debug("getting gene info for "+ensembl_id);
			
        	SqlRowSet rs = super.queryForRowSet(queryString, parameterMap) ;
			if (!rs.next()) throw new Exception("Could not gene info for "+ensembl_id);
			result = new EnsemblGeneData();
			result.setEnsemblID(ensembl_id);
			result.setLabel(rs.getString(1));
			result.setDescription(rs.getString(2));
			result.setStartPos(rs.getLong(3));
			result.setEndPos(rs.getLong(4));
			result.setStrand(rs.getInt(5));
			result.setType(rs.getString(6));
			result.setChromosome(rs.getString(7));
			if (rs.next()) throw new Exception("Multiple results for gene "+ensembl_id);
			log.debug("success");
		}	
		catch (Exception me) {
			log.error("Error getting "+ensembl_id,me);
			throw new Exception("SQL error while getting " + ensembl_id);
		}
		return result;
    }
    public EnsemblGeneData getGeneDataBySymbol(String symbol) throws Exception {
		String queryString = "SELECT  xref.display_label, gene.description, gene.seq_region_start,"+
		 					 "        gene.seq_region_end, gene.seq_region_strand, gene.biotype, seq_region.name"+
		 					 " FROM   gene, xref, seq_region, external_db"+
		 					 " WHERE  gene.display_xref_id=xref.xref_id"+
		 					 " AND	  gene.seq_region_id=seq_region.seq_region_id"+
		 					 " AND    seq_region.coord_system_id= :coord_system"+
		 					 " AND    seq_region.name in ('1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', " +
		 					 "							  '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', 'X', 'Y', 'MT')" +
		 					 " AND    xref.external_db_id = external_db.external_db_id"+
		 					 " AND    external_db.db_name = 'HGNC_curated_gene'"+ 
		 					 " AND    xref.display_label = :symbol";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("coord_system", ensemblCoordinateID);
		parameterMap.put("symbol", symbol);

		EnsemblGeneData result = null;
		try {
			log.debug("getting gene info for "+symbol);
        	SqlRowSet rs = super.queryForRowSet(queryString, parameterMap) ;
			if (!rs.next()) throw new Exception("Could not find ENSEMBL gene info for "+symbol);
			result = new EnsemblGeneData();
			result.setLabel(rs.getString(1));
			result.setDescription(rs.getString(2));
			result.setStartPos(rs.getLong(3));
			result.setEndPos(rs.getLong(4));
			result.setStrand(rs.getInt(5));
			result.setType(rs.getString(6));
			result.setChromosome(rs.getString(7));
			if (rs.next()) throw new Exception("Multiple ENSEMBL results for gene "+symbol);
			log.debug("success");
		}	
		catch (Exception me) {
			log.error("Error getting "+symbol,me);
			throw new Exception("SQL error while getting " + symbol);
		}
		return result;
    }
    
	public int getEnsemblCoordinateID() {
		return ensemblCoordinateID;
	}

	public void setEnsemblCoordinateID(int ensemblCoordinateID) {
		this.ensemblCoordinateID = ensemblCoordinateID;
	}

	public int getEnsemblCpGAnalysisID() {
		return ensemblCpGAnalysisID;
	}

	public void setEnsemblCpGAnalysisID(int ensemblCpGAnalysisID) {
		this.ensemblCpGAnalysisID = ensemblCpGAnalysisID;
	}

	public String getGenomicSequenceTable() {
		return genomicSequenceTable;
	}

	public void setGenomicSequenceTable(String genomicSequenceTable) {
		this.genomicSequenceTable = genomicSequenceTable;
	}

	public int getSpeciesId() {
		return speciesId;
	}

	public void setSpeciesId(int speciesId) {
		this.speciesId = speciesId;
	}
	
	
	public AssemblyEngine getAssemblyEngine() {
		return assemblyEngine;
	}

	public void setAssemblyEngine(AssemblyEngine assemblyEngine) {
		this.assemblyEngine = assemblyEngine;
	}

	public Gene getGeneByIdFast(String ensembl_id, Integer minDetailWitdh) throws Exception {
		String queryString = "" +
		" SELECT xref.display_label, gene.stable_id, gene.description, gene.seq_region_start,"+ //1 2 3 4
		" 		 gene.seq_region_end, gene.seq_region_strand, gene.biotype, seq_region.name," + //5 6 7 8
		"		 transcript.stable_id, transcript.seq_region_start, transcript.seq_region_end," + //9 10 11
		"		 transcript.biotype, transcript.status," + //12 13
		"		 translation.stable_id, translation.seq_start, translation.start_exon_id = exon.exon_id as translation_starts," + //14 15 16
		"		 translation.seq_end, translation.end_exon_id = exon.exon_id as translation_ends," + // 17 18
		"		 exon.stable_id, exon.seq_region_start, exon.seq_region_end, exon_transcript.rank"+ //19 20 21 22
		" FROM	 gene, xref, seq_region, exon, exon_transcript, transcript LEFT JOIN translation ON translation.transcript_id = transcript.transcript_id" +
		" WHERE	 gene.stable_id = :geneId" +
		" AND	 gene.seq_region_id=seq_region.seq_region_id"+
		" AND	 gene.display_xref_id=xref.xref_id"+
		" AND	 gene.gene_id=transcript.gene_id" +
		" AND	 exon_transcript.exon_id = exon.exon_id" +
		" AND	 exon_transcript.transcript_id = transcript.transcript_id" +
		" AND	 transcript.gene_id = gene.gene_id" +
		" ORDER BY gene.stable_id, transcript.stable_id, exon_transcript.rank";
		final Map<String, Transcript> transcriptMap = new HashMap<String, Transcript>();
		final Map<String, Exon> exonMap = new HashMap<String, Exon>();
		Gene gene = super.query(queryString, Collections.singletonMap("geneId", ensembl_id), new ResultSetExtractor<Gene>() {
			@Override
			public Gene extractData(ResultSet rs) throws SQLException, DataAccessException {
				Gene gene = null;
				while(rs.next()) {
					String chrom = rs.getString(8);
					Boolean strand = rs.getInt(6) == 1;
					if (gene == null) {
						gene = new Gene();
						String geneId = rs.getString(2);
						gene.setId(geneId);
						gene.setName(rs.getString(1));
						gene.setLoc(new LinkedList<Location>());
						Location loc = new Location();
						loc.setChr(chrom);
						loc.setStart(rs.getLong(4));
						loc.setEnd(rs.getLong(5));
						loc.setStrand(strand);
						gene.getLoc().add(loc);
						gene.setDescription(rs.getString(3));
						gene.setTranscripts(new LinkedList<Transcript>());
					}
					Transcript transcript = null;
					Exon exon = null;
					String transcriptId = rs.getString(9);
					String exonId = rs.getString(19);
					if (transcriptMap.containsKey(transcriptId)) {
						transcript = transcriptMap.get(transcriptId);
					} else {
						transcript = new Transcript();
						transcript.setId(transcriptId);
						transcript.setName(transcriptId);
						transcript.setLoc(new LinkedList<Location>());
						Location tloc = new Location();
						tloc.setChr(chrom);
						tloc.setStart(rs.getLong(10));
						tloc.setEnd(rs.getLong(11));
						tloc.setStrand(strand);
						transcript.getLoc().add(tloc);
						transcript.setParent(gene);
						transcript.setExons(new LinkedList<Exon>());
						transcriptMap.put(transcriptId, transcript);
					}
					if (!gene.getTranscripts().contains(transcript)) gene.getTranscripts().add(transcript);
					if (exonMap.containsKey(exonId)) {
						exon = exonMap.get(exonId);
					} else {
						exon = new Exon();
						exon.setId(exonId);
						exon.setLoc(new LinkedList<Location>());
						Location eloc = new Location();
						eloc.setChr(chrom);
						eloc.setStart(rs.getLong(20));
						eloc.setEnd(rs.getLong(21));
						eloc.setStrand(strand);
						exon.setParent(transcript);
						exon.getLoc().add(eloc);
						exonMap.put(exonId, exon);
					}
					if (!transcript.getExons().contains(exon)) transcript.getExons().add(exon);
					if (rs.getBoolean(16)) { //returns false if null -> ok
						if (strand)
							transcript.setTranslationStart(exon.getStart() + rs.getLong(15) - 1);
						else
							transcript.setTranslationStart(exon.getEnd() - rs.getLong(15) + 1);
					}
					if (rs.getBoolean(18)) {
						if (strand)
							transcript.setTranslationEnd(exon.getStart() + rs.getLong(17) - 1);
						else
							transcript.setTranslationEnd(exon.getEnd() - rs.getLong(17) + 1);
					}
				}
				return gene;
			}
		});
		fetchSequence(gene);
		return gene;
	}

	///////////
	//public Gene getGeneById(String ensembl_id, ZoomLevel zoomLevel) throws Exception {
	public Gene getGeneById(String ensembl_id, Integer minDetailWitdh) throws Exception {
		
			Gene gene = new Gene();
			EnsemblGeneData geneEnsembl = null;
			geneEnsembl = getGeneData(ensembl_id);
			gene.setId(ensembl_id);
			gene.setName(geneEnsembl.getLabel());
			gene.setDescription(geneEnsembl.getDescription());
			gene.getLoc().add(new Location(geneEnsembl.getStartPos(),geneEnsembl.getEndPos(),geneEnsembl.getChromosome(),geneEnsembl.getStrand()==1));
			if (gene.getLoc().get(0).getLength() >= minDetailWitdh) {
			//if (zoomLevel != ZoomLevel.OUTLINE) {
				Map<String, Exon> exonmap = new HashMap<String, Exon>();
				List<String> transcript_ids = getTranscriptIds(ensembl_id);
				List<Transcript> transcripts = new LinkedList<Transcript>();
				for (String transcript_id : transcript_ids) {
					Transcript newtrans = new Transcript();
					EnsemblTranslationData etrans = getTranslation(transcript_id,gene.getLoc().get(0).getStrand());
					//FIXME: find out why this is sometimes null
					if (etrans == null) continue;
					newtrans.setTranslationStart(etrans.getStartPos());
					newtrans.setTranslationEnd(etrans.getEndPos());
					newtrans.getLoc().add(new Location(etrans.getStartPos(),etrans.getEndPos(),gene.getLoc().get(0).getChr(),gene.getLoc().get(0).getStrand()));
 					newtrans.setScore(gene.getScore());
  					newtrans.setId(etrans.getEnsembl_id());
					List<EnsemblExonData> exonDataList = getExonData(transcript_id);
					List<Exon> exons = new LinkedList<Exon>();
					for (EnsemblExonData exonData : exonDataList) {
						String id = exonData.getEnsembl_id();
						Exon exon;
						if (!exonmap.containsKey(id)) {
							exon = new Exon();
							exon.getLoc().add(new Location(exonData.getStartPos(),exonData.getEndPos(),gene.getLoc().get(0).getChr(),gene.getLoc().get(0).getStrand()));
 							exon.setName(exonData.getEnsembl_id());
 							exon.setScore(gene.getScore());
 							
							exonmap.put(id, exon);
						} else {
							exon = exonmap.get(id);
						}
						exons.add(exon);
					}
					newtrans.setExons(exons);
					transcripts.add(newtrans);
				}
				gene.setTranscripts(transcripts);
			}
			return gene;
			
	}
	////////////
	
	public List<String> getGOTermsByTranscriptID(String transcript_id) {
//		select transcript.stable_id from transcript,gene where transcript.gene_id = gene.gene_id and gene.stable_id = 'ENSG00000171862' and transcript.biotype = 'protein_coding';
		String queryString = "SELECT DISTINCT xref.display_label " +
							 "FROM xref,object_xref,external_db,transcript,translation " +
							 "WHERE (" +
							 	"translation.transcript_id = transcript.transcript_id and " +
							 	"object_xref.ensembl_id = translation.translation_id and " +
							 	"object_xref.xref_id = xref.xref_id and " +
							 	"xref.external_db_id=external_db.external_db_id and " +
							 	"external_db.db_name='GO' and " +
							 	"transcript.stable_id = :transcriptId)";
		List<String> result = query(queryString, Collections.singletonMap("transcriptId", transcript_id), new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});
		return result;
	}
	
}
