package com.uniandes.db.dao;

import org.hibernate.SessionFactory;

import com.uniandes.db.vo.Tbl_usuario;

import io.dropwizard.hibernate.AbstractDAO;

public class UsuarioDAO extends AbstractDAO<Tbl_usuario>{

	public UsuarioDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
		// TODO Auto-generated constructor stub
	}
	
	
	public Tbl_usuario findByCedula(String cedula) {
		return (Tbl_usuario) currentSession().getNamedQuery("com.uniandes.db.vo.Tbl_usuario.findByCedula").setParameter("cedula", cedula).uniqueResult();
    }
}
