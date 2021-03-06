package com.uniandes.db.vo;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "tbl_tramite_usuario")
@NamedQueries(
	    {
	    	@NamedQuery(name = "com.uniandes.db.vo.Tbl_tramite_usuario.findAll", query = "SELECT T FROM Tbl_tramite_usuario T  ORDER BY T.estado, T.nombre")
	    }
	)
public class Tbl_tramite_usuario implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_tramite_usuario")
	private Long id_tramite_usuario;
	
	@Column(name = "id_tramite")
	private Long id_tramite;
	
	//llave foranea usuario propietario del tramite
	@Column(name = "id_usuario_ciudadano")
	private String id_usuario_ciudadano;

	//llave foranea usuario funcionario asignado al tramite
	@Column(name = "id_usuario_funcionario")
	private String id_usuario_funcionario;
	
	
	//0 - activo  1 - cerrado
	@Column(name = "estado")
	private int estado;
	
	@Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
	
	@Column(name="nombre")
	private String nombre;
	
	@Column(name="descripcion")
	private String descripcion;
	

	public Long getId_tramite_usuario() {
		return id_tramite_usuario;
	}

	public void setId_tramite_usuario(Long id_tramite_usuario) {
		this.id_tramite_usuario = id_tramite_usuario;
	}

	public Long getId_tramite() {
		return id_tramite;
	}

	public void setId_tramite(Long id_tramite) {
		this.id_tramite = id_tramite;
	}

	public String getId_usuario_ciudadano() {
		return id_usuario_ciudadano;
	}

	public void setId_usuario_ciudadano(String id_usuario_ciudadano) {
		this.id_usuario_ciudadano = id_usuario_ciudadano;
	}

	public String getId_usuario_funcionario() {
		return id_usuario_funcionario;
	}

	public void setId_usuario_funcionario(String id_usuario_funcionario) {
		this.id_usuario_funcionario = id_usuario_funcionario;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estadoactivo) {
		this.estado = estadoactivo;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

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
}
