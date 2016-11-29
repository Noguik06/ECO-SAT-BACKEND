package com.uniandes.db.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.uniandes.db.vo.Tbl_campo_usuario;
import com.uniandes.db.vo.Tbl_tramite;
import com.uniandes.db.vo.Tbl_usuario;

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
	
	public List<Tbl_tramite> getAllProcedures(){
		return ((List<Tbl_tramite>) currentSession().getNamedQuery("com.uniandes.db.vo.Tbl_tramite.findAll").list());
	}
	
	//traemos el tramite por el id del tramitre
	public Tbl_tramite findById(Long idtramite){
		return get(idtramite);
	}
	
	//Metodo para actualizar un valor de un campo
	public void update(Tbl_tramite tbl_tramite){
		currentSession().update(tbl_tramite);
	}
	
	//Actualizar el estado de los campos
	public void deleteAllFields(Long idTramite){
		currentSession().createQuery("UPDATE Tbl_campo T SET T.estado = false WHERE T.id_tramite = " + idTramite).executeUpdate();
	}
	
	//Metodo para eliminar un userprocedure por el id del procedure
	public void deleteProcedureById(Long id_tramite){
		currentSession().createQuery("UPDATE Tbl_tramite T SET T.estado = 0 WHERE T.id_tramite =  " + id_tramite).executeUpdate();
	}
}
