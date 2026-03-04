package com.eduardo.examen_backend.dto;

import com.eduardo.examen_backend.views.RolViews;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Objeto de transferencia de datos (DTO) que representa un Rol o Nivel de Acceso")
public class RolDTO {
    @Schema(description = "Identificador único del rol", example = "1")
    private Integer idRol;
    @Schema(description = "Nombre oficial del rol", example = "ADMINISTRADOR")
    private String nombreRol;
    @Schema(description = "Estado actual del rol (Si es false, nadie con este rol podrá operar)", example = "true")
    private boolean activo = true;

    public RolDTO() {
        //VACÍO POR NECESIDAD DEL DTO
    }

    
    @JsonView(RolViews.ExraIndiscreetRol.class)
    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    @JsonView(RolViews.IndiscreetRol.class)
    public Integer getIdRol() {
        return idRol;
    }

    @JsonView(RolViews.DiscreetRol.class)
    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    
    @JsonView(RolViews.IndiscreetRol.class)
    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

}
