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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.springframework.transaction.annotation.Transactional;

import com.genohm.viewsGWT.server.data.ViewsDao;


public class FileUploadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -486645069210249610L;
	
	protected String basePath = "/tmp";
	protected ViewsDao viewsDao = null;
	
	@Transactional
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    try {
	    	List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req);
	    	for (FileItem item: items) {
		    	 if (item.isFormField()) {
		                // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
		                String fieldname = item.getFieldName();
		                String fieldvalue = item.getString();
		                // ... (do your job here)
		            } else {
		                // Process form file field (input type="file").
		                String fieldname = item.getFieldName();
		                String filename = FilenameUtils.getName(item.getName());
		                String fileExt = FilenameUtils.getExtension(item.getName());
		                InputStream filecontent = item.getInputStream();
		                if (fileExt.equalsIgnoreCase("gff3")) {
		                	// first upload
		                	String uuidString = UUID.randomUUID().toString();
		                	String targetPath = FilenameUtils.concat(uuidString.substring(0, 3), uuidString.substring(3));
		                	String fullPath = FilenameUtils.concat(basePath, targetPath);
		                	File targetFile = new File(fullPath);
		                	targetFile.getParentFile().mkdirs();
		                	targetFile.createNewFile();
		                	FileOutputStream out = new FileOutputStream(targetFile);
		                	byte[] buffer = new byte[4096];  
		                	int bytesRead;  
		                	while ((bytesRead = filecontent.read(buffer)) != -1) {  
		                	  out.write(buffer, 0, bytesRead);  
		                	}  
		                	filecontent.close();  
		                	out.close();  
//		                	GFFDatasource ds = new GFFDatasource();
//		                	ds.setOwner(viewsDao.getCurrentUser().getUsername());
//		                	ds.setFilePath(fullPath);
//		                	viewsDao.merge(ds);
		                }
		                String message = "This is currently not actually doing anything";
		        		resp.setContentType("text/html");
		    			resp.setHeader("Pragma", "No-cache");
		    			resp.setDateHeader("Expires", 0);
		    			resp.setHeader("Cache-Control", "no-cache");
		    			PrintWriter out = resp.getWriter();
		    			out.println("<html>");
		    			out.println("<body>");
		  				out.println("<script type=\"text/javascript\">");
		   				out.println("if (parent.uploadComplete) parent.uploadComplete('"+ message + "');");
		    			out.println("</script>");
		    			out.println("</body>");
		    			out.println("</html>");
		    			out.flush();
		            }
	    		
	    	}
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	
}
