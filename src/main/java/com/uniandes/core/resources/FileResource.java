package com.uniandes.core.resources;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import javax.ws.rs.Consumes;
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

import com.codahale.metrics.annotation.Timed;


@Path("/fileResource")
@Produces({MediaType.APPLICATION_JSON})
public class FileResource {
    private DBI generalDAO;

    public FileResource(DBI generalDAO) {
        this.generalDAO = generalDAO;
    }

    @Timed
    @POST
    @Path("uploadFile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile() throws IOException {
        // TODO: uploadFileLocation should come from config.yml
        String uploadedFileLocation = "C:/Users/Juan/Pictures/uploads/";
        
        String output = "File uploaded to : " + uploadedFileLocation;
        return Response.ok(output).build();
    }
    
    //Metodo para traer los archivos que se han subido
    @GET
    @Path("showFiles")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getRegisteredUsers() throws JSONException, SQLException, ParseException {
        String response;
        //Abrimos la conexion
        Handle h = generalDAO.open();
        
        //Json de salida
        JSONObject outPUT = new JSONObject();
        try {
            //Json de entrada
//            JSONObject inPUT = new JSONObject(incomingData);
            //Sacamos el body
//            JSONObject body = new JSONObject(inPUT.getString("body"));
            //Validamos que el usuario conincida
            //Obtenemos el id del usuario
//            String cedula = body.getString("user_id");
            //Obtenemos el password
//            String password = body.getString("password");
            String query = "SELECT * FROM archivos" ;
           
            Statement stmt = null;
            stmt = h.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            JSONArray jsonArray = new JSONArray();
            while(rs.next()){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("idarchivo", rs.getString("idarchivo"));
                jsonObject.put("nombre", rs.getString("nombre"));
                jsonArray.put(jsonObject);
            }
            //Rellenamos el output de salida
            outPUT.put("archivos", jsonArray);
        }catch (Exception e){
        	response = ""+outPUT;
            outPUT.put("status","error");
            outPUT.put("errormessage", "Error trayendo los archivos cargados");
            outPUT.put("message","Error trayendo los archivos cargados");
            return Response.status(400).entity(response).build();
        }finally {
            h.close();
        }
        response = ""+outPUT;
        return Response.status(200).entity(response).build();
    }

}


