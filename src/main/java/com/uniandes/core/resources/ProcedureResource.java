package com.uniandes.core.resources;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.uniandes.db.dao.Tbl_CampoDAO;
import com.uniandes.db.dao.Tbl_Campo_UsuarioDAO;
import com.uniandes.db.dao.Tbl_FaseDAO;
import com.uniandes.db.dao.Tbl_Fase_UsuarioDAO;
import com.uniandes.db.dao.Tbl_TramiteDAO;
import com.uniandes.db.dao.Tbl_Tramite_UsuarioDAO;
import com.uniandes.db.dao.UsuarioDAO;
import com.uniandes.db.vo.Tbl_campo;
import com.uniandes.db.vo.Tbl_campo_usuario;
import com.uniandes.db.vo.Tbl_fase;
import com.uniandes.db.vo.Tbl_fase_usuario;
import com.uniandes.db.vo.Tbl_tramite;
import com.uniandes.db.vo.Tbl_tramite_usuario;
import com.uniandes.db.vo.Tbl_usuario;

import android.text.format.DateFormat;
import io.dropwizard.hibernate.UnitOfWork;

@Path("/procedureResource")
@Produces({ MediaType.APPLICATION_JSON })
public class ProcedureResource {

	private Tbl_TramiteDAO tbl_tramiteDAO;
	private Tbl_FaseDAO tbl_FaseDAO;
	private Tbl_CampoDAO tbl_CampoDAO;
	private Tbl_Tramite_UsuarioDAO tbl_Tramite_UsuarioDAO;
	private Tbl_Fase_UsuarioDAO tbl_Fase_UsuarioDAO;
	private Tbl_Campo_UsuarioDAO tbl_Campo_UsuarioDAO;
	private UsuarioDAO usuarioDAO;

	public ProcedureResource(Tbl_TramiteDAO tbl_tramiteDAO, 
			Tbl_FaseDAO tbl_FaseDAO, 
			Tbl_CampoDAO tbl_CampoDAO, 
			Tbl_Tramite_UsuarioDAO tbl_Tramite_UsuarioDAO,
			Tbl_Fase_UsuarioDAO tbl_Fase_UsuarioDAO,
			Tbl_Campo_UsuarioDAO tbl_Campo_UsuarioDAO,
			UsuarioDAO usuarioDAO) {
		this.tbl_tramiteDAO = tbl_tramiteDAO;
		this.tbl_FaseDAO = tbl_FaseDAO;
		this.tbl_CampoDAO = tbl_CampoDAO;
		this.tbl_Tramite_UsuarioDAO = tbl_Tramite_UsuarioDAO;
		this.tbl_Fase_UsuarioDAO = tbl_Fase_UsuarioDAO;
		this.tbl_Campo_UsuarioDAO = tbl_Campo_UsuarioDAO;
		this.usuarioDAO = usuarioDAO;
	}

