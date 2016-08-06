package com.uniandes.experimento.resources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

//import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;
//
//import ejb.TareasFacade;
//import entities.Tareas;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.ResultIterator;
import org.skife.jdbi.v2.StatementContext;

import com.uniandes.common.Utility;

//import com.example.com.example.common.Utility;

//@WebServlet("/fileResource/uploadFileServlet")
public class UploadFileServlet extends HttpServlet {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	// size of byte buffer to send file
	private static final int BUFFER_SIZE = 4096;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

         
        InputStream inputStream = null; // input stream of the upload file
        System.out.println(request.getContentType());
        // obtains the upload file part in this multipart request
        Part filePart = request.getPart("fileName");
        if (filePart != null) {
            // prints out some information for debugging
            System.out.println(filePart.getName());
            System.out.println(filePart.getSize());
            System.out.println(filePart.getContentType());
             
            // obtains input stream of the upload file
            inputStream = filePart.getInputStream();
        }
         
        Connection conn = null; // connection to the database
        String message = null;  // message will be sent back to client
         
        try {
            // connects to the database
        	Handle h = Utility.dao.open();
        	
            conn = h.getConnection();
 
            // constructs SQL statement
            String sql = "INSERT INTO archivos (archivo, nombre) values (?,?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            	  buffer.write(data, 0, nRead);
            }
            
            if (inputStream != null) {
                // fetches input stream of the upload file for the blob column
                statement.setBytes(1, buffer.toByteArray());
                statement.setString(2, filePart.getSubmittedFileName());
            }
 
            // sends the statement to the database server
            int row = statement.executeUpdate();
            if (row > 0) {
                message = "File uploaded and saved into database";
                JSONObject outPUT = new JSONObject();
                outPUT.put("status","ok");
                outPUT.put("errormessage", "");
                outPUT.put("message","Se ha cargado exitosamente el archivo");
        		String result = "" + outPUT;
        		response.setContentType("application/json");
        		response.setStatus(200);
				PrintWriter out = response.getWriter();
				out.println(outPUT);
            }
        } catch (SQLException | JSONException ex) {
            message = "ERROR: " + ex.getMessage();
            ex.printStackTrace();
            message = "File uploaded and saved into database";
            JSONObject outPUT = new JSONObject();
            try{
	            outPUT.put("status","error");
	            outPUT.put("errormessage", "Error cargando el archivo");
	            outPUT.put("message","No se ha cargado exitosamente el archivo");
	            response.setContentType("application/json");
	            response.setStatus(500);
				PrintWriter out = response.getWriter();
				out.println(outPUT);
    		}catch(Exception e){
    			///
    		}
    		String result = "" + outPUT;
    		Response.status(500).entity(result).build();
        } catch (Exception e) {
        	message = "File uploaded and saved into database";
            JSONObject outPUT = new JSONObject();
            try{
	            outPUT.put("status","error");
	            outPUT.put("errormessage", "Error cargando el archivo");
	            outPUT.put("message","No se ha cargado exitosamente el archivo");
	            response.setContentType("application/json");
	            response.setStatus(500);
				PrintWriter out = response.getWriter();
				out.println(outPUT);
            }catch(Exception e1){
            	///
            }
    		String result = "" + outPUT;
    		Response.status(500).entity(result).build();
		}finally {
            if (conn != null) {
                // closes the database connection
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
	}
}