package com.uniandes.experimento.resources;

import com.uniandes.experimento.common.JWT_Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.ResultIterator;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
        JSONObject body = new JSONObject(incomingData);
        
        JSONObject inPUT = new JSONObject(body.getString("body"));
//        //Json de respuesta
        JSONObject outPUT = new JSONObject();
//        //A
        Handle h = generalDAO.open();
        try{
            String nombre = inPUT.getString("name");
            String cedula = inPUT.getString("user_id");
            String email = inPUT.getString("email");
            String password = inPUT.getString("password");

            String query = "SELECT * FROM USUARIOS WHERE cedula = '"+cedula+"'";
            ResultIterator tareasIterator = h.createQuery(query).iterator();
            if(!tareasIterator.hasNext())
            {
                String query_insert = "INSERT INTO USUARIOS (cedula, password, email, nombre, tipo) " +
                        "values ('"+ cedula +"','" + password + "','" + email + "', '" + nombre + "', 2)";
                if(h.createStatement(query_insert).execute()==1){
                    outPUT.put("status","true");
                    outPUT.put("message","El usuario se ha inscrito correctamente");

                    Proceso hilo1 = new Proceso("Hilo 1");
                    // //Enviamos la info
                    hilo1.setMensaje(email);
                    hilo1.start();
                }else{
                    outPUT.put("status","false");
                    outPUT.put("message","Ha ocurrido un error al insertar el usuario");
                }

            }else{
                outPUT.put("status","false");
                outPUT.put("message","El usuario ya se encuentra inscrito");
                String result = ""+outPUT;
                return Response.status(200).entity(result).build();
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
    
    
    // Metodo para guardar en la base de datos
    private class Proceso extends Thread {

        private String email;

        public Proceso(String msg) {
            super(msg);
        }

        public void setMensaje(String msj) throws JSONException {
            this.email = msj;
        }

        public void run(){
            Handle h = generalDAO.open();
            try{
            	// TODO Auto-generated method stub
        		final String username = "juanfnoguera06@gmail.com";
        		final String password = "!)juanito@#$e123";

        		Properties props = new Properties();
        		props.put("mail.smtp.host", "smtp.gmail.com");
        		props.put("mail.smtp.socketFactory.port", "465");
        		props.put("mail.smtp.socketFactory.class",
        				"javax.net.ssl.SSLSocketFactory");
        		props.put("mail.smtp.auth", "true");
        		props.put("mail.smtp.port", "465");

        		Session session = Session.getDefaultInstance(props,
        			new javax.mail.Authenticator() {
        				protected PasswordAuthentication getPasswordAuthentication() {
        					return new PasswordAuthentication("juanfnoguera06@gmail.com","!)juanito@#$e123");
        				}
        			});

        		try {

        			Message message = new MimeMessage(session);
        			message.setFrom(new InternetAddress("juanfnoguera06@gmail.com"));
        			message.setRecipients(Message.RecipientType.TO,
        					InternetAddress.parse(email));
        			message.setSubject("Inscripcion en SAD");
        			message.setText("Bienvenido," +
        					"\n\n Es un gusto tenerlo en el nuevo sistema de Automatizacion!");

        			Transport.send(message);

        			System.out.println("Done");

        		} catch (MessagingException e) {
        			throw new RuntimeException(e);
        		}
            }catch(Exception e){

            }finally{
                h.close();
            }
        }
    }


    //Metodo para validar un login 
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
            //Sacamos el body
            JSONObject body = new JSONObject(inPUT.getString("body"));
            //Validamos que el usuario conincida
            //Obtenemos el id del usuario
            String cedula = body.getString("user_id");
            //Obtenemos el password
            String password = body.getString("password");
            String query = "SELECT * FROM USUARIOS WHERE cedula = '"+cedula+"' AND password = '" + password + "'" ;
            ResultIterator tareasIterator = h.createQuery(query).iterator();
            
            //Creamos un objeto tipo usuario
            JSONObject userObject = new JSONObject();
            
            if(!tareasIterator.hasNext())
            {
                outPUT.put("status","error");
                outPUT.put("message","El usuario y la contrase&ntilde;a no coinciden");
                response = ""+outPUT.toString();
                return Response.status(401).entity(response).build();
            }else{
            	Map object = new HashMap();
            	object = (Map) tareasIterator.next();
            	userObject.put("cedula", object.get("cedula").toString());
            	userObject.put("email", object.get("email").toString());
            	userObject.put("nombre", object.get("nombre").toString());
            	userObject.put("idusuario", object.get("idusuario").toString());
            	userObject.put("tipo", object.get("tipo").toString());
            	
            }

            //Generamos el token del usuario
            JSONObject tokenJWTUSER = JWT_Utility.generarToken(body.toString());
            query = "UPDATE  USUARIOS  set key =  '" + tokenJWTUSER.get("key")  + "', token = '" +
                    tokenJWTUSER.get("token") + "' WHERE cedula = '" + cedula + "' ";

            //Creamos la sentencia y la ejecutamos
            int result =  h.createStatement(query).execute();
            if(result !=1){
                outPUT.put("status","ok");
                outPUT.put("message","Ha ocurrido un error");
                response = ""+outPUT.toString();
                return Response.status(500).entity(response).build();

            }else{
                outPUT.put("status","ok");
                outPUT.put("message","El usuario ha hecho login satisfactoriamente");
                outPUT.put("token",tokenJWTUSER.getString("token"));
                outPUT.put("usuario",userObject);
                
            }

        }catch (Exception e){
            outPUT.put("status","error");
            outPUT.put("message","Error procesando el json");
            response = ""+outPUT;
            return Response.status(500).entity(response).build();
        }finally {
            h.close();
        }
        response = ""+outPUT;
        return Response.status(200).entity(response).build();
    }
    
    //Metodo para traer los usuarios registrados
    @GET
    @Path("getRegisteredUsers/{cedula}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getRegisteredUsers(@PathParam("cedula") String idusuario) throws JSONException, SQLException, ParseException {
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
            String query = "SELECT * FROM usuarios ORDER BY nombre" ;
            if(idusuario != null && !idusuario.equals("null")){
            	query = "SELECT * FROM usuarios where idusuario = '" + idusuario + "'" ;
            }
            Statement stmt = null;
            stmt = h.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            JSONArray jsonArray = new JSONArray();
            while(rs.next()){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("cedula", rs.getString("cedula"));
                jsonObject.put("email", rs.getString("email"));
                jsonObject.put("nombre", rs.getString("nombre"));
                jsonObject.put("idusuario", rs.getLong("idusuario"));
                jsonArray.put(jsonObject);
            }
            //Rellenamos el output de salida
            outPUT.put("usuarios", jsonArray);
        }catch (Exception e){
        	response = ""+outPUT;
            outPUT.put("status","error");
            outPUT.put("errormessage", "Error trayendo los usuarios registrados");
            outPUT.put("message","Error trayendo los usuarios");
            return Response.status(400).entity(response).build();
        }finally {
            h.close();
        }
        response = ""+outPUT;
        return Response.status(200).entity(response).build();
    }

    //Metodo para actualizar un usuario
    @PUT
    @Path("updateUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(String incomingData) throws JSONException, SQLException {
    	
    	//Sacamos el json del request
		JSONObject body = new JSONObject(incomingData);
		//Sacamos el json del body del request
		JSONObject inPUT = new JSONObject(body.getString("body"));
		//Json de respuesta
		JSONObject outPUT = new JSONObject();
		//Abrimos la conexion
		Handle h = generalDAO.open();
		try {
			String idusuario = inPUT.getString("idusuario");
			String nombre = inPUT.getString("name");
			String cedula = inPUT.getString("cedula");
			String email = inPUT.getString("email");
			String password = inPUT.getString("password");
			// Validmos que no haya otro usuariuo con la misma cedula
			String query = "SELECT * FROM USUARIOS WHERE cedula = '" + cedula + "'";
			ResultIterator usuariosIterator = h.createQuery(query).iterator();
			if (usuariosIterator.hasNext()) {
				Map ob = (HashMap) usuariosIterator.next();
				if (ob.get("idusuario").toString().equals(idusuario)) {
					if (password != null && password.trim().length() > 0) {
						String query_update = "update usuarios set  cedula = ? , nombre = ? , email = ?, password = ? "
								+ "where " + "idusuario = ?";
						PreparedStatement statement = h.getConnection().prepareStatement(query_update);
						statement.setString(1, cedula);
						statement.setString(2, nombre);
						statement.setString(3, email);
						statement.setString(4, password);
						statement.setLong(5, Long.parseLong(idusuario));
						if (statement.executeUpdate() != 1) {
							outPUT.put("status", "false");
							outPUT.put("message", "Ha ocurrido un error al actualizar el usuario");
							String result = "" + outPUT;
							return Response.status(500).entity(result).build();
						}
					} else {
						String query_update = "update usuarios set  cedula = ? , nombre = ? , email = ? where "
								+ " idusuario = ?";
						PreparedStatement statement = h.getConnection().prepareStatement(query_update);
						statement.setString(1, cedula);
						statement.setString(2, nombre);
						statement.setString(3, email);
						statement.setLong(4, Long.parseLong(idusuario));
						if (statement.executeUpdate() != 1) {
							outPUT.put("status", "false");
							outPUT.put("message", "Ha ocurrido un error al actualizar el usuario");
							String result = "" + outPUT;
							return Response.status(500).entity(result).build();
						}
					}
				} else {
					outPUT.put("status", "false");
					outPUT.put("message", "No se ha podido realizar la actualización. Ya existe otro usuario con esa cedula");
					String result = "" + outPUT;
					return Response.status(401).entity(result).build();
				}
			} else {
				if (password != null && password.trim().length() > 0) {
					String query_update = "update usuarios set  cedula = ? , nombre = ? , email = ?, password = ? "
							+ "where " + "idusuario = ?";
					PreparedStatement statement = h.getConnection().prepareStatement(query_update);
					statement.setString(1, cedula);
					statement.setString(2, nombre);
					statement.setString(3, email);
					statement.setString(4, password);
					statement.setLong(5, Long.parseLong(idusuario));
					if (statement.executeUpdate() != 1) {
						outPUT.put("status", "false");
						outPUT.put("message", "Ha ocurrido un error al actualizar el usuario");
						String result = "" + outPUT;
						return Response.status(500).entity(result).build();
					}
				} else {
					String query_update = "update usuarios set  cedula = ? , nombre = ? , email = ? where "
							+ " idusuario = ?";
					PreparedStatement statement = h.getConnection().prepareStatement(query_update);
					statement.setString(1, cedula);
					statement.setString(2, nombre);
					statement.setString(3, email);
					statement.setLong(4, Long.parseLong(idusuario));
					if (statement.executeUpdate() != 1) {
						outPUT.put("status", "false");
						outPUT.put("message", "Ha ocurrido un error al actualizar el usuario");
						String result = "" + outPUT;
						return Response.status(500).entity(result).build();
					}
				}
			}
		} catch (Exception e) {
			outPUT.put("status", "false");
			outPUT.put("message", "Ha ocurrido un error al insertar el usuario");
			String result = "" + outPUT;
			return Response.status(500).entity(result).build();
		} finally {
			//Cerramos la conexion
			h.close();
		}

		outPUT.put("status", "ok");
		outPUT.put("message", "El usuario se ha actualizado satisfactoriamente");
		String result = "" + outPUT;
		return Response.status(200).entity(result).build();
    }

}


