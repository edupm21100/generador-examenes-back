package com.eduardo.examen_backend.examenes.intentos;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Respuesta individual de un alumno a una pregunta")
public class RespuestaAlumnoDTO {

    @Schema(description = "ID de la pregunta que se está respondiendo", example = "10")
    @NotNull(message = "El ID de la pregunta es obligatorio")
    @JsonView(IntentoViews.IndiscreetTry.class)
    private Integer idPregunta;

    @Schema(description = "ID de la opción marcada. Puede ser null si se dejó en blanco", example = "42")
    @JsonView(IntentoViews.IndiscreetTry.class)
    private Integer idOpcionSeleccionada; 

    @Schema(description = "Indica si el sistema la evaluó como correcta (Solo lectura)", example = "true")
    @JsonView(IntentoViews.IndiscreetTry.class)
    private Boolean esCorrecta;
}