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

import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;


@SuppressWarnings("unused")
public class AssemblyEngine extends JdbcTemplate {
	private String assemblyName;
	private int chunkSize;

	public AssemblyEngine() {
	}

	public void init() throws Exception {
		SqlRowSet rs = super.queryForRowSet(
				"SELECT chunksize FROM assembly_info WHERE assembly=?",
				new Object[] { assemblyName });
		// System.out.println(rs.)
		if (!rs.next())
			throw new Exception("Could not get chunksize for "
					+ assemblyName);
		chunkSize = rs.getInt(1);
		if (rs.next())
			throw new Exception("Multiple chunksize results found for "
					+ assemblyName);
	}
	
	public String getSeq(String chr, long startpos, long endpos)
			throws Exception {
		if (startpos > endpos)
			throw new Exception("Startpos should be <= endpos");
		else if (startpos < 1)
			//FIXME
			//throw new Exception("Startpos should be >= 1");
			startpos = 1;
		// positions in database are 0-based
		startpos--; endpos--;
		long startchunk = startpos - startpos % chunkSize;
		long endchunk = endpos - endpos % chunkSize;
		String result = "";

		try {

			SqlRowSet rs = super
					.queryForRowSet(
							"SELECT * FROM "+ assemblyName + " WHERE (start >= ?) AND (start <= ?) AND (chr = ?)",
							new Object[] { startchunk, endchunk,
									chr });
			while (rs.next()) {
				result = result + rs.getString(3);
			}
			result = result.substring((int) (startpos - startchunk),
					(int) (endpos - startchunk + 1));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return result;
	}

	public String getAssemblyName() {
		return assemblyName;
	}

	public void setAssemblyName(String assemblyName) {
		this.assemblyName = assemblyName;
	}
	
	public static void main(String[] args) {
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("WEB-INF/spring-service.xml");
		AssemblyEngine as = (AssemblyEngine) context.getBean("assemblyEngineHuman");
		as.test();
	}
	private void test() {
		try {
			System.out.println(getSeq("1", 1, 20));
			System.out.println(getSeq("1", 847, 853));
			System.out.println(getSeq("1", 847, 847));
			System.out.println(getSeq("1", 848, 848));
			System.out.println(getSeq("1", 849, 849));
			System.out.println(getSeq("1",15050,15099));
			System.out.println(getSeq("1",9950,10049));
			System.out.println(getSeq("1",20134556,20137899));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
