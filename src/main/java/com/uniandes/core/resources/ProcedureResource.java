package com.uniandes.core.resources;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.ResultIterator;

import com.uniandes.db.dao.Tbl_CampoDAO;
import com.uniandes.db.dao.Tbl_FaseDAO;
import com.uniandes.db.dao.Tbl_TramiteDAO;
import com.uniandes.db.dao.Tbl_Tramite_UsuarioDAO;
import com.uniandes.db.vo.Tbl_campo;
import com.uniandes.db.vo.Tbl_fase;
import com.uniandes.db.vo.Tbl_tramite;
import com.uniandes.db.vo.Tbl_tramite_usuario;

import io.dropwizard.hibernate.UnitOfWork;

@Path("/procedureResource")
@Produces({ MediaType.APPLICATION_JSON })
public class ProcedureResource {

	private Tbl_TramiteDAO tbl_tramiteDAO;
	private Tbl_FaseDAO tbl_FaseDAO;
	private Tbl_CampoDAO tbl_CampoDAO;
	private Tbl_Tramite_UsuarioDAO tbl_Tramite_UsuarioDAO;

	public ProcedureResource(Tbl_TramiteDAO tbl_tramiteDAO, 
			Tbl_FaseDAO tbl_FaseDAO, Tbl_CampoDAO tbl_CampoDAO, 
			Tbl_Tramite_UsuarioDAO tbl_Tramite_UsuarioDAO) {
		this.tbl_tramiteDAO = tbl_tramiteDAO;
		this.tbl_FaseDAO = tbl_FaseDAO;
		this.tbl_CampoDAO = tbl_CampoDAO;
		this.tbl_Tramite_UsuarioDAO = tbl_Tramite_UsuarioDAO;
	}

	// Servicio para crear un tramite
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
		JSONArray jsonArrayFases = tramiteJSON.getJSONArray("fases");
		for(int i = 0; i<jsonArrayFases.length(); i ++){
			//Sacamos la fase que vamos a crear
			JSONObject jsonFase = (JSONObject) jsonArrayFases.get(i);
			//Cremos el objeto fase
			Tbl_fase  tbl_fase = new Tbl_fase();
			tbl_fase.setOrden(jsonFase.getInt("orden"));
			tbl_fase.setTipousuario(jsonFase.getInt("tipousuario"));
			tbl_fase.setId_tramite(idTramite);
			//Guardamos la fase nueva
			Long idFase = tbl_FaseDAO.create(tbl_fase);
			
			//Creamos los campos de la fase
			JSONArray jsonArrayCampos = jsonFase.getJSONArray("campos");
			for(int j = 0; j<jsonArrayCampos.length(); j ++){
				//Obtenemos el json del campo
				JSONObject jsonCampo = (JSONObject) jsonArrayCampos.get(j);
				//Creamos el objeto tipo tbl_campo
				Tbl_campo tbl_campo = new Tbl_campo();
				tbl_campo.setNombre(jsonCampo.getString("nombre"));
				tbl_campo.setTipo(jsonCampo.getString("tipo"));
				tbl_campo.setOrden(jsonCampo.getInt("orden"));
				tbl_campo.setObligatorio(jsonCampo.getBoolean("obligatorio"));
				tbl_campo.setId_fase(idFase);
				//Persistimos el objeto
				Long idCampo = tbl_CampoDAO.create(tbl_campo);
			}
		}
		String result = "";
		return Response.status(200).entity(result).build();
	}
	
	
	//Servicio para mostar todos los tramites activos
	@GET
	@Path("getAllProcedures")
	@UnitOfWork
	public Response getAllProcedures() throws JSONException, SQLException {
		
		//Creamos el nuevo objeto
		List<Tbl_tramite> dataListTramites = new ArrayList<Tbl_tramite>(); 
		dataListTramites = tbl_tramiteDAO.getAllProcedures();
		
		//
		List<JSONObject> dataListJSONTramite = new ArrayList<JSONObject>();
		for(Tbl_tramite t:dataListTramites){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", t.getId_tramite());
			jsonObject.put("nombre", t.getNombre());
			jsonObject.put("descripcion", t.getDescripcion());
			dataListJSONTramite.add(jsonObject);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("tramites", dataListJSONTramite);
		String result = "" + jsonObject;
		return Response.status(200).entity(result).build();
	}
	
	//Servicio para create una solicitud de tramite
	@POST
	@Path("createRequestProcedure")
	@UnitOfWork
	public Response createRequestProcedure(String incomingData) throws JSONException{
		//Sacamos la informacion principal
		JSONObject inPUT = new JSONObject(incomingData);
		JSONObject tramiteJSON = inPUT.getJSONObject("tramite");
		
		//Sacamos la cedula del usuario
		Long id_usuario = tramiteJSON.getLong("idusuario");
		Long id_tramite = tramiteJSON.getLong("idtramite");
	
		//Creamos instancia del tramite ciudadano que vamos a crear
		Tbl_tramite_usuario tramite_usuario = new Tbl_tramite_usuario();
		//Colocamos el usuario propietario del tramite
		tramite_usuario.setId_usuario_ciudadano(id_usuario);
		//Colocamos el id del tramite que se crea
		tramite_usuario.setId_tramite(id_tramite);
		//Colocamos el estado del tramite
		tramite_usuario.setEstadoactivo(true);
		//Colocamos la fecha de creacion de la solicitud
		tramite_usuario.setFecha(new Date());
		
		
		//Persistimo la nueva solicitud del suario
		Long id_tramite_usuario = tbl_Tramite_UsuarioDAO.create(tramite_usuario);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("message", "la solicitud ha sido creada exitosamente");
		jsonObject.put("idsolicitud", id_tramite_usuario);
		String result = "" + jsonObject;
		return Response.status(200).entity(result).build();
	}
	
	@GET
	@Path("getAllRequetstProcedures")
	@UnitOfWork
	public Response getAllRequetstProcedures() throws JSONException, SQLException {
		//Creamos el nuevo objeto
		List<Tbl_tramite> dataListTramites = new ArrayList<Tbl_tramite>(); 
		dataListTramites = tbl_tramiteDAO.getAllProcedures();
		//
		List<JSONObject> dataListJSONTramite = new ArrayList<JSONObject>();
		for(Tbl_tramite t:dataListTramites){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", t.getId_tramite());
			jsonObject.put("nombre", t.getNombre());
			jsonObject.put("descripcion", t.getDescripcion());
			dataListJSONTramite.add(jsonObject);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("tramites", dataListJSONTramite);
		String result = "" + jsonObject;
		return Response.status(200).entity(result).build();
	}
	
	@GET
	@Path("getRequetstProcedureByID")
	@UnitOfWork
	public Response getRequetstProcedureByID() throws JSONException, SQLException {
		//Creamos el nuevo objeto
		List<Tbl_tramite> dataListTramites = new ArrayList<Tbl_tramite>(); 
		dataListTramites = tbl_tramiteDAO.getAllProcedures();
		//
		List<JSONObject> dataListJSONTramite = new ArrayList<JSONObject>();
		for(Tbl_tramite t:dataListTramites){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", t.getId_tramite());
			jsonObject.put("nombre", t.getNombre());
			jsonObject.put("descripcion", t.getDescripcion());
			dataListJSONTramite.add(jsonObject);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("tramites", dataListJSONTramite);
		String result = "" + jsonObject;
		return Response.status(200).entity(result).build();
	}
	
}
