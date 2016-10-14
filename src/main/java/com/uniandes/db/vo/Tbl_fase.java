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
@Table(name = "tbl_fase")
@NamedQueries(
	    {
	    	@NamedQuery(name = "com.uniandes.db.vo.Tbl_fase.findAllByTrammite", query = "SELECT T FROM Tbl_fase T WHERE "
	    			+ "T.estado <> 1 "
	    			+ "AND T.id_tramite =:idtramite "
	    			+ "ORDER BY T.orden")
	    }
	)
public class Tbl_fase implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_fase")
	private Long id_fase;
	
	@Column(name = "orden")
	private int orden;
	
	@Column(name = "tipousuario")
	private int tipousuario;
	
	@Column(name = "id_tramite")
	private Long id_tramite;
	
	@Column(name = "estado")
	private int estado;

	public Long getId_fase() {
		return id_fase;
	}

	public void setId_fase(Long id_fase) {
		this.id_fase = id_fase;
	}

	public int getOrden() {
		return orden;
	}

	public void setOrden(int orden) {
		this.orden = orden;
	}

	public int getTipousuario() {
		return tipousuario;
	}

	public void setTipousuario(int tipousuario) {
		this.tipousuario = tipousuario;
	}

	public Long getId_tramite() {
		return id_tramite;
	}

	public void setId_tramite(Long id_tramite) {
		this.id_tramite = id_tramite;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}
	
}
