package com.eduardo.examen_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de confirmación usado al añadir o quitar un rol específico a un usuario")
public class UsuarioRolDTO {
    @Schema(description = "ID del usuario modificado", example = "5")
    private Integer idUsuario;
    @Schema(description = "ID del rol que fue afectado en la operación", example = "2")
    private Integer idRol;

    public UsuarioRolDTO() {
        // CONSTRUCTOR VACÍO POR NECESIDAD DEL DTO
    }

    public Integer getIdUsuario() {return idUsuario;}
    public void setIdUsuario(Integer idUsuario) {this.idUsuario = idUsuario;}

    public Integer getIdRol() {return idRol;}
    public void setIdRol(Integer idRol) {this.idRol = idRol;}

    


    
}
