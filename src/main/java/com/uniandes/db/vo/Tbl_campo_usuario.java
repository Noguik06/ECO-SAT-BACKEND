package com.uniandes.db.vo;
import java.io.Serializable;
import java.util.Objects;

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


@Cacheable(false)
@Entity
@Table(name = "tbl_campo_usuario")
@NamedQueries(
	    {
	    	@NamedQuery(name = "com.uniandes.db.vo.Tbl_campo_usuario.findAll", query = "SELECT T FROM Tbl_campo_usuario T "
	    			+ "WHERE T.id_fase_usuario = : id_fase_usuario")
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
	
	@Lob
    @Column(name = "valorarchivo")
    private byte[] valorarchivo;

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

	public byte[] getValorarchivo() {
		return valorarchivo;
	}

	public void setValorarchivo(byte[] valorarchivo) {
		this.valorarchivo = valorarchivo;
	}

}
