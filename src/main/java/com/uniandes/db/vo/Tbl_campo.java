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
@Table(name = "tbl_campo")
@NamedQueries(
	    {
	    	@NamedQuery(name = "com.uniandes.db.vo.Tbl_campo.findAllByFase", query = "SELECT T FROM Tbl_campo T "
	    			+ "WHERE T.id_fase =:id_fase")
	    }
	)
public class Tbl_campo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_campo")
	private Long id_campo;
	
	@Column(name = "nombre")
	private String nombre;
	
	@Column(name = "tipo")
	private String tipo;
	
	@Column(name = "obligatorio")
	private boolean obligatorio;
	
	@Column(name = "orden")
	private int orden;
	
	@Column(name = "tipousuario")
	private String tipousuario;
	
	@Column(name = "id_fase")
	private Long id_fase;
	
	@Column(name = "id_tramite")
	private Long id_tramite;
	

	public Long getId_campo() {
		return id_campo;
	}

	public void setId_campo(Long id_campo) {
		this.id_campo = id_campo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public boolean isObligatorio() {
		return obligatorio;
	}

	public void setObligatorio(boolean obligatorio) {
		this.obligatorio = obligatorio;
	}

	public int getOrden() {
		return orden;
	}

	public void setOrden(int orden) {
		this.orden = orden;
	}

	public String getTipousuario() {
		return tipousuario;
	}

	public void setTipousuario(String tipousuario) {
		this.tipousuario = tipousuario;
	}

	public Long getId_fase() {
		return id_fase;
	}

	public void setId_fase(Long id_fase) {
		this.id_fase = id_fase;
	}

	public Long getId_tramite() {
		return id_tramite;
	}

	public void setId_tramite(Long id_tramite) {
		this.id_tramite = id_tramite;
	}
}
