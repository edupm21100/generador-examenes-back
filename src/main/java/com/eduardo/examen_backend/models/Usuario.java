package com.eduardo.examen_backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_usuario;
    @Column(name = "nombre_usuario")
    private String nombre_usuario;
    @Column(name = "apellido_usuario")
    private String apellido_usuario;
    @Column(name = "correo_usuario")
    private String correo_usuario;
    @Column(name = "contrasenha_usuario")
    private String contrasenha_usuario;
    @Column(name = "activo")
    private boolean activo;



    public Usuario(String nombre_usuario, String apellido_usuario, String correo_usuario, String contrasenha_usuario, boolean activo) {
        this.nombre_usuario = nombre_usuario;
        this.apellido_usuario = apellido_usuario;
        this.correo_usuario = correo_usuario;
        this.contrasenha_usuario = contrasenha_usuario;
        this.activo = activo;
    }

    //EXIGENCIAS DE HIBERNATE
    public Usuario(){}

//*   *   *   *   *   *   *   *   *   *   *    *GETTERS & SETTERS*   *   *   *   *   *   *   *   *   *   *   *   *   *   *\\
    public int getId_usuario() {
        return id_usuario;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }
    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public String getApellido_usuario() {
        return apellido_usuario;
    }
    public void setApellido_usuario(String apellido_usuario) {
        this.apellido_usuario = apellido_usuario;
    }

    public String getCorreo_usuario() {
        return correo_usuario;
    }
    public void setCorreo_usuario(String correo_usuario) {
        this.correo_usuario = correo_usuario;
    }

    public String getContrasenha_usuario() {
        return contrasenha_usuario;
    }
    public void setContrasenha_usuario(String contrasenha_usuario) {
        this.contrasenha_usuario = contrasenha_usuario;
    }

    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }


    
}
