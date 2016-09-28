package com.uniandes.core.resources;

import java.sql.SQLException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.ResultIterator;

import com.uniandes.db.dao.Tbl_TramiteDAO;
import com.uniandes.db.vo.Tbl_tramite;

import io.dropwizard.hibernate.UnitOfWork;

@Path("/procedureResource")
@Produces({ MediaType.APPLICATION_JSON })
public class ProcedureResource {

	private Tbl_TramiteDAO tbl_tramiteDAO;

	public ProcedureResource(Tbl_TramiteDAO tbl_tramiteDAO) {
		this.tbl_tramiteDAO = tbl_tramiteDAO;
	}

	// Servicio para registrar un procedimiento
	@POST
	@Path("createProcedure")
	@UnitOfWork
	public Response createProcedure(String incomingData) throws JSONException, SQLException {
		// Json de entrada
		JSONObject inPUT = new JSONObject(incomingData);
		JSONObject tramiteJSON = inPUT.getJSONObject("tramites");
		
		//Creamos un objeto tipo tramite
		Tbl_tramite tbl_tramite = new Tbl_tramite();
		tbl_tramite.setNombre(tramiteJSON.getString("nombre"));
		tbl_tramite.setDescripcion(tramiteJSON.getString("descripcion"));
		
		//Creamos el nuevo objeto
		Long idTramite = tbl_tramiteDAO.create(tbl_tramite);
		
		//Creamos la fase del tramite
		
		
		
		
//		try {
//			String nombre = inPUT.getString("nombre");
//			String cedula = inPUT.getString("cedula");
//			String telefono = inPUT.getString("telefono");
//			String email = inPUT.getString("email");
//			String tipo = inPUT.getString("tipo");
//			String password = inPUT.getString("password");
//
//			// Validamos que el usuario no haya sido creado
//			String query = "SELECT * FROM tbl_usuario WHERE cedula = '" + cedula + "'";
//			ResultIterator tareasIterator = h.createQuery(query).iterator();
//			
//		} catch (Exception e) {
//			outPUT.put("status", "false");
//			outPUT.put("message", "Ha ocurrido un error al insertar el usuario");
//			String result = "" + outPUT;
//			return Response.status(500).entity(result).build();
//		} finally {
//			h.close();
//		}
		String result = "";
		return Response.status(200).entity(result).build();
	}
}
