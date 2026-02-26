package com.eduardo.examen_backend.dto;

import com.eduardo.examen_backend.views.UsuarioViews;
import com.fasterxml.jackson.annotation.JsonView;

public class UsuarioDTO {
    private Integer idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;
    private String correoUsuario;
    private String contrasenhaUsuario;
    private boolean activo;

    public UsuarioDTO() {
    }

    //SETTER
    public void setIdUsuario(Integer idUsuario){
        this.idUsuario = idUsuario;
    }

    @JsonView(UsuarioViews.IndiscreetUser.class)
    public Integer getIdUsuario() {
        return idUsuario;
    }

    @JsonView(UsuarioViews.DiscreetUser.class)
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    //SETTER
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    @JsonView(UsuarioViews.DiscreetUser.class)
    public String getApellidoUsuario() {
        return apellidoUsuario;
    }

    //SETTER
    public void setApellidoUsuario(String apellidoUsuario) {
        this.apellidoUsuario = apellidoUsuario;
    }

    @JsonView(UsuarioViews.IndiscreetUser.class)
    public String getCorreoUsuario() {
        return correoUsuario;
    }

    //SETTER
    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    @JsonView(UsuarioViews.ExtraIndiscreetUser.class)
    public String getContrasenhaUsuario() {
        return contrasenhaUsuario;
    }

    //SETTER
    public void setContrasenhaUsuario(String contrasenhaUsuario) {
        this.contrasenhaUsuario = contrasenhaUsuario;
    }

    @JsonView(UsuarioViews.ExtraIndiscreetUser.class)
    public boolean isActivo() {
        return activo;
    }

    //SETTER
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

}
