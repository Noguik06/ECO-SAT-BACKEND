package com.uniandes.db.vo;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@Entity
@Table(name = "tbl_usuario")
@NamedQueries(
	    {
	    	@NamedQuery(name = "com.uniandes.db.vo.Tbl_usuario.findByCedula", query = "SELECT U FROM Tbl_usuario U WHERE U.cedula = :cedula")
	    }
	)
public class Tbl_usuario {
	@Id
	Long idusuario;
	String nombre;
	
	@Column(name = "cedula", nullable = false)
	String cedula;
	
	@Column(name = "token")
	String token;
	
	@Column(name = "password")
	String password;
	@Column(name = "email")
	String email;
	@Column(name = "telefono")
	String telefono;
	@Column(name = "tipo")
	String tipo;
	@Column(name = "estado")
	Integer estado;
	
	@Column(name = "confirmpassword")
	String confirmpassword;
	public Long getIdusuario() {
		return idusuario;
	}
	public void setIdusuario(Long idusuario) {
		this.idusuario = idusuario;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getCedula() {
		return cedula;
	}
	public void setCedula(String cedula) {
		this.cedula = cedula;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Integer getEstado() {
		return estado;
	}
	public void setEstado(Integer estado) {
		this.estado = estado;
	}
	public String getConfirmpassword() {
		return confirmpassword;
	}
	public void setConfirmpassword(String confirmpassword) {
		this.confirmpassword = confirmpassword;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tbl_usuario)) {
            return false;
        }

        final Tbl_usuario that = (Tbl_usuario) o;

        return Objects.equals(this.idusuario, that.idusuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idusuario);
    }
}
