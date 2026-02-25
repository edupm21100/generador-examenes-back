package com.eduardo.examen_backend.dto;

public class UsuarioDTOLista {
    private Integer id_usuario;
    private String nombre_usuario;
    private String apellido_usuario;
    private String correo_usuario;
    private String contrasenha_usuario;
    private boolean activo;

    public UsuarioDTOLista() {
    }

    public void setId_usuario(Integer id_usuario){
        this.id_usuario = id_usuario;
    }

    public Integer getId_usuario() {
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
