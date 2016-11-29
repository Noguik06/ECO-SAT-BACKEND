package com.uniandes.db.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.uniandes.db.vo.Tbl_campo;
import com.uniandes.db.vo.Tbl_campo_usuario;

import io.dropwizard.hibernate.AbstractDAO;

@SuppressWarnings("unchecked")
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
	
	//Metodo para crear un tramite nuevo
	public Tbl_campo finById(Long idCampo) {
		return get(idCampo);
	}
	
	//Metodo para traer todas los campos del usuario por la fase
	public List<Tbl_campo> getAllCamposByFase(Long id_fase){
		List<Tbl_campo> list = (List<Tbl_campo>) currentSession().
				getNamedQuery("com.uniandes.db.vo.Tbl_campo.findAllByFase")
				.setParameter("id_fase", id_fase).list();
		return list;
	}
	
	
	//Metodo para traer todas los campos del usuario por el id del tramite
	public List<Tbl_campo> getAllCamposByID(Long id_tramite){
		List<Tbl_campo> list = (List<Tbl_campo>) currentSession().
				getNamedQuery("com.uniandes.db.vo.Tbl_campo.findAllByID")
				.setParameter("id_tramite", id_tramite).list();
		return list;
	}
}
