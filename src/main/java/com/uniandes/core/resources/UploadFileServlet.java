package com.uniandes.core.resources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import org.json.simple.JSONArray;
//
//import ejb.TareasFacade;
//import entities.Tareas;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.ResultIterator;
import org.skife.jdbi.v2.StatementContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniandes.common.Utility;
import com.uniandes.db.dao.UsuarioDAO;
import com.uniandes.db.vo.Tbl_campo;
import com.uniandes.db.vo.Tbl_campo_usuario;
import com.uniandes.db.vo.Tbl_tramite;
import com.uniandes.db.vo.Tbl_tramite_usuario;
import com.uniandes.db.vo.Tbl_usuario;

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

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
         
        InputStream inputStream = null; // input stream of the upload file
    	Handle h = Utility.dao.open();
        Connection conn = null; // connection to the database
        String message = null;  // message will be sent back to client
        conn = h.getConnection();
        
        
        List<Tbl_campo> dataListIdCampos = new ArrayList<Tbl_campo>();
        Tbl_tramite_usuario tbl_tramite_usuario = new Tbl_tramite_usuario();
        
        try {        	
        	//Sacamos el json principal
			JSONObject tramiteJSON = new JSONObject(request.getParameter("informacion"));
			//Sacamos el json que contiene los campos
			JSONObject camposJSON = new JSONObject((tramiteJSON.get("campos")).toString().replace("\"\"", "--"));
			String queryCampos = "select * from tbl_campo  where estado = true and id_tramite  = " + tramiteJSON.getString("idtramite");
			
			PreparedStatement st = conn.prepareStatement(queryCampos);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				Tbl_campo campo = new Tbl_campo();
				campo.setId_campo(rs.getLong("id_campo"));
				campo.setNombre(rs.getString("nombre"));
				campo.setTipo(rs.getString("tipo"));
				
				dataListIdCampos.add(campo);
			}
			
			//Traemos la informacion del tramite
			String queryTramite = "SELECT * FROM tbl_tramite where id_tramite = " + tramiteJSON.getString("idtramite");
			st = conn.prepareStatement(queryTramite);
			rs = st.executeQuery();
			while (rs.next()) {
				tbl_tramite_usuario = new Tbl_tramite_usuario();
				tbl_tramite_usuario.setId_tramite(new Long(tramiteJSON.getString("idtramite")));
				tbl_tramite_usuario.setDescripcion(rs.getString("descripcion"));
				tbl_tramite_usuario.setEstado(0);
				tbl_tramite_usuario.setFecha(new Date());
				tbl_tramite_usuario.setId_usuario_ciudadano(tramiteJSON.getString("idusuario"));
				tbl_tramite_usuario.setNombre(rs.getString("nombre"));
			}
			
			
			//Colocamos el responsable de la solicitud
			String queryResponsable = "select count(1), t.idusuario usuarioid from tbl_usuario t  "
					+ "left join tbl_tramite_usuario tu on t.idusuario =  tu.id_usuario_funcionario and tu.estado not in (2,3) "
					+ "where t.tipo = '2'  "
					+ "group by t.idusuario "
					+ "order by 1 "
					+ "limit 1";
			st = conn.prepareStatement(queryResponsable);
			rs = st.executeQuery();
			while (rs.next()) {
				tbl_tramite_usuario.setId_usuario_funcionario(rs.getString("usuarioid"));
			}
			
			//Creamos el tramite del usuario
			String createTramite = "INSERT INTO tbl_tramite_usuario (id_tramite, nombre, descripcion, estado, id_usuario_ciudadano, fecha, id_usuario_funcionario)"
					+ " VALUES(?,?,?,?,?,?,?)";
			st = conn.prepareStatement(createTramite);
			st.setLong(1, tbl_tramite_usuario.getId_tramite());
			st.setString(2, tbl_tramite_usuario.getNombre());
			st.setString(3, tbl_tramite_usuario.getDescripcion());
			st.setInt(4, tbl_tramite_usuario.getEstado());
			st.setString(5, tbl_tramite_usuario.getId_usuario_ciudadano());
			st.setDate(6, new java.sql.Date(new Date().getTime()));
			st.setString(7, tbl_tramite_usuario.getId_usuario_funcionario());
			
			int rowsInserted = st.executeUpdate();

			//Sacamos el ultimo tramite creato
			String lastramite = "SELECT id_tramite_usuario from tbl_tramite_usuario "
					+ "where id_usuario_ciudadano = '" + tbl_tramite_usuario.getId_usuario_ciudadano() + "' "
					+ "order by 1 desc limit 1";
			st = conn.prepareStatement(lastramite);
			rs = st.executeQuery();
			while (rs.next()) {
				tbl_tramite_usuario.setId_tramite_usuario(rs.getLong("id_tramite_usuario"));
			}
			
			//Ahora vamos a crear los campos_usuario del tramite_usuario
			for(Tbl_campo campo : dataListIdCampos){
				Tbl_campo_usuario campo_usuario = new Tbl_campo_usuario();
				campo_usuario.setId_campo(campo.getId_campo());
				campo_usuario.setId_tramite_usuario(tbl_tramite_usuario.getId_tramite_usuario());
				campo_usuario.setNombre(campo.getNombre());
				campo_usuario.setTipo(campo.getTipo());
				if(campo.getTipo().equals("texto")){
					campo_usuario.setValortexto(camposJSON.get(campo.getId_campo().toString()).toString());
					String queryCrearCampoUsuario = "INSERT INTO tbl_campo_usuario (id_campo, id_tramite_usuario, nombre, tipo, valortexto) "
							+ "VALUES(?,?,?,?,?)";
					st = conn.prepareStatement(queryCrearCampoUsuario);
					st.setLong(1, campo_usuario.getId_campo());
					st.setLong(2, campo_usuario.getId_tramite_usuario());
					st.setString(3, campo_usuario.getNombre());
					st.setString(4, campo_usuario.getTipo());
					st.setString(5, campo_usuario.getValortexto());
					st.executeUpdate();
				}else{
					String queryCrearCampoUsuarioArchivo = "INSERT INTO tbl_campo_usuario (id_campo, id_tramite_usuario, nombre, tipo) "
							+ "VALUES(?,?,?,?)";
					st = conn.prepareStatement(queryCrearCampoUsuarioArchivo);
					st.setLong(1, campo_usuario.getId_campo());
					st.setLong(2, campo_usuario.getId_tramite_usuario());
					st.setString(3, campo_usuario.getNombre());
					st.setString(4, campo_usuario.getTipo());
					st.executeUpdate();
					
					//Sacamos el ultimo campo insertado
					String queryLasCampoUsuario = "SELECT id_campo_usuario FROM tbl_campo_usuario "
							+ "WHERE id_tramite_usuario = " + campo_usuario.getId_tramite_usuario() +   " order by 1 desc LIMIT 1 ";
					
					st = conn.prepareStatement(queryLasCampoUsuario);
					rs = st.executeQuery();
					
					while(rs.next()){
						campo_usuario.setId_campo_usuario(rs.getLong("id_campo_usuario"));
					}
					Collection<Part> datalistFilepart = request.getParts();
					Part filePart = null;
					for(Part p:datalistFilepart){
						if(p.getName().equals(campo_usuario.getId_campo().toString())){
							filePart = p;
							break;
						}
					}
					
					inputStream = filePart.getInputStream();
					
					try {
						// constructs SQL statement
						String sql = "INSERT INTO tbl_archivo (archivo, nombre, id_campo_usuario) values (?,?,?)";
						PreparedStatement statement = conn.prepareStatement(sql);
						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						int nRead;
						byte[] data = new byte[16384];
						while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
							buffer.write(data, 0, nRead);
						}

						if (inputStream != null) {
							// fetches input stream of the upload file for the
							// blob column
							statement.setBytes(1, buffer.toByteArray());
							statement.setString(2, filePart.getSubmittedFileName());
							statement.setLong(3, campo_usuario.getId_campo_usuario());
						}

						// sends the statement to the database server
						int row = statement.executeUpdate();
					} catch (Exception ex) {
						message = "ERROR: " + ex.getMessage();
						ex.printStackTrace();
						message = "File uploaded and saved into database";
						JSONObject outPUT = new JSONObject();
						try {
							outPUT.put("status", "error");
							outPUT.put("errormessage", "Error cargando el archivo");
							outPUT.put("message", "No se ha cargado exitosamente el archivo");
							response.setContentType("application/json");
							response.setStatus(500);
							PrintWriter out = response.getWriter();
							out.println(outPUT);
						} catch (Exception e) {
							///
						}
						String result = "" + outPUT;
						Response.status(500).entity(result).build();
					}
					
					//Scamos el ultimo archivo insertado con este campo
					String lastArchivoCampoUsuario =  "SELECT idarchivo FROM tbl_archivo WHERE id_campo_usuario = " + campo_usuario.getId_campo_usuario()
												   +"  ORDER BY 1 DESC LIMIT 1";
					st = conn.prepareStatement(lastArchivoCampoUsuario);
					rs = st.executeQuery();
					
					while(rs.next()){
						Long idArchivo = rs.getLong("idarchivo");
						campo_usuario.setValorarchivo(idArchivo.toString());
					}
					
					String updateCampoArchivo = "UPDATE tbl_campo_usuario set valorarchivo = " + campo_usuario.getValorarchivo() 
											  +" WHERE id_campo_usuario = " + campo_usuario.getId_campo_usuario();
					st = conn.prepareStatement(updateCampoArchivo);
					st.executeUpdate();	
				}
			}
			
			message = "File uploaded and saved into database";
			JSONObject outPUT = new JSONObject();
			outPUT.put("status", "ok");
			outPUT.put("errormessage", "");
			outPUT.put("message", "Se ha cargado actualizado exitosamente la informacion");
			String result = "" + outPUT;
			response.setContentType("application/json");
			response.setStatus(200);
			PrintWriter out = response.getWriter();
			out.println(outPUT);
		} catch (JSONException | SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			JSONObject outPUT = new JSONObject();
			try {
				outPUT.put("status", "error");
				outPUT.put("errormessage", "Error cargando el archivo");
				outPUT.put("message", "No se ha cargado exitosamente el archivo");
				response.setContentType("application/json");
				response.setStatus(500);
				PrintWriter out = response.getWriter();
				out.println(outPUT);
			} catch (Exception e) {
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