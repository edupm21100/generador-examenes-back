package com.eduardo.examen_backend.roles;

import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de transferencia de datos (DTO) que representa un Rol o Nivel de Acceso")
public class RolDTO {

    @JsonView({RolViews.ExraIndiscreetRol.class, RolViews.IndiscreetRol.class}) 
    @Schema(description = "Identificador único del rol", example = "1")
    private Integer idRol;

    @JsonView(RolViews.DiscreetRol.class)
    @Schema(description = "Nombre oficial del rol", example = "ADMINISTRADOR")
    private String nombreRol;

    @JsonView(RolViews.IndiscreetRol.class)
    @Schema(description = "Estado actual del rol (Si es false, nadie con este rol podrá operar)", example = "true")
    @Builder.Default
    private Boolean activo = true;

}