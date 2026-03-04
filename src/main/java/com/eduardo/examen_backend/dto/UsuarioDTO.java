package com.eduardo.examen_backend.dto;

import com.eduardo.examen_backend.views.UsuarioViews;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Schema(description = "Objeto de transferencia de datos (DTO) que representa a un Usuario en el sistema")
public class UsuarioDTO {

    @Schema(description = "Identificador único del usuario (Autogenerado por la BD)", example = "1")
    private Integer idUsuario;

    @Schema(description = "Nombre de pila del usuario", example = "Eduardo")
    @NotBlank(message = "El nombre del usuario no puede estar vacío")
    @Size(min= 2, max=255, message="El nombre debe tener entre 2 y 255 carácteres")
    private String nombreUsuario;

    @Schema(description = "Apellidos del usuario", example = "Pérez")
    @NotBlank(message = "El apellido del usuario no puede estar vacío")
    @Size(min= 2, max=255, message="El apellido debe tener entre 2 y 255 carácteres")
    private String apellidoUsuario;

    @Schema(description = "Dirección de correo electrónico", example = "eduardo@test.com")
    @NotBlank(message = "Correo es un campo obligatorio")
    @Email(message = "Formato de correo no valido")
    private String correoUsuario;

    @Schema(description = "Contraseña en texto plano (Solo se usa al recibir datos, nunca se devuelve por seguridad)", example = "MiPasswordSeguro123")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasenhaUsuario;

    @Schema(description = "Indica si el usuario tiene permiso para acceder al sistema", example = "true")
    private boolean activo = true;
    
    // ATRIBUTO NECESARIO PARA ASIGNAR ROL
    @Schema(description = "ID del rol inicial que se le asignará al crear (Opcional, por defecto es 3)", example = "2")
    private Integer idRol;

    public UsuarioDTO() {
        //POR CUESTIONES DTO, ESTE CONSTRUCTOR ESTÁ VACÍO
    }

    // SETTER
    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    @JsonView(UsuarioViews.NotDiscreetUser.class)
    public Integer getIdUsuario() {
        return idUsuario;
    }

    @JsonView(UsuarioViews.DiscreetUser.class)
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    // SETTER
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    @JsonView(UsuarioViews.DiscreetUser.class)
    public String getApellidoUsuario() {
        return apellidoUsuario;
    }

    // SETTER
    public void setApellidoUsuario(String apellidoUsuario) {
        this.apellidoUsuario = apellidoUsuario;
    }

    @JsonView(UsuarioViews.IndiscreetUser.class)
    public String getCorreoUsuario() {
        return correoUsuario;
    }

    // SETTER
    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    @JsonView(UsuarioViews.UltraExtraIndiscreetUser.class)
    public String getContrasenhaUsuario() {
        return contrasenhaUsuario;
    }

    // SETTER
    public void setContrasenhaUsuario(String contrasenhaUsuario) {
        this.contrasenhaUsuario = contrasenhaUsuario;
    }

    @JsonView(UsuarioViews.ExtraIndiscreetUser.class)
    public boolean isActivo() {
        return activo;
    }

    // SETTER
    public void setActivo(boolean activo) {
        this.activo = activo;
    }


    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

}