	// Servicio para crear un tramite
	@POST
	@Path("createProcedure")
	@UnitOfWork
	public Response createProcedure(String incomingData) throws JSONException{
		try{
		// Json de entrada
		JSONObject tramiteJSON = new JSONObject(incomingData);
//		JSONObject tramiteJSON = inPUT.getJSONObject("tramites");
		
		//Creamos un objeto tipo tramite
		Tbl_tramite tbl_tramite = new Tbl_tramite();
		tbl_tramite.setNombre(tramiteJSON.getString("nombre"));
		tbl_tramite.setDescripcion(tramiteJSON.getString("descripcion"));
		tbl_tramite.setEstado(1);
		
		//Creamos el nuevo objeto
		Long idTramite = tbl_tramiteDAO.create(tbl_tramite);
		
		//Sacamos los campos del trámite
		JSONArray jsonArrayCampos = tramiteJSON.getJSONArray("campos");
		
		//Creamos la fase asociada al tramite
		Tbl_fase  tbl_fase = new Tbl_fase();
		tbl_fase.setOrden(0);
		tbl_fase.setTipousuario(0);
		tbl_fase.setId_tramite(idTramite);
		//Guardamos la fase nueva
		Long idFase = tbl_FaseDAO.create(tbl_fase);
		
		//Recorremos el array de los campos
		for(int i = 0; i<jsonArrayCampos.length(); i ++){
			//Obtenemos el json del campo
			JSONObject jsonCampo = (JSONObject) jsonArrayCampos.get(i);
			//Creamos el objeto tipo tbl_campo
			Tbl_campo tbl_campo = new Tbl_campo();
			tbl_campo.setNombre(jsonCampo.getString("nombre"));
			tbl_campo.setTipo(jsonCampo.getString("tipo"));
			tbl_campo.setOrden(0); 
			tbl_campo.setEstado(true); 
			tbl_campo.setObligatorio(true);
			tbl_campo.setId_fase(idFase);
			tbl_campo.setId_tramite(idTramite);
			//Persistimos el objeto
			Long idCampo = tbl_CampoDAO.create(tbl_campo);
		}
		
		JSONObject outPUT = new JSONObject();
		outPUT.put("message","El tramite ha sido creado exitosamente");
		String result = "" + outPUT;
		return Response.status(200).entity(result).build();
		}catch(Exception e){
			JSONObject outPUT = new JSONObject();
			outPUT.put("message","Ha ocurrido un error en la creación del trámite");
			String result = "Error";
			System.out.println(e);
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
			return Response.status(500).entity(result).build();
		}
	}
	
