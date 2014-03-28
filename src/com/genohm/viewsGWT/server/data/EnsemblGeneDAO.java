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

import java.util.List;


import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.util.Version;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import com.genohm.viewsGWT.shared.data.ensembl.EnsemblGene;

public class EnsemblGeneDAO {
	
	private static Logger log = Logger.getLogger(EnsemblGeneDAO.class);
	//set by spring
	private SessionFactory sessionFactory;
	
	public void initializeIndexes(String chr) {
		Session session = sessionFactory.openSession();
		//Then open a Hibernate Search session
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		//Begin your search transaction
		SQLQuery sqlQuery = session.createSQLQuery("select gene.gene_id from gene,seq_region,coord_system where seq_region.coord_system_id = coord_system.coord_system_id and seq_region.name = '"+ chr +"' and coord_system.name = 'chromosome' and gene.seq_region_id = seq_region.seq_region_id");
		List<Integer> ids = sqlQuery.list();
		Transaction tx = fullTextSession.beginTransaction();
		for (Integer id : ids) {
			EnsemblGene gene = (EnsemblGene) session.get(EnsemblGene.class, id);
			log.debug("Indexing "+gene.getEnsemblId());
			fullTextSession.index(gene);
			tx.commit();
			tx = session.beginTransaction();
		}
		session.close();
	}
	
	public List<EnsemblGene> findGenesByFullTextSearch(String queryString) throws Exception {
		Session session = null;
		List<EnsemblGene> result = null;
		try {
			session = sessionFactory.openSession();
			//Then open a Hibernate Search session
			FullTextSession fullTextSession = Search.getFullTextSession(session);
			MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_35,	new String[] {"description","ensemblId","displayXref.displayLabel","ensemblXrefs.displayLabel"}, new StandardAnalyzer(Version.LUCENE_35));
			parser.setAllowLeadingWildcard(true);
			Query query = fullTextSession.createFullTextQuery(parser.parse(queryString), EnsemblGene.class);
			result = query.list();
		} catch (Throwable t) {
			Exception e = new Exception("Could not perform full text search");
			e.setStackTrace(t.getStackTrace());
			log.error(e);
			throw e;
		} finally {
			if (session != null) session.close();
		}
		return result;
	}
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	
	
}
