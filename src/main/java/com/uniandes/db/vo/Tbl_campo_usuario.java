package com.uniandes.db.vo;
import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Type;


@Cacheable(false)
@Entity
@Table(name = "tbl_campo_usuario")
@NamedQueries(
	    {
	    	@NamedQuery(name = "com.uniandes.db.vo.Tbl_campo_usuario.findAllByFaseUsuario", query = "SELECT T FROM Tbl_campo_usuario T "
	    			+ "WHERE T.id_fase_usuario =:id_fase_usuario"),
	    	@NamedQuery(name = "com.uniandes.db.vo.Tbl_campo_usuario.findAllByIdTramiteUsuario", query = "SELECT T FROM Tbl_campo_usuario T "
	    			+ "WHERE T.id_tramite_usuario =:id_tramite_usuario")
	    }
	)
public class Tbl_campo_usuario implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_campo_usuario")
	private Long id_campo_usuario;
	
	//Llave foranea con id_fase_usuario
	@Column(name = "id_fase_usuario")
	private Long id_fase_usuario;
	
	//Llave foranea con id_campo original
	@Column(name = "id_campo")
	private Long id_campo;
	
	@Column(name = "valortexto")
	private String valortexto;
	
	@Column(name = "nombre")
	private String nombre;
	
    @Column(name = "valorarchivo")
    private String valorarchivo;

	@Column(name="id_tramite_usuario")
	private Long id_tramite_usuario;
	
	@Column(name = "tipo")
	private String tipo;

	public Tbl_campo_usuario(){
//		super()
	}
	
	public Long getId_campo_usuario() {
		return id_campo_usuario;
	}

	public void setId_campo_usuario(Long id_campo_usuario) {
		this.id_campo_usuario = id_campo_usuario;
	}

	public Long getId_fase_usuario() {
		return id_fase_usuario;
	}

	public void setId_fase_usuario(Long id_fase_usuario) {
		this.id_fase_usuario = id_fase_usuario;
	}

	public Long getId_campo() {
		return id_campo;
	}

	public void setId_campo(Long id_campo) {
		this.id_campo = id_campo;
	}

	public String getValortexto() {
		return valortexto;
	}

	public void setValortexto(String valortexto) {
		this.valortexto = valortexto;
	}

	public String getValorarchivo() {
		return valorarchivo;
	}

	public void setValorarchivo(String valorarchivo) {
		this.valorarchivo = valorarchivo;
	}

	public Long getId_tramite_usuario() {
		return id_tramite_usuario;
	}

	public void setId_tramite_usuario(Long id_tramite_usuario) {
		this.id_tramite_usuario = id_tramite_usuario;
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
}
