package com.eduardo.examen_backend.examenes;

import java.util.Set;

import com.eduardo.examen_backend.examenes.preguntas.PreguntaDTO;
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
@Schema(description = "Objeto que representa la plantilla de un Examen")
public class ExamenDTO {

    @Schema(description = "ID del examen", example = "1")
    @JsonView(ExamenViews.DiscreetExam.class)
    private Integer idExamen;

    @Schema(description = "Título del examen", example = "Examen Final de Geografía")
    @NotBlank(message = "El título del examen es obligatorio")
    @JsonView(ExamenViews.DiscreetExam.class)
    private String titulo;

    @Schema(description = "Descripción o instrucciones", example = "Tienes 60 minutos. Las fallas no restan.")
    @JsonView(ExamenViews.DiscreetExam.class)
    private String descripcion;

    @Schema(description = "Indica si el examen está visible para los alumnos", example = "true")
    @Builder.Default
    @JsonView(ExamenViews.DiscreetExam.class)
    private boolean activo = true;

    @Schema(description = "Lista de preguntas que componen el examen")
    @JsonView(ExamenViews.IndiscreetExam.class)
    private Set<PreguntaDTO> preguntas;
}