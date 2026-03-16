package com.eduardo.examen_backend.incidencias;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "incidencias")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

}