package com.eduardo.examen_backend.dto;

import com.eduardo.examen_backend.views.RolViews;
import com.fasterxml.jackson.annotation.JsonView;

public class RolDTO {
    private Integer idRol;
    private String nombreRol;
    private boolean activo;

    public RolDTO() {
        //VACÍO POR NECESIDAD DEL DTO
    }

    
    @JsonView(RolViews.ExraIndiscreetRol.class)
    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

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
