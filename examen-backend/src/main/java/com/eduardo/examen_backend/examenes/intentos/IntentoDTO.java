package com.eduardo.examen_backend.examenes.intentos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto para enviar y recibir la resolución de un examen")
public class IntentoDTO {

    @Schema(description = "ID del intento (Solo lectura)", example = "1")
    @JsonView(IntentoViews.DiscreetTry.class)
    private Integer idIntento;

    @Schema(description = "ID del examen que se va a realizar", example = "1")
    @NotNull(message = "El ID del examen es obligatorio")
    @JsonView(IntentoViews.DiscreetTry.class)
    private Integer idExamen;

    @Schema(description = "Título del examen (Solo lectura)", example = "Parcial de Historia")
    @JsonView(IntentoViews.DiscreetTry.class)
    private String tituloExamen;

    @Schema(description = "Nota final calculada sobre 10 (Solo lectura)", example = "8.5")
    @JsonView(IntentoViews.DiscreetTry.class)
    private Double nota;

    @Schema(description = "Fecha en la que se realizó (Solo lectura)")
    @JsonView(IntentoViews.DiscreetTry.class)
    private String fechaRealizacion;

    @Schema(description = "Lista de respuestas marcadas por el alumno")
    @Valid
    @JsonView(IntentoViews.IndiscreetTry.class)
    private List<RespuestaAlumnoDTO> respuestas;
}