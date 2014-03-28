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
package com.genohm.viewsGWT.tools.initializer;

import java.util.List;

import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.genohm.viewsGWT.shared.data.FeatureDatasource;
import com.genohm.viewsGWT.shared.data.GFFDatasource;
import com.genohm.viewsGWT.shared.data.RefSeqDatasource;


public class FeatureDatasourceLoader extends HibernateTemplate {
	
	@Transactional
	public void loadGenericSources() {
//		GFFDatasource ds1 = new GFFDatasource();
//		ds1.setName("Example non filterable GFF");
//		ds1.setFilePath("transcripts.gff3");
//		ds1.setDescription("Example gff datasource representing a crazy transcript");
//		ds1.setCanBeFiltered(false);
//		ds1.setIsPublic(true);
//		ds1.setOwner("martijn");
//		merge(ds1);
//		RefSeqDatasource ds2 = new RefSeqDatasource();
//		ds2.setName("Refseq");
//		ds2.setAssemblyName("GRCh37");
//		ds2.setDescription("Reference assembly GRCh37");
//		ds2.setCanBeFiltered(false);
//		ds2.setIsPublic(true);
//		ds2.setOwner("martijn");
//		merge(ds2);
//		GFFDatasource ds3 = new GFFDatasource();
//		ds3.setName("Example filterable GFF");
//		ds3.setFilePath("/ontologies/transcripts.gff3");
//		ds3.setDescription("Example gff datasource representing a crazy transcript");
//		ds3.setCanBeFiltered(true);
//		ds3.setIsPublic(true);
//		ds3.setOwner("martijn");
//		merge(ds3);
//		FeatureDatasource example = new FeatureDatasource();
//		example.setIsPublic(true);
//		List<? extends FeatureDatasource> result = findByExample(example);
//		result.size();
	}
	
	public static void main(String[] args) {
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("WEB-INF/spring-service.xml");
		FeatureDatasourceLoader dsl = (FeatureDatasourceLoader) context.getBean("featureDatasourceLoaderBean");
		dsl.loadGenericSources();
	}
}