	//Servicio para modificar tramite
	@POST
	@Path("modifyProcedure")
	@UnitOfWork
	public Response modifyProcedure(String incomingData) throws JSONException{
		try{
		// Json de entrada
		JSONObject tramiteJSON = new JSONObject(incomingData);
		//Sacamos el id del tramite
		Long idTramite =  tramiteJSON.getLong("id");
		
		//Creamos un objeto tipo tramite
		Tbl_tramite tbl_tramite = tbl_tramiteDAO.findById(idTramite);
		
		//Creamos un objeto tipo tramite
		tbl_tramite.setNombre(tramiteJSON.getString("nombre"));
		tbl_tramite.setDescripcion(tramiteJSON.getString("descripcion"));
		
		//Creamos el nuevo objeto
		tbl_tramiteDAO.update(tbl_tramite);
		
		//Todo los campos los desactivamos
		tbl_tramiteDAO.deleteAllFields(tbl_tramite.getId_tramite());
		
		//Sacamos los campos del trámite
		JSONArray jsonArrayCampos = tramiteJSON.getJSONArray("campos");
		
		//Traemos las fases del tramite
		List<Tbl_fase> dataListFases = tbl_FaseDAO.getAllFasesByTramite(idTramite);
		
		//Sacamos el di de la unica fase que en teoria deberia tener
		Long idFase = dataListFases.get(0).getId_fase();
		
		//Recorremos el array de los campos
		for(int i = 0; i<jsonArrayCampos.length(); i ++){
			//Obtenemos el json del campo
			JSONObject jsonCampo = (JSONObject) jsonArrayCampos.get(i);
			//Creamos el objeto tipo tbl_campo
			Tbl_campo tbl_campo = new Tbl_campo();
			tbl_campo.setNombre(jsonCampo.getString("nombre"));
			tbl_campo.setTipo(jsonCampo.getString("tipo"));
			tbl_campo.setOrden(0);
			tbl_campo.setObligatorio(true);
			tbl_campo.setEstado(true);
			tbl_campo.setId_fase(idFase);
			tbl_campo.setId_tramite(idTramite);
			//Persistimos el objeto
			tbl_CampoDAO.create(tbl_campo);
		}
		
		JSONObject outPUT = new JSONObject();
		outPUT.put("message","El tramite ha sido actualizado exitosamente");
		String result = "" + outPUT;
		return Response.status(200).entity(result).build();
		}catch(Exception e){
			JSONObject outPUT = new JSONObject();
			outPUT.put("message","Ha ocurrido un error en la creación del trámite");
			String result = "Error";
			System.out.println(e);
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
			return Response.status(500).entity(result).build();
		}
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
			List<Tbl_campo> dataListCampos = 
					tbl_CampoDAO.getAllCamposByID(t.getId_tramite());
			JSONArray arrayCampos = new JSONArray();
			for(Tbl_campo campo_tramite:dataListCampos){
				JSONObject campo = new JSONObject();
				campo.put("nombre", campo_tramite.getNombre());
				campo.put("idcampo", campo_tramite.getId_campo());
				campo.put("tipo", campo_tramite.getTipo());
				arrayCampos.put(campo);
			}
			jsonObject.put("campos",arrayCampos);
			dataListJSONTramite.add(jsonObject);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("tramites", dataListJSONTramite);
		String result = "" + jsonObject;
		return Response.status(200).entity(result).build();
	}
	
	
	
	
	//Servicio para create una solicitud de tramite
	@POST
	@Path("fillRequestProcedure")
	@UnitOfWork
	public Response fillRequestProcedure(String incomingData) throws JSONException{
		//Sacamos la informacion principal
		JSONObject tramiteJSON = new JSONObject(incomingData);
		
		//Sacamos la cedula del usuario
		String id_usuario = tramiteJSON.getString("idusuario");
		Long id_tramite = tramiteJSON.getLong("idtramite");
		
		//Traemos el tramite de la base de datos
		Tbl_tramite tbl_tramite = tbl_tramiteDAO.findById(id_tramite);
	
		if(tbl_tramite == null){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("message", "Este id de tramite no existe");
			String result = "" + jsonObject;
			return Response.status(500).entity(result).build();
		}
		//Creamos instancia del tramite ciudadano que vamos a crear
		Tbl_tramite_usuario tramite_usuario = new Tbl_tramite_usuario();
		//Colocamos el usuario propietario del tramite
		tramite_usuario.setId_usuario_ciudadano(id_usuario);
		//Colocamos el id del tramite que se crea
		tramite_usuario.setId_tramite(id_tramite);
		//Colocamos el estado del tramite
		tramite_usuario.setEstado(0);
		//Colocamos el nombre del tramite
		tramite_usuario.setNombre(tbl_tramite.getNombre());
		//Colocamos el nombre del tramite
		tramite_usuario.setDescripcion(tbl_tramite.getDescripcion());
		//Colocamos la fecha de creacion de la solicitud
		tramite_usuario.setFecha(new Date());
		//Persistimo la nueva solicitud del suario
		Long id_tramite_usuario = tbl_Tramite_UsuarioDAO.create(tramite_usuario);
		
		//Buscamos todas las fases de un tramite
		List<Tbl_fase> dataListTbl_fase = tbl_FaseDAO.getAllFasesByTramite(id_tramite);
		//Recorremos la lista para empezar a crear las fases de cada usuario
		Iterator<Tbl_fase> iterator = dataListTbl_fase.iterator();
		while(iterator.hasNext()){
			Tbl_fase fase = iterator.next();
			Tbl_fase_usuario fase_usuario = new Tbl_fase_usuario();
			fase_usuario.setId_tramite_usuario(id_tramite_usuario);
			fase_usuario.setId_fase(fase.getId_fase());
			//Persistimos el objeto fase
			Long id_fase_usuario = tbl_Fase_UsuarioDAO.create(fase_usuario);
			//Sacamos los campos que corresponden a esta fase
			JSONArray jsonArrayCampos = tramiteJSON.getJSONArray("campos");
			for(int i=0; i<jsonArrayCampos.length(); i++){
				JSONObject JSONCampo = jsonArrayCampos.getJSONObject(i);
				Tbl_campo campo = tbl_CampoDAO.finById(JSONCampo.getLong("idcampo"));
				Tbl_campo_usuario campo_usuario = new Tbl_campo_usuario();
				campo_usuario.setId_campo(campo.getId_campo());
				campo_usuario.setId_fase_usuario(id_fase_usuario);
				campo_usuario.setId_tramite_usuario(id_tramite_usuario);
				campo_usuario.setTipo(campo.getTipo());
				campo_usuario.setNombre(campo.getNombre());
				if(campo.getTipo().equals("texto")){
					campo_usuario.setValortexto(JSONCampo.getString("valor"));
				}
				//Persistimos el objeto campo usuario
				tbl_Campo_UsuarioDAO.create(campo_usuario);
			}
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("message", "la solicitud ha sido creada exitosamente");
		jsonObject.put("idsolicitud", id_tramite_usuario);
		String result = "" + jsonObject;
		return Response.status(200).entity(result).build();
	}
	
	
	//Traer una solicitud por id de tramite
	@GET
	@Path("getRequestProcedureByID/{idtramite}")
	@UnitOfWork
	public Response getRequestProcedureByID(@PathParam("idtramite") Long id_tramite_usuario) throws JSONException, SQLException {	
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
		//Encontramos el objeto id_tramite_usuario
		Tbl_tramite_usuario tbl_tramite_usuario = tbl_Tramite_UsuarioDAO.findById(id_tramite_usuario);
		List<Tbl_campo_usuario> dataListCamposUsuario = 
				tbl_Campo_UsuarioDAO.getAllCamposUsuarioByIdTramite(id_tramite_usuario);
		
		JSONObject output = new JSONObject();
		output.put("nombre",tbl_tramite_usuario.getNombre());
		output.put("descripcion",tbl_tramite_usuario.getDescripcion());
		output.put("id", id_tramite_usuario);
		output.put("estado", tbl_tramite_usuario.getEstado());
		output.put("fechacreacion", dateFormat.format(tbl_tramite_usuario.getFecha()));
		
		
		//Colocamos los datos del usuario
		Tbl_usuario funcionario = usuarioDAO.findById(tbl_tramite_usuario.getId_usuario_funcionario());
		output.put("nombrefuncionario", funcionario.getNombre());
		output.put("documentofuncionario", funcionario.getCedula());
		//Colocamos los datos del funcionario
		Tbl_usuario ciudadano = usuarioDAO.findById(tbl_tramite_usuario.getId_usuario_ciudadano());
		output.put("nombreciudadano", ciudadano.getNombre());
		output.put("documentociudadano", ciudadano.getCedula());
		JSONArray arrayCampos = new JSONArray();
		List<Tbl_campo_usuario> tmpList = new ArrayList<Tbl_campo_usuario>();
		tmpList = tbl_Campo_UsuarioDAO.getAllCamposUsuarioByIdTramite(tbl_tramite_usuario.getId_tramite_usuario());
		for(int i=0; i<tmpList.size(); i++){
			Tbl_campo_usuario campo_usuario = tmpList.get(i);
			JSONObject campo = new JSONObject();
			campo.put("id", campo_usuario.getId_campo_usuario());
			campo.put("nombre", campo_usuario.getNombre());
			//Valor
			if(campo_usuario.getTipo().equals("texto")){
				campo.put("valor", campo_usuario.getValortexto()!=null?campo_usuario.getValortexto():"");
			}else{
				campo.put("valor", "http://localhost:44111/fileResource/downloadFileServlet?idarchivo=" + campo_usuario.getValorarchivo());
			}
			campo.put("tipo", campo_usuario.getTipo());
			campo.put("tipo", campo_usuario.getTipo());
			
			arrayCampos.put(campo);
		}
		output.put("campos", arrayCampos);
		String result = "" + output;
		return Response.status(200).entity(result).build();
	}
	
	@GET
	@Path("getProcedureByID/{idtramite}")
	@UnitOfWork
	public Response getProcedureByID(@PathParam("idtramite") Long idtramite) throws JSONException, SQLException {
//		
		//Encontramos el objeto id_tramite_usuario
		Tbl_tramite tbl_tramite_usuario = tbl_tramiteDAO.findById(idtramite);
		List<Tbl_campo> dataListCampos = 
				tbl_CampoDAO.getAllCamposByID(idtramite);
		
		JSONObject output = new JSONObject();
		output.put("nombre",tbl_tramite_usuario.getNombre());
		output.put("descripcion",tbl_tramite_usuario.getDescripcion());
		JSONArray arrayCampos = new JSONArray();
		for(Tbl_campo campo_tramite:dataListCampos){
			JSONObject campo = new JSONObject();
			campo.put("nombre", campo_tramite.getNombre());
			campo.put("idcampo", campo_tramite.getId_campo());
			campo.put("tipo", campo_tramite.getTipo());
			arrayCampos.put(campo);
		}
		output.put("campos", arrayCampos);
		String result = "" + output;
		return Response.status(200).entity(result).build();
	}
	

	//traer las solicitudes por usuario
	@GET
	@Path("getAllRequestProcedures")
	@UnitOfWork
	public Response getAllRequestProcedures() throws JSONException, SQLException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
		
		//Creamos el nuevo objeto
		List<Tbl_tramite_usuario> dataListTramites = new ArrayList<Tbl_tramite_usuario>();
		
		//Sacamos el usuario
//		Tbl_usuario tbl_usuario = usuarioDAO.findById(idusuario);
//		
//		if(tbl_usuario == null){
//			JSONObject outPUT = new JSONObject();
//			outPUT.put("message","El usuario no existe");
//			String result = "" + outPUT;
//			return Response.status(500).entity(result).build();
//		}
		//Validamos si es un administrador
//		if(tbl_usuario.getTipo().equals("0")){
//			
//		}else{
//			//Validamos si es un ciudadano
//			if(tbl_usuario.getTipo().equals("1")){
//				dataListTramites = tbl_Tramite_UsuarioDAO.getAllRequestProceduresByCitizen(idusuario);
//			}else{
//				//Validamos si es un funcionario
//				if(tbl_usuario.getTipo().equals("2")){
//					dataListTramites = tbl_Tramite_UsuarioDAO.getAllRequestProceduresByFunctionary(idusuario);
//				}
//			}
//		}
		
		dataListTramites = tbl_Tramite_UsuarioDAO.getAllRequestProcedures();
		
		
		//
		List<JSONObject> dataListJSONTramite = new ArrayList<JSONObject>();
		for(Tbl_tramite_usuario t:dataListTramites){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", t.getId_tramite_usuario());
			jsonObject.put("nombre", t.getNombre());
			jsonObject.put("estado", t.getEstado());
			jsonObject.put("fechacreacion", dateFormat.format(t.getFecha()));
			//Colocamos los datos del usuario
			Tbl_usuario funcionario = usuarioDAO.findById(t.getId_usuario_funcionario());
			jsonObject.put("nombrefuncionario", funcionario.getNombre());
			jsonObject.put("documentofuncionario", funcionario.getCedula());
			//Colocamos los datos del funcionario
			Tbl_usuario ciudadano = usuarioDAO.findById(t.getId_usuario_ciudadano());
			jsonObject.put("nombreciudadano", ciudadano.getNombre());
			jsonObject.put("documentociudadano", ciudadano.getCedula());
			
			List<Tbl_campo_usuario> tmpList = new ArrayList<Tbl_campo_usuario>();
			tmpList = tbl_Campo_UsuarioDAO.getAllCamposUsuarioByIdTramite(t.getId_tramite_usuario());
			JSONArray arrayCampos = new JSONArray();
			for(int i=0; i<tmpList.size(); i++){
				Tbl_campo_usuario campo_usuario = tmpList.get(i);
				JSONObject campo = new JSONObject();
				campo.put("id", campo_usuario.getId_campo_usuario());
				campo.put("nombre", campo_usuario.getNombre());
				//Valor
				if(campo_usuario.getTipo().equals("texto")){
					campo.put("valor", campo_usuario.getValortexto()!=null?campo_usuario.getValortexto():"");
				}else{
					campo.put("valor", "http://localhost:44111/fileResource/downloadFileServlet?idarchivo=" + campo_usuario.getValorarchivo());
				}
				campo.put("tipo", campo_usuario.getTipo());
				
				arrayCampos.put(campo);
			}
			jsonObject.put("campos", arrayCampos);
			dataListJSONTramite.add(jsonObject);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("tramites", dataListJSONTramite);
		String result = "" + jsonObject;
		return Response.status(200).entity(result).build();
	}
	
	
	
	//traer las solicitudes por usuario
	@GET
	@Path("getRequestProceduresByUser/{idusuario}")
	@UnitOfWork
	public Response getRequestProceduresByUser(@PathParam("idusuario") String idusuario) throws JSONException, SQLException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
		
		//Creamos el nuevo objeto
		List<Tbl_tramite_usuario> dataListTramites = new ArrayList<Tbl_tramite_usuario>();
		
		//Sacamos el usuario
		Tbl_usuario tbl_usuario = usuarioDAO.findById(idusuario);
		
		if(tbl_usuario == null){
			JSONObject outPUT = new JSONObject();
			outPUT.put("message","El usuario no existe");
			String result = "" + outPUT;
			return Response.status(500).entity(result).build();
		}
		//Validamos si es un administrador
		if(tbl_usuario.getTipo().equals("0")){
			dataListTramites = tbl_Tramite_UsuarioDAO.getAllRequestProcedures();
		}else{
			//Validamos si es un ciudadano
			if(tbl_usuario.getTipo().equals("1")){
				dataListTramites = tbl_Tramite_UsuarioDAO.getAllRequestProceduresByCitizen(idusuario);
			}else{
				//Validamos si es un funcionario
				if(tbl_usuario.getTipo().equals("2")){
					dataListTramites = tbl_Tramite_UsuarioDAO.getAllRequestProceduresByFunctionary(idusuario);
				}
			}
		}
		
		
		
		//
		List<JSONObject> dataListJSONTramite = new ArrayList<JSONObject>();
		for(Tbl_tramite_usuario t:dataListTramites){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", t.getId_tramite_usuario());
			jsonObject.put("nombre", t.getNombre());
			jsonObject.put("estado", t.getEstado());
			jsonObject.put("fechacreacion", dateFormat.format(t.getFecha()));
			//Colocamos los datos del usuario
			Tbl_usuario funcionario = usuarioDAO.findById(t.getId_usuario_funcionario());
			jsonObject.put("nombrefuncionario", funcionario.getNombre());
			jsonObject.put("documentofuncionario", funcionario.getCedula());
			//Colocamos los datos del funcionario
			Tbl_usuario ciudadano = usuarioDAO.findById(t.getId_usuario_ciudadano());
			jsonObject.put("nombreciudadano", ciudadano.getNombre());
			jsonObject.put("documentociudadano", ciudadano.getCedula());
			
			List<Tbl_campo_usuario> tmpList = new ArrayList<Tbl_campo_usuario>();
			tmpList = tbl_Campo_UsuarioDAO.getAllCamposUsuarioByIdTramite(t.getId_tramite_usuario());
			JSONArray arrayCampos = new JSONArray();
			for(int i=0; i<tmpList.size(); i++){
				Tbl_campo_usuario campo_usuario = tmpList.get(i);
				JSONObject campo = new JSONObject();
				campo.put("id", campo_usuario.getId_campo_usuario());
				campo.put("nombre", campo_usuario.getNombre());
				//Valor
				if(campo_usuario.getTipo().equals("texto")){
					campo.put("valor", campo_usuario.getValortexto()!=null?campo_usuario.getValortexto():"");
				}else{
					campo.put("valor", "http://localhost:44111/fileResource/downloadFileServlet?idarchivo=" + campo_usuario.getValorarchivo());
				}
				campo.put("tipo", campo_usuario.getTipo());
				
				arrayCampos.put(campo);
			}
			jsonObject.put("campos", arrayCampos);
			dataListJSONTramite.add(jsonObject);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("tramites", dataListJSONTramite);
		String result = "" + jsonObject;
		return Response.status(200).entity(result).build();
	}
	
	//Eliminar tramite
	@POST
	@Path("deleteProcedure")
	@UnitOfWork
	public Response deleteProcedure(String incomingData) throws JSONException, SQLException {
		try{
			JSONObject tramiteJSON = new JSONObject(incomingData);
			//Sacamos el id del tramite
			Long idTramite =  tramiteJSON.getLong("id");
			tbl_tramiteDAO.deleteProcedureById(idTramite);
		}catch(Exception e){
			JSONObject outPUT = new JSONObject();
			outPUT.put("message","Ha ocurrido un error eliminando el tramite");
			String result = "" + outPUT;
			return Response.status(500).entity(result).build();
		}
		
		JSONObject outPUT = new JSONObject();
		outPUT.put("message","El tramite ha sido eliminado exitosamente");
		String result = "" + outPUT;
		return Response.status(200).entity(result).build();
	}
	
	
	//Eeliminar solicitud
	@POST
	@Path("deleteUserProcedure")
	@UnitOfWork
	public Response deleteUserProcedure(String incomingData) throws JSONException, SQLException {
		try {
			JSONObject tramiteJSON = new JSONObject(incomingData);
			// Sacamos el id del tramite
			Long idTramite = tramiteJSON.getLong("id");
			tbl_Tramite_UsuarioDAO.deleteRequestProcedureById(idTramite);
		} catch (Exception e) {
			JSONObject outPUT = new JSONObject();
			outPUT.put("message", "Ha ocurrido un error eliminando la solicitud");
			String result = "" + outPUT;
			return Response.status(500).entity(result).build();
		}

		JSONObject outPUT = new JSONObject();
		outPUT.put("message", "La solicitud ha sido eliminada exitosamente");
		String result = "" + outPUT;
		return Response.status(200).entity(result).build();
	}	
	
	
	//Asignar responsable a una solicitud
	@POST
	@Path("assignResponsableUR")
	@UnitOfWork
	public Response assignResponsableUR(String incomingData) throws JSONException, SQLException {
		try {
			JSONObject tramiteJSON = new JSONObject(incomingData);
//			// Sacamos el id del tramite
			Long idTramite = tramiteJSON.getLong("id");
			//Sacamos el funcionarion
			String idFuncionario = tramiteJSON.getString("idfuncionario");
			//Enontramos el tramite
			Tbl_tramite_usuario tbl_tramite_usuario = tbl_Tramite_UsuarioDAO.findById(idTramite);
			//Asignamos el id del funcionario
			tbl_tramite_usuario.setId_usuario_funcionario(idFuncionario);
			//Actualizamos
			tbl_Tramite_UsuarioDAO.update(tbl_tramite_usuario);

		} catch (Exception e) {
			JSONObject outPUT = new JSONObject();
			outPUT.put("message", "Ha ocurrido un error eliminando la solicitud");
			String result = "" + outPUT;
			return Response.status(500).entity(result).build();
		}

		JSONObject outPUT = new JSONObject();
		outPUT.put("message", "La solicitud ha sido actualizada exitosamente");
		String result = "" + outPUT;
		return Response.status(200).entity(result).build();
	}	
	
	//Cambiar el estado de una solicitud
	@POST
	@Path("changeState")
	@UnitOfWork
	public Response changeState(String incomingData) throws JSONException, SQLException {
		try {
			JSONObject tramiteJSON = new JSONObject(incomingData);
//			// Sacamos el id del tramite
			Long idTramite = tramiteJSON.getLong("id");
			//Sacamos el funcionarion
			int state = tramiteJSON.getInt("state");
			//Enontramos el tramite
			Tbl_tramite_usuario tbl_tramite_usuario = tbl_Tramite_UsuarioDAO.findById(idTramite);
			//Asignamos el id del funcionario
			tbl_tramite_usuario.setEstado(state);
			//Actualizamos
			tbl_Tramite_UsuarioDAO.update(tbl_tramite_usuario);

		} catch (Exception e) {
			JSONObject outPUT = new JSONObject();
			outPUT.put("message", "Ha ocurrido un error eliminando la solicitud");
			String result = "" + outPUT;
			return Response.status(500).entity(result).build();
		}

		JSONObject outPUT = new JSONObject();
		outPUT.put("message", "La solicitud ha sido actualizada exitosamente");
		String result = "" + outPUT;
		return Response.status(200).entity(result).build();
	}	
	
	
}
