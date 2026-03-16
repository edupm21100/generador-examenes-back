package com.eduardo.examen_backend.incidencias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidenciaDTO {

    private Integer idIncidencia;
    private String endpoint;
    private String tipo;
    private String clase;
    private String metodo;
    private String traza;
    private LocalDateTime fecha;
    private Integer idUsuario;

}