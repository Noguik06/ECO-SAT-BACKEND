package com.uniandes.db.vo;
import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@Entity
@Table(name = "tbl_fase_usuario")
@NamedQueries(
	    {
	    	@NamedQuery(name = "com.uniandes.db.vo.Tbl_fase_usuario.findAll", query = "SELECT T FROM Tbl_fase_usuario T "
	    			+ "WHERE T.id_tramite_usuario = : id_tramite_usuario")
	    }
	)
public class Tbl_fase_usuario implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_fase_usuario")
	private Long id_fase_usuario;
	
	@Column(name = "id_fase")
	private Long id_fase;
	
	@Column(name = "id_tramite_usuario")
	private Long id_tramite_usuario;

	public Long getId_fase_usuario() {
		return id_fase_usuario;
	}

	public void setId_fase_usuario(Long id_fase_usuario) {
		this.id_fase_usuario = id_fase_usuario;
	}

	public Long getId_fase() {
		return id_fase;
	}

	public void setId_fase(Long id_fase) {
		this.id_fase = id_fase;
	}

	public Long getId_tramite_usuario() {
		return id_tramite_usuario;
	}

	public void setId_tramite_usuario(Long id_tramite_usuario) {
		this.id_tramite_usuario = id_tramite_usuario;
	}
	
}
