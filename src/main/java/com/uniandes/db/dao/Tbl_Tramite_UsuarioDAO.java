package com.uniandes.db.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.uniandes.db.vo.Tbl_tramite;
import com.uniandes.db.vo.Tbl_tramite_usuario;
import com.uniandes.db.vo.Tbl_usuario;

import io.dropwizard.hibernate.AbstractDAO;

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
}
