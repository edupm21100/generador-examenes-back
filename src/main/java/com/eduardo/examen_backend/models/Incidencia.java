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

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(name = "correo_usuario")
    private String correoUsuario;

    public Incidencia() {
        // Constructor vacío exigido por JPA
    }

    public Incidencia(String endpoint, String descripcion, LocalDateTime fecha, String correoUsuario) {
        this.endpoint = endpoint;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.correoUsuario = correoUsuario;
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