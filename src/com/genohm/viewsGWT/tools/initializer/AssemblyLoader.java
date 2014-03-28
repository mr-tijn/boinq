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
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openjena.atlas.logging.Log;
import org.quartz.SchedulerException;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.genohm.viewsGWT.server.analysis.ComputationEngine;

public class AssemblyLoader extends JdbcTemplate {
	//private String version;
	private String filename;
	private String chromosome;
	private String assembly;

	public AssemblyLoader() {
	}

	private void loadAssembly() {
		try {
			int chunklength = getChunkLength();
			String chunk = "";

			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			long startpos = 0;
			while ((line = br.readLine()) != null) {
				if (line.startsWith(">")) continue;
				int readpos = 0;
				while (readpos < line.length()) { // <= line.length() - 1
					int chunkspace = chunklength - chunk.length();
					int readspace = line.length() - readpos;
					if (readspace >= chunkspace) {
						// fill chunk and store
						chunk = chunk.concat(line.substring(readpos,readpos+chunkspace));
						
						super.update("INSERT INTO " + assembly + " VALUES (?,?,?)", new Object[]{Long.toString(startpos), chromosome, chunk});

						System.out.println("Inserting pos "+startpos+" of chromosome "+chromosome);
						chunk = "";
						startpos += chunklength;
						readpos += chunkspace;
					} else {
						// increase chunk
						chunk = chunk.concat(line.substring(readpos,readpos+readspace));
						readpos += readspace;
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error loading assembly");
			e.printStackTrace();
		} 
	}

	private int getChunkLength() {
		return super.query("SELECT chunksize FROM assembly_info WHERE assembly = ?", new Object[] {assembly}, new ResultSetExtractor<Integer>() {	
			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (!rs.next()) throw new SQLException("Cannot find chunksize entry for assembly "+assembly);
				Integer chunklength = rs.getInt(1);
				if (rs.next()) throw new SQLException("Multiple chunksize entries found for assembly "+assembly);
				return chunklength;
			}
		});
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}
	
	public String getAssembly() {
		return assembly;
	}

	public void setAssembly(String assembly) {
		this.assembly = assembly;
	}

	public static void main(String[] args) {
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("WEB-INF/spring-service.xml");
		AssemblyLoader al = (AssemblyLoader) context.getBean("assemblyLoaderBean");
		if (args.length < 3) System.err.println("Expecting following arguments: assembly, chromosome, filename");
		al.setAssembly(args[0]);
		al.setChromosome(args[1]);
		al.setFilename(args[2]);
		al.loadAssembly();
		//shutdown quartz manually
		ComputationEngine ce = (ComputationEngine) context.getBean("quartzEngine");
		try {
			ce.stop();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
}