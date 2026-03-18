package com.eduardo.examen_backend.examenes.preguntas;

import java.util.List;

import com.eduardo.examen_backend.examenes.opciones.OpcionDTO;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Schema(description = "Objeto que representa una Pregunta junto con sus opciones")
public class PreguntaDTO {

    @Schema(description = "ID de la pregunta", example = "1")
    @JsonView(PreguntaViews.DiscreetQuestion.class)
    private Integer idPregunta;

    @Schema(description = "Enunciado de la pregunta", example = "¿Cuál es la capital de Francia?")
    @NotBlank(message = "El enunciado es obligatorio")
    @JsonView(PreguntaViews.DiscreetQuestion.class)
    private String enunciado;

    @Schema(description = "ID de la categoría a la que pertenece", example = "2")
    @NotNull(message = "La pregunta debe pertenecer a una categoría (idCategoria)")
    @JsonView(PreguntaViews.DiscreetQuestion.class)
    private Integer idCategoria;

    @Schema(description = "Nombre de la categoría (Solo lectura)", example = "Geografía")
    @JsonView(PreguntaViews.DiscreetQuestion.class)
    private String nombreCategoria;

    @Schema(description = "Lista de opciones/respuestas")
    @Valid
    @Size(min = 2, message = "La pregunta debe tener al menos 2 opciones")
    @JsonView(PreguntaViews.IndiscreetQuestion.class)
    private List<OpcionDTO> opciones;
}