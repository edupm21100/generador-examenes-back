package com.eduardo.examen_backend.usuarios;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de confirmación usado al añadir o quitar un rol específico a un usuario")
public class UsuarioRolDTO {
    
    @Schema(description = "ID del usuario modificado", example = "5")
    private Integer idUsuario;
    
    @Schema(description = "ID del rol que fue afectado en la operación", example = "2")
    private Integer idRol;

}