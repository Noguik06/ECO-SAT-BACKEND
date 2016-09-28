package com.uniandes.db.dao;

import org.hibernate.SessionFactory;

import com.uniandes.db.vo.Tbl_campo;

import io.dropwizard.hibernate.AbstractDAO;

public class Tbl_CampoDAO extends AbstractDAO<Tbl_campo> {

	public Tbl_CampoDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
		// TODO Auto-generated constructor stub
	}


	//Metodo para crear un tramite nuevo
	public long create(Tbl_campo tbl_campo) {
		currentSession().persist(tbl_campo);
		return persist(tbl_campo).getId_campo();
	}
}
