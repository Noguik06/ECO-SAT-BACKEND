package com.uniandes.core.resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.ResultIterator;

import com.uniandes.core.common.JWT_Utility;
import com.uniandes.db.dao.UsuarioDAO;

import io.dropwizard.hibernate.UnitOfWork;

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
@Produces({ MediaType.APPLICATION_JSON })
public class UserResource {
	private DBI generalDAO;
	private UsuarioDAO usuarioDAO;

	public UserResource(DBI generalDAO, UsuarioDAO usuarioDAO) {
		this.generalDAO = generalDAO;
		this.usuarioDAO = usuarioDAO;
	}

	// Servicio para registrar el usuario
	@POST
	@Path("registerCitizen")
	@UnitOfWork
	// @Consumes(MediaType.APPLICATION_JSON)
	public Response registerUser(String incomingData) throws JSONException, SQLException {
		// Json de entrada
		JSONObject inPUT = new JSONObject(incomingData);
		// Json de respuesta
		JSONObject outPUT = new JSONObject();
		Handle h = generalDAO.open();
		try {
			String nombre = inPUT.getString("nombre");
			String cedula = inPUT.getString("cedula");
			String telefono = inPUT.getString("telefono");
			String email = inPUT.getString("email");
			String tipo = inPUT.getString("tipo");
			String password = inPUT.getString("password");

			// Validamos que el usuario no haya sido creado
			String query = "SELECT * FROM tbl_usuario WHERE cedula = '" + cedula + "'";
			ResultIterator tareasIterator = h.createQuery(query).iterator();
			if (!tareasIterator.hasNext()) {
				String query_insert = "INSERT INTO tbl_usuario (cedula, password, email, nombre, tipo) " + "values ('"
						+ cedula + "','" + password + "','" + email + "', '" + nombre + "', '" + tipo + "')";
				if (h.createStatement(query_insert).execute() == 1) {
					outPUT.put("status", "true");
					outPUT.put("message", "El usuario se ha inscrito correctamente");

					Proceso hilo1 = new Proceso("Hilo 1");
					// //Enviamos la info
					hilo1.setMensaje(email);
					hilo1.start();
				} else {
					outPUT.put("status", "false");
					outPUT.put("message", "Ha ocurrido un error al insertar el usuario");
				}

			} else {
				outPUT.put("status", "false");
				outPUT.put("message", "El usuario ya se encuentra inscrito");
				String result = "" + outPUT;
				return Response.status(403).entity(result).build();
			}
		} catch (Exception e) {
			outPUT.put("status", "false");
			outPUT.put("message", "Ha ocurrido un error al insertar el usuario");
			String result = "" + outPUT;
			return Response.status(500).entity(result).build();
		} finally {
			h.close();
		}
		String result = "" + outPUT;
		return Response.status(200).entity(result).build();
	}

	// Servicio para registrar usuarios desde un usuario con sesion iniciada
	@POST
	@Path("registerOtherUser")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerOtherUser(String incomingData) throws JSONException, SQLException {
		// Abrimos la conexion
		Handle h = generalDAO.open();

		// Json de entrada
		JSONObject body = new JSONObject(incomingData);
		// Json del body de entrada
		JSONObject jsonUser = new JSONObject(body.getString("body"));
		// Json de respuesta
		JSONObject outPUT = new JSONObject();

		// Declaramos variables para validaciones
		String key = "";
		String tokenUsario = "";

		// Declaramos la variable para sacar el token del usuario de la base de
		// datos
		String tokenMessage = jsonUser.getString("token");
		String user_id = jsonUser.getString("user_id");
		// Realizamos el query para traer los usuarios
		String query = "SELECT * FROM USUARIOS WHERE cedula = '" + user_id + "'";
		Statement stmt = null;
		stmt = h.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			tokenUsario = rs.getString("token");
			key = rs.getString("key");
		}

		try {
			JWT_Utility.validarToken(tokenUsario, tokenMessage, key);
		} catch (Exception e) {
			outPUT.put("status", "false");
			outPUT.put("message", "Error validando la identidad del usuario");
			String result = "" + outPUT;
			return Response.status(403).entity(result).build();
		}

