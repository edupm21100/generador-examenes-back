package com.eduardo.examen_backend.dto;

public class LoginDTO {
    
    private String correoUsuario;
    private String contrasenhaUsuario;

    public LoginDTO() {/*NADA*/}

    // Getters y Setters
    public String getCorreoUsuario() {return correoUsuario;}
    public void setCorreoUsuario(String correoUsuario) {this.correoUsuario = correoUsuario;}

    public String getContrasenhaUsuario() {return contrasenhaUsuario;}
    public void setContrasenhaUsuario(String contrasenhaUsuario) {this.contrasenhaUsuario = contrasenhaUsuario;}
}