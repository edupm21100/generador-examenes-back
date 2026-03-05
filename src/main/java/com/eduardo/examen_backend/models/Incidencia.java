package com.eduardo.examen_backend.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidencias")
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idIncidencia;

    @Column(nullable = false)
    private String endpoint;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String clase;

    @Column(nullable = false)
    private String metodo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String traza;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    public Incidencia() {}

    public Incidencia(String endpoint, String tipo, String clase, String metodo, String traza, LocalDateTime fecha, Integer idUsuario) {
        this.endpoint = endpoint;
        this.tipo = tipo;
        this.clase = clase;
        this.metodo = metodo;
        this.traza = traza;
        this.fecha = fecha;
        this.idUsuario = idUsuario;
    }

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