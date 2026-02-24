package com.eduardo.examen_backend.dto;

public class RolDTO {
    private Integer id_rol;
    private String nombre_rol;
    private boolean activo;

    public RolDTO() {
    }

    public Integer getId_rol() {
        return id_rol;
    }

    public String getNombre_rol() {
        return nombre_rol;
    }

    public void setNombre_rol(String nombre_rol) {
        this.nombre_rol = nombre_rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

}
