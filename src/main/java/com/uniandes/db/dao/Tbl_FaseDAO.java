package com.uniandes.db.dao;

import org.hibernate.SessionFactory;

import com.uniandes.db.vo.Tbl_fase;
import com.uniandes.db.vo.Tbl_tramite;

import io.dropwizard.hibernate.AbstractDAO;

public class Tbl_FaseDAO extends AbstractDAO<Tbl_fase> {

	public Tbl_FaseDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
		// TODO Auto-generated constructor stub
	}


	//Metodo para crear un tramite nuevo
	public long create(Tbl_fase tbl_fase) {
		currentSession().persist(tbl_fase);
		return persist(tbl_fase).getId_fase();
	}
}
