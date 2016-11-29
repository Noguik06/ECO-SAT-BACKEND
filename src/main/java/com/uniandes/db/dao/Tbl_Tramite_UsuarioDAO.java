package com.uniandes.db.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.uniandes.db.vo.Tbl_tramite;
import com.uniandes.db.vo.Tbl_tramite_usuario;
import com.uniandes.db.vo.Tbl_usuario;

import io.dropwizard.hibernate.AbstractDAO;

@SuppressWarnings(value = { "all" })
public class Tbl_Tramite_UsuarioDAO extends AbstractDAO<Tbl_tramite_usuario> {

	public Tbl_Tramite_UsuarioDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
		// TODO Auto-generated constructor stub
	}


	//Metodo para crear una solicitud de tramite de un ciudadano
	public long create(Tbl_tramite_usuario tbl_tramite_usuario) {
		currentSession().persist(tbl_tramite_usuario);
		return persist(tbl_tramite_usuario).getId_tramite_usuario();
	}
	
	public void update(Tbl_tramite_usuario tbl_tramite_usuario) {
		currentSession().update(tbl_tramite_usuario);
	}
	
	
	
	
	//Metodo para traer un objeto tipo tbl_tramite_usuario
	public Tbl_tramite_usuario findById(Long idTramiteUsuario){
		return get(idTramiteUsuario);
	}
	
	//Metodo para traer todos los tramites que están en proceso
	public List<Tbl_tramite_usuario> getAllRequestProcedures(){
		return ((List<Tbl_tramite_usuario>) currentSession().getNamedQuery("com.uniandes.db.vo.Tbl_tramite_usuario.findAll").list());
	}
	
	//Metodo para traer todos los tramites que están en proceso
	public List<Tbl_tramite_usuario> getAllRequestProceduresByCitizen(String idusuario){
		return ((List<Tbl_tramite_usuario>) currentSession().createQuery("SELECT T FROM Tbl_tramite_usuario T WHERE T.id_usuario_ciudadano = '" + idusuario 
				+ "' ORDER BY T.estado, T.nombre").list());
	}
	
	public List<Tbl_tramite_usuario> getAllRequestProceduresByFunctionary(String idusuario){
		return ((List<Tbl_tramite_usuario>) currentSession().createQuery("SELECT T FROM Tbl_tramite_usuario T WHERE T.id_usuario_funcionario = '" + idusuario 
				+ "' ORDER BY T.estado, T.nombre").list());
	}
	
	//Metodo para borrar todas las 
	public void deleteRequestProceduresByUserId(String idusuario){
		currentSession().createQuery("UPDATE Tbl_tramite_usuario T SET T.estado = 3 WHERE T.estado <> 2 AND T.id_usuario_ciudadano =  '" + idusuario + "'").executeUpdate();
	}
	
	//Metodo para eliminar un userprocedure por el id del procedure
	public void deleteRequestProcedureById(Long id_tramite_usuario){
		currentSession().createQuery("UPDATE Tbl_tramite_usuario T SET T.estado = 3 WHERE T.id_tramite_usuario =  " + id_tramite_usuario).executeUpdate();
	}
}
