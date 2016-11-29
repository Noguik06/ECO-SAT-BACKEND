package com.uniandes.core.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

//import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
//
//import ejb.TareasFacade;
//import entities.Tareas;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.ResultIterator;
import org.skife.jdbi.v2.StatementContext;

import com.uniandes.common.Utility;



//@WebServlet("/DownloadFileServlet")
public class DownloadFileServlet extends HttpServlet{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// size of byte buffer to send file
    private static final int BUFFER_SIZE = 4096; 
	
	protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
		try{
			
			String idarchivo = request.getParameter("idarchivo");
			String query = "select archivo, nombre from tbl_archivo "
					+ "where idarchivo = " + idarchivo;
			Handle h = Utility.dao.open();
			Query<Map<String, Object>> object = h.createQuery(query);
			if(object == null || object.first() == null || object.first().get("archivo") == null){
				response.setContentType("text/html");
			    PrintWriter out = response.getWriter();
			    out.println("<html>");
			    out.println("<head>");
			    out.println("<title>Hola</title>");
			    out.println("</head>");
			    out.println("<body bgcolor=\"white\">");
			    out.println("<p>Esta tarea no existe o no tiene nigun adjunto </p>");
			    out.println("</body>");
			    out.println("</html>");
			}else{
				String fileName = object.first().get("nombre").toString();
				InputStream inputStream = new ByteArrayInputStream((byte[]) object.first().get("archivo"));
				int fileLength = inputStream.available();
		        System.out.println("fileLength = " + fileLength);
		        ServletContext context = getServletContext();
		        // sets MIME type for the file download
		        String mimeType = context.getMimeType(fileName);
		        if (mimeType == null) {        
		            mimeType = "application/octet-stream";
		        }              
		        // set content properties and header attributes for the response
		        response.setContentType(mimeType);
		        response.setContentLength(fileLength);
		        String headerKey = "Content-Disposition";
		        String headerValue = String.format("attachment; filename=\"%s\"", fileName);
		        response.setHeader(headerKey, headerValue);
		        // writes the file to the client
		        OutputStream outStream = response.getOutputStream();
		        byte[] buffer = new byte[BUFFER_SIZE];
		        int bytesRead = -1;
		        while ((bytesRead = inputStream.read(buffer)) != -1) {
		            outStream.write(buffer, 0, bytesRead);
		        }
		        inputStream.close();
		        outStream.close();
			}
			h.close();
		}catch(Exception e){
			response.setContentType("text/html");
		    PrintWriter out = response.getWriter();
		    out.println("<html>");
		    out.println("<head>");
		    out.println("<title>Hola</title>");
		    out.println("</head>");
		    out.println("<body bgcolor=\"white\">");
		    out.println("<p>Error descargando la tarea </p>");
		    out.println("</body>");
		    out.println("</html>");
		}
		
		
		
    }
}
