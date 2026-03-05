package com.eduardo.examen_backend.dto;

import java.time.LocalDateTime;

public class IncidenciaDTO {

    private Integer idIncidencia;
    private String endpoint;
    private String tipo;
    private String clase;
    private String metodo;
    private String traza;
    private LocalDateTime fecha;
    private Integer idUsuario;

    public IncidenciaDTO() {/*VACIO*/}

    // Getters y Setters
    public Integer getIdIncidencia() { return idIncidencia; }
    public void setIdIncidencia(Integer idIncidencia) { this.idIncidencia = idIncidencia; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getClase() { return clase; }
    public void setClase(String clase) { this.clase = clase; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }

    public String getTraza() { return traza; }
    public void setTraza(String traza) { this.traza = traza; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
}