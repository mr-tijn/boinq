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

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

/*
 * creates and accesses a featureindex for a gff file
 */

public class GFFIndexer {
	
	private static Logger log = Logger.getLogger(GFFIndexer.class);
	protected FeatureIndex lineIndex;
	protected String fileName;
	
	public GFFIndexer(String fileName) {
		setFileName(fileName);
		setLineIndex(new DerbyFeatureIndex(fileName));
	}
	
	public void generateIndex() {
		RandomAccessFile file = null;
		try {
			if (lineIndex.exists()) return;
			lineIndex.init();
			file = new RandomAccessFile(fileName, "r");
			long line = 1;
			long pos = 0;
			long linestart = pos;
			long start = 0;
			long end = 0;
			int field = 1;
			String fieldvalue = "";
			String seqid = fieldvalue;
			char chr;
			while (true) {
				try {
					chr = (char) file.readByte(); //file.readChar();
				} catch (EOFException e) {
					break;
				}
				switch (chr) {
				case ' ':
					break;
				case '\t':
					if (field == 1) {
						seqid = fieldvalue;
					}
					if (field == 4) {
						start = Long.parseLong(fieldvalue);
					}
					if (field == 5) {
						end = Long.parseLong(fieldvalue);
					}
					field++;
					fieldvalue = "";
					break;
				case '\n':
					storepos(line,linestart,start,end,seqid);
					//System.out.println("line "+line+" starts at "+linestart+" and describes feature from "+start+" to "+end);
					linestart = pos+1;
					line++;
					field = 1;
					fieldvalue = "";
					break;
				default:
					fieldvalue += chr;
				}
				pos++;
			}
		}
		catch (FileNotFoundException e) {
			log.error("Could not open file " + fileName,e);
		}
		catch (IOException e) {
			log.error("IO exception ", e);
		}
		catch (Exception e) {
			log.error("Exception ", e);
		}
		finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException e) {
					//swallow
				}
			}
		}
	}
	
	public String getlines(Long start, Long end, String chr) {
		String result = "";
		List<Long> positions = lineIndex.getPositionsBetween(start, end, chr);
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(fileName, "r");
			for (Long position: positions) {
				file.seek(position);
				result += file.readLine() + "\n";
			}
		} catch (Exception e) {
			log.error("Exception while getting lines");
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException e) {
					//swallow
				}
			}			
		}
		return result;
	}
	
	protected void storepos(long line, long linestart, long start, long end, String seqid) throws Exception {
		lineIndex.storePosition(linestart, start, end, seqid);
	}
	
	public void setLineIndex(FeatureIndex lineIndex) {
		this.lineIndex = lineIndex;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
	
}
