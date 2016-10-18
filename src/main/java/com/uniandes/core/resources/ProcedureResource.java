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
import javax.ws.rs.PathParam;
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
import com.uniandes.db.dao.Tbl_Campo_UsuarioDAO;
import com.uniandes.db.dao.Tbl_FaseDAO;
import com.uniandes.db.dao.Tbl_Fase_UsuarioDAO;
import com.uniandes.db.dao.Tbl_TramiteDAO;
import com.uniandes.db.dao.Tbl_Tramite_UsuarioDAO;
import com.uniandes.db.vo.Tbl_campo;
import com.uniandes.db.vo.Tbl_campo_usuario;
import com.uniandes.db.vo.Tbl_fase;
import com.uniandes.db.vo.Tbl_fase_usuario;
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
	private Tbl_Fase_UsuarioDAO tbl_Fase_UsuarioDAO;
	private Tbl_Campo_UsuarioDAO tbl_Campo_UsuarioDAO;

	public ProcedureResource(Tbl_TramiteDAO tbl_tramiteDAO, 
			Tbl_FaseDAO tbl_FaseDAO, 
			Tbl_CampoDAO tbl_CampoDAO, 
			Tbl_Tramite_UsuarioDAO tbl_Tramite_UsuarioDAO,
			Tbl_Fase_UsuarioDAO tbl_Fase_UsuarioDAO,
			Tbl_Campo_UsuarioDAO tbl_Campo_UsuarioDAO) {
		this.tbl_tramiteDAO = tbl_tramiteDAO;
		this.tbl_FaseDAO = tbl_FaseDAO;
		this.tbl_CampoDAO = tbl_CampoDAO;
		this.tbl_Tramite_UsuarioDAO = tbl_Tramite_UsuarioDAO;
		this.tbl_Fase_UsuarioDAO = tbl_Fase_UsuarioDAO;
		this.tbl_Campo_UsuarioDAO = tbl_Campo_UsuarioDAO;
	}

	// Servicio para crear un tramite
	@POST
	@Path("createProcedure")
	@UnitOfWork
	public Response createProcedure(String incomingData) throws JSONException{
		try{
		// Json de entrada
		JSONObject inPUT = new JSONObject(incomingData);
		JSONObject tramiteJSON = inPUT.getJSONObject("tramites");
		
		//Creamos un objeto tipo tramite
		Tbl_tramite tbl_tramite = new Tbl_tramite();
		tbl_tramite.setNombre(tramiteJSON.getString("nombre"));
		tbl_tramite.setDescripcion(tramiteJSON.getString("descripcion"));
		
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
			tbl_campo.setObligatorio(true);
			tbl_campo.setId_fase(idFase);
			tbl_campo.setId_tramite(idTramite);
			//Persistimos el objeto
			Long idCampo = tbl_CampoDAO.create(tbl_campo);
		}
		
//		//Creamos la fase del tramite
//		JSONArray jsonArrayFases = tramiteJSON.getJSONArray("fases");
//		for(int i = 0; i<jsonArrayFases.length(); i ++){
//			//Sacamos la fase que vamos a crear
//			JSONObject jsonFase = (JSONObject) jsonArrayFases.get(i);
//			//Cremos el objeto fase
//			Tbl_fase  tbl_fase = new Tbl_fase();
//			tbl_fase.setOrden(jsonFase.getInt("orden"));
//			tbl_fase.setTipousuario(jsonFase.getInt("tipousuario"));
//			tbl_fase.setId_tramite(idTramite);
//			//Guardamos la fase nueva
//			Long idFase = tbl_FaseDAO.create(tbl_fase);
//			
//			//Creamos los campos de la fase
//			JSONArray jsonArrayCampos = jsonFase.getJSONArray("campos");
//			for(int j = 0; j<jsonArrayCampos.length(); j ++){
//				//Obtenemos el json del campo
//				JSONObject jsonCampo = (JSONObject) jsonArrayCampos.get(j);
//				//Creamos el objeto tipo tbl_campo
//				Tbl_campo tbl_campo = new Tbl_campo();
//				tbl_campo.setNombre(jsonCampo.getString("nombre"));
//				tbl_campo.setTipo(jsonCampo.getString("tipo"));
//				tbl_campo.setOrden(jsonCampo.getInt("orden"));
//				tbl_campo.setObligatorio(jsonCampo.getBoolean("obligatorio"));
//				tbl_campo.setId_fase(idFase);
//				//Persistimos el objeto
//				Long idCampo = tbl_CampoDAO.create(tbl_campo);
//				
//				
//			}
//		}
			JSONObject outPUT = new JSONObject();
			outPUT.put("message","El tramite ha sido creado exitosamente");
			String result = "" + outPUT;
			return Response.status(200).entity(result).build();
		}catch(Exception e){
			JSONObject outPUT = new JSONObject();
			outPUT.put("message","Ha ocurrido un error en la creación del trámite");
			String result = "Error";
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
		
		//Traemos el tramite de la base de datos
		Tbl_tramite tbl_tramite = tbl_tramiteDAO.finBydId(id_tramite);
	
		//Creamos instancia del tramite ciudadano que vamos a crear
		Tbl_tramite_usuario tramite_usuario = new Tbl_tramite_usuario();
		//Colocamos el usuario propietario del tramite
		tramite_usuario.setId_usuario_ciudadano(id_usuario);
		//Colocamos el id del tramite que se crea
		tramite_usuario.setId_tramite(id_tramite);
		//Colocamos el estado del tramite
		tramite_usuario.setEstadoactivo(true);
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
			List<Tbl_campo> datListTblCampo =  tbl_CampoDAO.getAllCamposByFase(fase.getId_fase());
			Iterator<Tbl_campo> iteratorFases = datListTblCampo.iterator();
			while(iteratorFases.hasNext()){
				Tbl_campo campo = iteratorFases.next();
				Tbl_campo_usuario campo_usuario = new Tbl_campo_usuario();
				campo_usuario.setId_campo(campo.getId_campo());
				campo_usuario.setId_fase_usuario(id_fase_usuario);
				campo_usuario.setId_tramite_usuario(id_tramite_usuario);
				campo_usuario.setTipo(campo.getTipo());
				campo_usuario.setNombre(campo.getNombre());
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
	
	//Servicio para mostrar todos los servicios
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
	@Path("getRequetstProcedureByID/{idtramite}")
	@UnitOfWork
	public Response getRequetstProcedureByID(@PathParam("idtramite") Long id_tramite_usuario) throws JSONException, SQLException {
//		
		//Encontramos el objeto id_tramite_usuario
		Tbl_tramite_usuario tbl_tramite_usuario = tbl_Tramite_UsuarioDAO.findById(id_tramite_usuario);
		List<Tbl_campo_usuario> dataListCamposUsuario = 
				tbl_Campo_UsuarioDAO.getAllCamposUsuarioByIdTramite(id_tramite_usuario);
		
		JSONObject output = new JSONObject();
		output.put("nombre",tbl_tramite_usuario.getNombre());
		output.put("descripcion",tbl_tramite_usuario.getDescripcion());
		JSONArray arrayCampos = new JSONArray();
		for(Tbl_campo_usuario campo_usuario:dataListCamposUsuario){
			JSONObject campo = new JSONObject();
			campo.put("nombre", campo_usuario.getNombre());
			campo.put("idcampo", campo_usuario.getId_campo_usuario());
			campo.put("tipo", campo_usuario.getTipo());
			arrayCampos.put(campo);
		}
		output.put("campos", arrayCampos);
		String result = "" + output;
		return Response.status(200).entity(result).build();
	}
	
	//Servicio para llenar un tramite
	@POST
	@Path("fillRequestProcedure")
	@UnitOfWork
	public Response fillRequestProcedure(String incomingData) throws JSONException, SQLException {
		//Sacamos la informacion principal
		JSONObject inPUT = new JSONObject(incomingData);
		//Sacamos la lista de campos a colocar valor
		JSONArray jsonArrayCampos = inPUT.getJSONArray("campos");
		//Creamos la lista de campos que se actualizaron con el estado
		JSONArray camposActualizados = new JSONArray();
		//Creamos el objeto de salida
		JSONObject output = new JSONObject();
		//Variable para llevar la cuenta de cuantos campos se actualizaron
		int contadoCamposActualizados = 0;
		
		for(int i=0; i<jsonArrayCampos.length(); i++){
			//Sacamos la lista de campos
			JSONObject JSONCampo = jsonArrayCampos.getJSONObject(i);
			//Creamos el objeto de respuesta de las actualizaciones que se hicieron
			JSONObject campoActualizado = new JSONObject();
			campoActualizado.put("id",JSONCampo.getLong("idcampo"));
			try{
				//Traemos de base de datos el objeto campo_usuario
				Tbl_campo_usuario campo_usuario = 
						tbl_Campo_UsuarioDAO.finById(JSONCampo.getLong("idcampo"));
				//Colocamos el valor
				if(campo_usuario.getTipo().equals("texto")){
						campo_usuario.setValortexto(JSONCampo.getString("valor"));
						tbl_Campo_UsuarioDAO.update(campo_usuario);
				}else{
				}
				campoActualizado.put("estado","true");
				contadoCamposActualizados ++;
			}catch(Exception e){
				campoActualizado.put("estado","false");
			}
			camposActualizados.put(campoActualizado);
		}
		output.put("message", "Se ha actualizaron " +  contadoCamposActualizados + 
				" campo(s) de " + jsonArrayCampos.length());
		output.put("valores",camposActualizados);
		String result = "" + output;
		return Response.status(200).entity(result).build();
	}
	
}
