package com.uniandes.experimento.resources;

import com.uniandes.experimento.common.JWT_Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.ResultIterator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

@Path("episodeResource")
@Produces({MediaType.APPLICATION_JSON})
public class EpisodeResource {


    private DBI generalDAO;

    public EpisodeResource(DBI generalDAO) {
        this.generalDAO = generalDAO;
    }

    public EpisodeResource() {
    }

    @POST
    @Path("registerEpisode")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerEpisode(String incomingData) throws JSONException, SQLException {
        //Abrimos la conexion
        Handle h = generalDAO.open();
        //Validamos el usuario
        JSONObject jsonUser = new JSONObject(incomingData);


        //Declaramos la variable para sacar el token del usuario de la base de datos
        String tokenMessage = jsonUser.getString("token");
        String tokenUsario = "";
        String key = "";


        String user_id = jsonUser.getString("user_id");
        //Realizamos el query para traer los usuarios
        String query = "SELECT * FROM USUARIOS WHERE cedula = '" + user_id + "'";

        try{
        Statement stmt = null;
        stmt = h.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while(rs.next()){
            tokenUsario = rs.getString("token");
            key = rs.getString("key");
        }
        }catch(Exception e){
        	
        }finally {
			h.close();
		}
        
        
        JWT_Utility.validarToken(tokenUsario, tokenMessage, key);
        
        try{
        	JWT_Utility.validarToken(tokenUsario, tokenMessage, key);
        }catch(Exception e){
        	JSONObject total = new JSONObject();
        	total.put("episodios", "");
        	total.put("message","error");
    		String result = "" + total;
        	return Response.status(403).entity(result).build();
        }
    	

        JSONObject obj = new JSONObject();
        obj.put("status", "ok");

        Proceso hilo1 = new Proceso("Hilo 1");
        // //Enviamos la info
        hilo1.setMensaje(incomingData);
        hilo1.start();

        String result = "" + obj;
        return Response.status(200).entity(obj + "").build();
    }

    @POST
    @Path("showEpisode")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response validateUser(String incomingData) throws JSONException, SQLException {
    	 //Abrimos la conexion
        Handle h = generalDAO.open();
        //Validamos el usuario
        JSONObject jsonUser = new JSONObject(incomingData);


        //Declaramos la variable para sacar el token del usuario de la base de datos
        String tokenMessage = jsonUser.getString("token");
        String tokenUsario = "";
        String key = "";


        String user_id = jsonUser.getString("user_id");
        //Realizamos el query para traer los usuarios
        String query = "SELECT * FROM USUARIOS WHERE cedula = '" + user_id + "'";

        Statement stmt = null;
        stmt = h.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while(rs.next()){
            tokenUsario = rs.getString("token");
            key = rs.getString("key");
        }
        try{
        	JWT_Utility.validarToken(tokenUsario, tokenMessage, key);
        }catch(Exception e){
        	JSONObject total = new JSONObject();
        	total.put("episodios", "");
        	total.put("message","error");
    		String result = "" + total;
        	return Response.status(403).entity(result).build();
        }
    	
		JSONObject total = new JSONObject();
		JSONObject jsonObject = new JSONObject(incomingData);
		String cedula = jsonObject.getString("user_id_paciente");
		
		query = "select json from episodios where userid = '" + cedula + "'";
		ResultIterator tareasIterator = h.createQuery(query).iterator();
		tareasIterator = h.createQuery(query).iterator();
		ArrayList<JSONObject> ob = new ArrayList<JSONObject>();
		if(tareasIterator.hasNext()){
			while(tareasIterator.hasNext()){
				Map<String, Object>  t = (Map<String, Object>) tareasIterator.next();
				Object primero = t.get("json");
				JSONObject object = new JSONObject(primero.toString());
				ob.add(object);
			}
		}
		total.put("episodios", ob.toArray());
		String result = "" + total;
		return Response.status(200).entity(result).build();
    }


    // Metodo para guardar en la base de datos
    private class Proceso extends Thread {

        private String mensaje;

        public Proceso(String msg) {
            super(msg);
        }

        public void setMensaje(String msj) throws JSONException {
            this.mensaje = msj;
        }

        public void run(){
            Handle h = generalDAO.open();
            try{
                JSONObject jsonObjectTmp = new JSONObject(mensaje);
                org.json.JSONArray jsonArray = jsonObjectTmp.getJSONArray("episodes");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                    String cedula = jsonObject.getString("user_id");
                    String query = "SELECT * FROM USUARIOS WHERE cedula = '"+cedula+"'";
                    ResultIterator tareasIterator = h.createQuery(query).iterator();
                    if(!tareasIterator.hasNext())
                    {
                        String query_insert = "INSERT INTO USUARIOS (cedula) values ('"+ cedula +"')";
                        h.createStatement(query_insert).execute();
                        tareasIterator = h.createQuery(query).iterator();
                    }

                    if(tareasIterator.hasNext()){
                        String sql = "insert into episodios (userid, json) "
                                + "values (?, ?)";
                        PreparedStatement preparedStatement = h.getConnection().prepareStatement(sql);
                        Map<String, Object> t = (Map<String, Object>) tareasIterator.next();
                        Object primero = t.get("cedula");
                        String id_isuarios = (String) primero;
                        preparedStatement.setString(1, id_isuarios);
                        preparedStatement.setString(2, mensaje);
                        try{
                            int rowsInserted = preparedStatement.executeUpdate();
                        }catch(Exception e){
                            System.out.println("Errror escribiendo episodios");
                        }
                    }
                }
            }catch(Exception e){

            }finally{
                h.close();
            }
        }
    }


    @POST
    @Path("/exampleService")
    public String example_service() throws JSONException, SQLException {
        JSONObject obj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for(int i=1;i<3; i++){
            JSONObject objTmp = new JSONObject();
            objTmp.put(i + "", "Windstorm");
            jsonArray.put(objTmp);
        }
        obj.put("data",jsonArray);
        String result = "" + obj.toString();
        return result;
    }
}


