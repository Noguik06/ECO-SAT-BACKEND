package com.uniandes.db.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.uniandes.db.vo.Tbl_fase_usuario;

import io.dropwizard.hibernate.AbstractDAO;

public class Tbl_Fase_UsuarioDAO extends AbstractDAO<Tbl_fase_usuario> {

	public Tbl_Fase_UsuarioDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
		// TODO Auto-generated constructor stub
	}


	//Metodo para crear un tramite nuevo
	public long create(Tbl_fase_usuario tbl_fase_usuario) {
		currentSession().persist(tbl_fase_usuario);
		return persist(tbl_fase_usuario).getId_fase_usuario();
	}
	
	
	//Metodo para traer todas las fase del usuario
	public List<Tbl_fase_usuario> getAllFasesUsuario(Long id_tramite_usuario){
		List<Tbl_fase_usuario> list = (List<Tbl_fase_usuario>) currentSession().
				getNamedQuery("com.uniandes.db.vo.Tbl_fase_usuario.findAll")
				.setParameter("id_tramite_usuario", id_tramite_usuario).list();
		return list;
	}
}
