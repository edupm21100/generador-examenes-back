package com.eduardo.examen_backend.usuarios;

import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
@Schema(description = "Objeto de transferencia de datos (DTO) que representa a un Usuario en el sistema")
public class UsuarioDTO {

    @Schema(description = "Identificador único del usuario (Autogenerado por la BD)", example = "1")
    @JsonView(UsuarioViews.NotDiscreetUser.class)
    private Integer idUsuario;

    @Schema(description = "Nombre de pila del usuario", example = "Eduardo")
    @NotBlank(message = "El nombre del usuario no puede estar vacío")
    @Size(min= 2, max=255, message="El nombre debe tener entre 2 y 255 carácteres")
    @JsonView(UsuarioViews.DiscreetUser.class)
    private String nombreUsuario;

    @Schema(description = "Apellidos del usuario", example = "Pérez")
    @NotBlank(message = "El apellido del usuario no puede estar vacío")
    @Size(min= 2, max=255, message="El apellido debe tener entre 2 y 255 carácteres")
    @JsonView(UsuarioViews.DiscreetUser.class)
    private String apellidoUsuario;

    @Schema(description = "Dirección de correo electrónico", example = "eduardo@test.com")
    @NotBlank(message = "Correo es un campo obligatorio")
    @Email(message = "Formato de correo no valido")
    @JsonView(UsuarioViews.IndiscreetUser.class)
    private String correoUsuario;

    @Schema(description = "Contraseña en texto plano (Solo se usa al recibir datos, nunca se devuelve por seguridad)", example = "MiPasswordSeguro123")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @JsonView(UsuarioViews.UltraExtraIndiscreetUser.class)
    private String contrasenhaUsuario;

    @JsonView(UsuarioViews.ExtraIndiscreetUser.class)
    @Schema(description = "Indica si el usuario tiene permiso para acceder al sistema", example = "true")
    @Builder.Default
    private Boolean activo = true;
    
    // ATRIBUTO NECESARIO PARA ASIGNAR ROL
    @Schema(description = "ID del rol inicial que se le asignará al crear (Opcional, por defecto es 3)", example = "2")
    private Integer idRol;

}