		try {
			String nombre = jsonUser.getString("name");
			String cedula = jsonUser.getString("user_id");
			String email = jsonUser.getString("email");
			String tipo = jsonUser.getString("tipo");
			String password = jsonUser.getString("password");

			// Validamos que el usuario no haya sido creado
			query = "SELECT * FROM USUARIOS WHERE cedula = '" + cedula + "'";
			ResultIterator tareasIterator = h.createQuery(query).iterator();
			if (!tareasIterator.hasNext()) {
				String query_insert = "INSERT INTO USUARIOS (cedula, password, email, nombre, tipo) " + "values ('"
						+ cedula + "','" + password + "','" + email + "', '" + nombre + "', '" + tipo + "')";
				if (h.createStatement(query_insert).execute() == 1) {
					outPUT.put("status", "true");
					outPUT.put("message", "El usuario se ha inscrito correctamente");

					// Hilo para envio de emails
					Proceso hilo1 = new Proceso("Hilo 1");
					// //Enviamos la info
					hilo1.setMensaje(email);
					hilo1.start();
				} else {
					outPUT.put("status", "false");
					outPUT.put("message", "Ha ocurrido un error al insertar el usuario");
					String result = "" + outPUT;
					return Response.status(403).entity(result).build();
				}
			} else {
				outPUT.put("status", "false");
				outPUT.put("message", "El usuario ya se encuentra inscrito");
				String result = "" + outPUT;
				return Response.status(403).entity(result).build();
			}
		} catch (Exception e) {
			outPUT.put("status", "false");
			outPUT.put("message", "Ha ocurrido un error al insertar el usuario");
			String result = "" + outPUT;
			return Response.status(500).entity(result).build();
		} finally {
			h.close();
		}
		String result = "" + outPUT;
		return Response.status(200).entity(result).build();
	}

	// Metodo para validar un login
	@POST
	@Path("login")
	// @Consumes(MediaType.APPLICATION_JSON)
	public Response loginUser(String incomingData) throws JSONException, SQLException, ParseException {
		String response;
		// Abrimos la conexion
		Handle h = generalDAO.open();
		// Json de salida
		JSONObject outPUT = new JSONObject();
		try {
			// Json de entrada
			JSONObject body = new JSONObject(incomingData);
			// Validamos que el usuario conincida
			// Obtenemos el id del usuario
			String cedula = body.getString("cedula");
			// Obtenemos el password
			String password = body.getString("password");
			String query = "SELECT * FROM tbl_usuario WHERE cedula = '" + cedula + "' AND password = '" + password
					+ "'";
			ResultIterator tareasIterator = h.createQuery(query).iterator();

			// Creamos un objeto tipo usuario
			JSONObject userObject = new JSONObject();

			if (!tareasIterator.hasNext()) {
				outPUT.put("status", "error");
				outPUT.put("message", "El usuario y la contrase&ntilde;a no coinciden");
				response = "" + outPUT.toString();
				return Response.status(401).entity(response).build();
			} else {
				Map object = new HashMap();
				object = (Map) tareasIterator.next();
				userObject.put("cedula", object.get("cedula").toString());
				userObject.put("email", object.get("email").toString());
				userObject.put("nombre", object.get("nombre").toString());
				// userObject.put("idusuario",
				// object.get("idusuario").toString());
				userObject.put("tipo", object.get("tipo").toString());

			}

			// Generamos el token del usuario
			JSONObject tokenJWTUSER = JWT_Utility.generarToken(body.toString());
			query = "UPDATE  tbl_usuario  set key =  '" + tokenJWTUSER.get("key") + "', token = '"
					+ tokenJWTUSER.get("token") + "' WHERE cedula = '" + cedula + "' ";

			// Creamos la sentencia y la ejecutamos
			int result = h.createStatement(query).execute();
			if (result != 1) {
				outPUT.put("status", "ok");
				outPUT.put("message", "Ha ocurrido un error");
				response = "" + outPUT.toString();
				return Response.status(500).entity(response).build();

			} else {
				outPUT.put("status", "ok");
				outPUT.put("message", "El usuario ha hecho login satisfactoriamente");
				outPUT.put("token", tokenJWTUSER.getString("token"));
				outPUT.put("usuario", userObject);

			}

		} catch (Exception e) {
			outPUT.put("status", "error");
			outPUT.put("message", "Error procesando el json");
			response = "" + outPUT;
			return Response.status(500).entity(response).build();
		} finally {
			h.close();
		}
		response = "" + outPUT;
		return Response.status(200).entity(response).build();
	}

	// Metodo para traer los usuarios registrados
	@GET
	@Path("getRegisteredUsers/{cedula}")
