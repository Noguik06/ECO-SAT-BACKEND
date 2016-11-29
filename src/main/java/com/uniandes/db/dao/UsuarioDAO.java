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
	
	public Tbl_usuario findById(String idusuario) {
		return get(idusuario);
    }
	
	//Desactivar usuario
	public void deleteUser(String idusuario) {
		currentSession().getNamedQuery("com.uniandes.db.vo.Tbl_usuario.deleteById").setParameter("idusuario", idusuario).executeUpdate();

    }
	
	public void updateUser(Tbl_usuario tbl_usuario) {
		currentSession().update(tbl_usuario);
	}
	
	public void updateIdUsuario(String id_tbl_usuario, String new_id_tbl_usuario){
		currentSession().createQuery("UPDATE Tbl_usuario T SET T.idusuario = '" + new_id_tbl_usuario + "' WHERE T.idusuario = '" + id_tbl_usuario + "'").executeUpdate();
	}
}
