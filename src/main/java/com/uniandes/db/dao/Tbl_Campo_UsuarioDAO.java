package com.uniandes.db.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.uniandes.db.vo.Tbl_campo_usuario;
import com.uniandes.db.vo.Tbl_fase_usuario;

import io.dropwizard.hibernate.AbstractDAO;

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
}
