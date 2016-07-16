package com.uniandes.experimento.resources;

import com.uniandes.experimento.common.JWT_Utility;

import org.json.JSONException;
import org.json.JSONObject;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.ResultIterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.text.ParseException;

@Path("/userResource")
@Produces({MediaType.APPLICATION_JSON})
public class UserResource {
    private DBI generalDAO;

    public UserResource(DBI generalDAO) {
        this.generalDAO = generalDAO;
    }

    //Servicio para registrar el usuario
    @POST
    @Path("registerUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(String incomingData) throws JSONException, SQLException {
        //Json de entrada
        JSONObject inPUT = new JSONObject(incomingData);
//        //Json de respuesta
        JSONObject outPUT = new JSONObject();
//        //A
        Handle h = generalDAO.open();
        try{
            String name = inPUT.getString("name");
            String cedula = inPUT.getString("user_id");
            String imei = inPUT.getString("imei");
            String clientid = inPUT.getString("clientid");
            String clientse = inPUT.getString("clientse");
            String password = inPUT.getString("password");

            String query = "SELECT * FROM USUARIOS WHERE cedula = '"+cedula+"'";
            ResultIterator tareasIterator = h.createQuery(query).iterator();
            if(!tareasIterator.hasNext())
            {
                String query_insert = "INSERT INTO USUARIOS (cedula, password, clientid, clientse, imei) " +
                        "values ('"+ cedula +"','" + password + "','" + clientid + "', '" + clientse + "', '"+ imei +"')";
                if(h.createStatement(query_insert).execute()==1){
                    outPUT.put("status","true");
                    outPUT.put("message","El usuario se ha inscrito correctamente");
                }else{
                    outPUT.put("status","false");
                    outPUT.put("message","Ha ocurrido un error al insertar el usuario");
                }

            }else{
                outPUT.put("status","false");
                outPUT.put("message","El usuario ya se encuentra inscrito");
            }
        }catch (Exception e){
            outPUT.put("status","false");
            outPUT.put("message","Ha ocurrido un error al insertar el usuario");
        }finally {
            h.close();
        }
        String result = ""+outPUT;
        return Response.status(200).entity(result).build();
    }

    @POST
    @Path("loginUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUser(String incomingData) throws JSONException, SQLException, ParseException {
        String response;
        //Abrimos la conexion
        Handle h = generalDAO.open();
        //Json de salida
        JSONObject outPUT = new JSONObject();
        try {
            //Json de entrada
            JSONObject inPUT = new JSONObject(incomingData);
            //Validamos que el usuario conincida
            //Obtenemos el id del usuario
            String cedula = inPUT.getString("user_id");
            //Obtenemos el password
            String password = inPUT.getString("password");
            String query = "SELECT * FROM USUARIOS WHERE cedula = '"+cedula+"' AND password = '" + password + "'" ;
            ResultIterator tareasIterator = h.createQuery(query).iterator();
            if(!tareasIterator.hasNext())
            {
                outPUT.put("status","error");
                outPUT.put("message","El usuario y la contrase&ntilde;a no coinciden");
                response = ""+outPUT.toString();
                return Response.status(401).entity(response).build();
            }

            //Generamos el token del usuario
            JSONObject tokenJWTUSER = JWT_Utility.generarToken(inPUT.toString());
            query = "UPDATE  USUARIOS  set key =  '" + tokenJWTUSER.get("key")  + "', token = '" +
                    tokenJWTUSER.get("token") + "' WHERE cedula = '" + cedula + "' ";

            //Creamos la sentencia y la ejecutamos
            int result =  h.createStatement(query).execute();
            if(result !=1){
                outPUT.put("status","ok");
                outPUT.put("message","Ha ocurrido un error");
                response = ""+outPUT.toString();
                return Response.status(200).entity(response).build();

            }else{
                outPUT.put("status","ok");
                outPUT.put("message","El usuario ha hecho login satisfactoriamente");
                outPUT.put("token",tokenJWTUSER.getString("token"));
            }

        }catch (Exception e){
            outPUT.put("status","error");
            outPUT.put("message","Error procesando el json");
        }finally {
            h.close();
        }
        response = ""+outPUT;
        return Response.status(200).entity(response).build();
    }



    @POST
    @Path("showEpisode")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response showEpisode(String incomingData) throws JSONException, SQLException {

        return Response.status(200).build();
    }

}


