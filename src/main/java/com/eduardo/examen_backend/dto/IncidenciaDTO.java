package com.eduardo.examen_backend.dto;

import java.time.LocalDateTime;

public class IncidenciaDTO {

    private Integer idIncidencia;
    private String endpoint;
    private String descripcion;
    private LocalDateTime fecha;
    private String correoUsuario;

    public IncidenciaDTO() {
        //VACIO
    }

    public Integer getIdIncidencia() { return idIncidencia; }
    public void setIdIncidencia(Integer idIncidencia) { this.idIncidencia = idIncidencia; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getCorreoUsuario() { return correoUsuario; }
    public void setCorreoUsuario(String correoUsuario) { this.correoUsuario = correoUsuario; }
}