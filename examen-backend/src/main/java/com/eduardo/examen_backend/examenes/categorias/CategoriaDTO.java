package com.eduardo.examen_backend.examenes.categorias;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "DTO que representa una Categoría o Temática de las preguntas")
public class CategoriaDTO {

    @Schema(description = "ID de la categoría (Autogenerado)", example = "1")
    private Integer idCategoria;

    @Schema(description = "Nombre de la temática", example = "Matemáticas")
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @Schema(description = "Descripción opcional de la temática", example = "Preguntas relacionadas con álgebra y geometría")
    private String descripcion;
}