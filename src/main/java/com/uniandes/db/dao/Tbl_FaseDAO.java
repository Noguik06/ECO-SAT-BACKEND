package com.uniandes.db.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.uniandes.db.vo.Tbl_fase;

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
	
	
	public List<Tbl_fase> getAllFasesByTramite(Long idtramite){
		return ((List<Tbl_fase>) currentSession()
				.getNamedQuery("com.uniandes.db.vo.Tbl_fase.findAllByTrammite")
				.setParameter("idtramite", idtramite)
				.list());
	}
}
