package com.uniandes.db.dao;

import org.hibernate.SessionFactory;

import com.uniandes.db.vo.Tbl_tramite;

import io.dropwizard.hibernate.AbstractDAO;

public class Tbl_TramiteDAO extends AbstractDAO<Tbl_tramite> {

	public Tbl_TramiteDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
		// TODO Auto-generated constructor stub
	}


	//Metodo para crear un tramite nuevo
	public long create(Tbl_tramite tbl_tramite) {
		currentSession().persist(tbl_tramite);
		return persist(tbl_tramite).getId_tramite();
	}
}
