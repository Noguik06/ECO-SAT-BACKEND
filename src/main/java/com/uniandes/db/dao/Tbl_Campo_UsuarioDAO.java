package com.uniandes.db.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.uniandes.db.vo.Tbl_campo_usuario;
import com.uniandes.db.vo.Tbl_fase_usuario;

import io.dropwizard.hibernate.AbstractDAO;

@SuppressWarnings(value = { "all" })
public class Tbl_Campo_UsuarioDAO extends AbstractDAO<Tbl_campo_usuario> {

	public Tbl_Campo_UsuarioDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
		// TODO Auto-generated constructor stub
	}


	//Metodo para crear un tramite nuevo
	public long create(Tbl_campo_usuario campo_usuario) {
		currentSession().persist(campo_usuario);
		return persist(campo_usuario).getId_campo_usuario();
	}
	
	//Metodo para traer todas los campos del usuario
	public List<Tbl_campo_usuario> getAllCamposUsuario(Long id_fase_usuario){
		List<Tbl_campo_usuario> list = (List<Tbl_campo_usuario>) currentSession().
				getNamedQuery("com.uniandes.db.vo.Tbl_campo_usuario.findAll")
				.setParameter("id_fase_usuario", id_fase_usuario).list();
		return list;
	}
	
	//Metodo para traer un objeto
	public Tbl_campo_usuario finById(Long id){
		return get(id);
	}
	
	//Metodo para actualizar un valor de un campo
	public void update(Tbl_campo_usuario campo_usuario){
		currentSession().update(campo_usuario);
	}
	
	//Metodo para traer todas los campos del usuario por el id_tramite_usuario
	public List<Tbl_campo_usuario> getAllCamposUsuarioByIdTramite(Long id_tramite_usuario){
		List<Tbl_campo_usuario> list = (List<Tbl_campo_usuario>) currentSession().
				getNamedQuery("com.uniandes.db.vo.Tbl_campo_usuario.findAllByIdTramiteUsuario")
				.setParameter("id_tramite_usuario", id_tramite_usuario).list();
//		List<Tbl_campo_usuario> list  = currentSession().createQuery("SELECT T.id_campo FROM Tbl_campo_usuario T "
//	    			+ "WHERE T.id_tramite_usuario = " + id_tramite_usuario).list();
		return list;
	}
}
