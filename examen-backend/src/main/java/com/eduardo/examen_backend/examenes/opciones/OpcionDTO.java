package com.eduardo.examen_backend.examenes.opciones;

import com.eduardo.examen_backend.examenes.preguntas.PreguntaViews;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Objeto que representa una Opción/Respuesta a una pregunta")
public class OpcionDTO {

    @Schema(description = "ID de la opción (No enviar al crear nuevas)", example = "1")
    @JsonView(PreguntaViews.IndiscreetQuestion.class)
    private Integer idOpcion;

    @Schema(description = "Texto de la respuesta", example = "París")
    @NotBlank(message = "El texto de la opción no puede estar vacío")
    @JsonView(PreguntaViews.DiscreetQuestion.class)
    private String texto;

    @Schema(description = "Indica si es la respuesta correcta", example = "true")
    @JsonView(PreguntaViews.IndiscreetQuestion.class)
    private Boolean esCorrecta;
}