//	@Consumes(MediaType.APPLICATION_JSON)
	public Response getRegisteredUsers(@PathParam("cedula") String cedula)
			throws JSONException, SQLException, ParseException {
		String response;
		// Abrimos la conexion
		Handle h = generalDAO.open();

		// Json de salida
		JSONObject outPUT = new JSONObject();
		try {
	
			String query = "SELECT * FROM tbl_usuario ORDER BY nombre";
			if (cedula != null && !cedula.equals("null")) {
				query = "SELECT * FROM tbl_usuario where cedula = '" + cedula + "'";
			}
			Statement stmt = null;
			stmt = h.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			JSONArray jsonArray = new JSONArray();
			while (rs.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("cedula", rs.getString("cedula"));
				jsonObject.put("email", rs.getString("email"));
				jsonObject.put("nombre", rs.getString("nombre"));
				jsonObject.put("idusuario", rs.getLong("idusuario"));
				jsonArray.put(jsonObject);
			}
			// Rellenamos el output de salida
			outPUT.put("usuarios", jsonArray);
		} catch (Exception e) {
			response = "" + outPUT;
			outPUT.put("status", "error");
			outPUT.put("errormessage", "Error trayendo los usuarios registrados");
			outPUT.put("message", "Error trayendo los usuarios");
			return Response.status(400).entity(response).build();
		} finally {
			h.close();
		}
		response = "" + outPUT;
		return Response.status(200).entity(response).build();
	}

	// Metodo para actualizar un usuario
	@PUT
	@Path("updateUser")
	public Response updateUser(String incomingData) throws JSONException, SQLException {

		// Sacamos el json del request
		JSONObject inPUT = new JSONObject(incomingData);
		// Sacamos el json del body del request
		// JSONObject inPUT = new JSONObject(body.getString("body"));
		// Json de respuesta
		JSONObject outPUT = new JSONObject();
		// Abrimos la conexion
		Handle h = generalDAO.open();
		try {
			String nombre = inPUT.getString("nombre");
			String email = inPUT.getString("email");
			String telefono = inPUT.getString("telefono");
			String cedula = inPUT.getString("cedula");
			String password = inPUT.getString("password");
			// Validmos que no haya otro usuariuo con la misma cedula
			String query = "SELECT * FROM tbl_usuario WHERE cedula = '" + cedula + "'";
			ResultIterator usuariosIterator = h.createQuery(query).iterator();
			if (usuariosIterator.hasNext()) {
				Map ob = (HashMap) usuariosIterator.next();

				if (password != null && password.trim().length() > 0) {
					String query_update = "update tbl_usuario set  cedula = ? , " + "nombre = ?, " + "email = ?, "
							+ "password = ?, " + "telefono = ? " + "where " + "cedula = ?";
					PreparedStatement statement = h.getConnection().prepareStatement(query_update);
					statement.setString(1, cedula);
					statement.setString(2, nombre);
					statement.setString(3, email);
					statement.setString(4, password);
					statement.setString(5, telefono);
					statement.setString(6, cedula);
					if (statement.executeUpdate() != 1) {
						outPUT.put("status", "false");
						outPUT.put("message", "Ha ocurrido un error al actualizar el usuario");
						String result = "" + outPUT;
						return Response.status(500).entity(result).build();
					}
				} else {
					String query_update = "update tbl_usuario set  cedula = ? , nombre = ? , email = ? where "
							+ " idusuario = ?";
					PreparedStatement statement = h.getConnection().prepareStatement(query_update);
					statement.setString(1, cedula);
					statement.setString(2, nombre);
					statement.setString(3, email);
					if (statement.executeUpdate() != 1) {
						outPUT.put("status", "false");
						outPUT.put("message", "Ha ocurrido un error al actualizar el usuario");
						String result = "" + outPUT;
						return Response.status(500).entity(result).build();
					}
				}

			} else {
				if (password != null && password.trim().length() > 0) {
					String query_update = "update tbl_usuario set  cedula = ? , nombre = ? , email = ?, password = ? "
							+ "where " + "idusuario = ?";
					PreparedStatement statement = h.getConnection().prepareStatement(query_update);
					statement.setString(1, cedula);
					statement.setString(2, nombre);
					statement.setString(3, email);
					statement.setString(4, password);
					if (statement.executeUpdate() != 1) {
						outPUT.put("status", "false");
						outPUT.put("message", "Ha ocurrido un error al actualizar el usuario");
						String result = "" + outPUT;
						return Response.status(500).entity(result).build();
					}
				} else {
					String query_update = "update tbl_usuario set  cedula = ? , nombre = ? , email = ? where "
							+ " idusuario = ?";
					PreparedStatement statement = h.getConnection().prepareStatement(query_update);
					statement.setString(1, cedula);
					statement.setString(2, nombre);
					statement.setString(3, email);
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
			// Cerramos la conexion
			h.close();
		}

		outPUT.put("status", "ok");
		outPUT.put("message", "El usuario se ha actualizado satisfactoriamente");
		String result = "" + outPUT;
		return Response.status(200).entity(result).build();
	}

	@POST
	@Path("recoverPassword")
	public Response recoverPassword(String incomingData) throws JSONException, SQLException, ParseException {
		String response;
		// Abrimos la conexion
		Handle h = generalDAO.open();
		// Json de salida
		JSONObject outPUT = new JSONObject();
		try {
			// Json de entrada
			JSONObject body = new JSONObject(incomingData);
			// Obtenemos el id del usuario
			String cedula = body.getString("cedula");
			String query = "SELECT * FROM tbl_usuario WHERE cedula = '" + cedula + "'";
			ResultIterator usuariosIterator = h.createQuery(query).iterator();

			// Creamos un objeto tipo usuario
			JSONObject userObject = new JSONObject();

			if (!usuariosIterator.hasNext()) {
				outPUT.put("status", "error");
				outPUT.put("message", "No existe ningún usuario registrado con esa cédula");
				response = "" + outPUT.toString();
				return Response.status(401).entity(response).build();
			} else {
				Map object = new HashMap();
				object = (Map) usuariosIterator.next();
				String email = object.get("email").toString();

				String[] abecedario = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
						"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

				String clave = "";
				for (int i = 0; i < 6; i++) {
					int primero = (int) Math.round(Math.random() * 2);
					if (primero == 1) {
						int numRandon = (int) Math.round(Math.random() * 26);
						clave += abecedario[numRandon];
					} else {
						clave += (int) Math.round(Math.random() * 26);
					}
				}

				// Realizamos el query del cambio de password del usuario
				query = "UPDATE  tbl_usuario  set password = '" + clave + "' " + "WHERE cedula = '" + cedula + "'";

				PreparedStatement statement = h.getConnection().prepareStatement(query);

				if (statement.executeUpdate() != 1) {
					outPUT.put("status", "false");
					outPUT.put("message", "Ha ocurrido un error al actualizar el usuario");
					String result = "" + outPUT;
					return Response.status(500).entity(result).build();
				} else {
					outPUT.put("status", "ok");
					outPUT.put("message", "Hemos enviado su clave nueva al correo registrado");
					RecoverPasswordT hilo1 = new RecoverPasswordT(email, clave);
					// //Enviamos la info
					hilo1.setMensaje(email, clave);
					hilo1.start();
				}

			}
		} catch (Exception e) {
			outPUT.put("status", "error");
			outPUT.put("message", "Error procesando la petición");
			response = "" + outPUT;
			return Response.status(500).entity(response).build();
		} finally {
			h.close();
		}
		response = "" + outPUT;
		return Response.status(200).entity(response).build();
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

		public void run() {
			Handle h = generalDAO.open();
			try {
				// TODO Auto-generated method stub
				final String username = "ecossatarchidroids@gmail.com";
				final String password = "ucnhyfvygpfkqbsi";

				Properties props = new Properties();
				props.put("mail.smtp.host", "smtp.gmail.com");
				props.put("mail.smtp.socketFactory.port", "465");
				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.port", "465");

				Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("noguik@gmail.com", "nejiydzaapwfrhkt");
					}
				});

				try {

					Message message = new MimeMessage(session);
					message.setFrom(new InternetAddress("juanfnoguera06@gmail.com"));
					message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
					message.setSubject("Inscripcion en SAD");
					message.setText("Bienvenido," + "\n\n Es un gusto tenerlo en el nuevo sistema de Automatizacion!");

					Transport.send(message);

					System.out.println("Done");

				} catch (MessagingException e) {
					throw new RuntimeException(e);
				}
			} catch (Exception e) {

			} finally {
				h.close();
			}
		}
	}

	// Metodo para guardar en la base de datos
	private class RecoverPasswordT extends Thread {

		private String email;
		private String clave;

		public RecoverPasswordT(String email, String password) {
			super(email);
		}

		public void setMensaje(String email, String clave) throws JSONException {
			this.email = email;
			this.clave = clave;
		}

		public void run() {
			Handle h = generalDAO.open();
			try {
				// TODO Auto-generated method stub
				final String username = "juanfnoguera06@gmail.com";
				final String password = "!)juanito@#$e123";

				Properties props = new Properties();
				props.put("mail.smtp.host", "smtp.gmail.com");
				props.put("mail.smtp.socketFactory.port", "465");
				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.port", "465");

				Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("juanfnoguera06@gmail.com", "!)juanito@#$e123");
					}
				});

				try {

					Message message = new MimeMessage(session);
					message.setFrom(new InternetAddress("juanfnoguera06@gmail.com"));
					message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
					message.setSubject("Inscripcion en SAD");
					message.setText("Su password ha sido actualizado," + "\n\n Ahora es el siguiente: " + clave);

					Transport.send(message);

					System.out.println("Done");

				} catch (MessagingException e) {
					throw new RuntimeException(e);
				}
			} catch (Exception e) {

			} finally {
				h.close();
			}
		}
	}
}
