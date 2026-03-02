package com.eduardo.examen_backend.dto;

public class UsuarioRolDTO {
    private Integer idUsuario;
    private Integer idRol;

    public UsuarioRolDTO() {
        // CONSTRUCTOR VACÍO POR NECESIDAD DEL DTO
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    


    
}
