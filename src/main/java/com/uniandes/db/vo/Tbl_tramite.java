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
@Table(name = "tbl_tramite")
@NamedQueries(
	    {
	    	@NamedQuery(name = "com.uniandes.db.vo.Tbl_tramite.findAll", query = "SELECT T FROM Tbl_tramite T WHERE T.estado = 1 ORDER BY T.nombre")
	    }
	)
public class Tbl_tramite implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_tramite")
	private Long id_tramite;
	
	@Column(name = "nombre")
	private String nombre;
	
	@Column(name = "descripcion")
	private String descripcion;
	
	@Column(name = "estado")
	private int estado;
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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
