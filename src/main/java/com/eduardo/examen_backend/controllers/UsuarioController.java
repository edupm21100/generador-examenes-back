package com.eduardo.examen_backend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.dto.UsuarioDTO;
import com.eduardo.examen_backend.dto.UsuarioRolDTO;
import com.eduardo.examen_backend.exception.ErrorResponse;
import com.eduardo.examen_backend.exception.NotFoundException;
import com.eduardo.examen_backend.services.UsuarioService;
import com.eduardo.examen_backend.views.RolViews;
import com.eduardo.examen_backend.views.UsuarioViews;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Gestión integral de usuarios, contraseñas y asignación de roles")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // POST
    // http://localhost:8080/usuarios
    // RECUERDA QUE SPRING SECURITY ESTÁ DESHABILITADO POR AHORA
    @PostMapping
    @JsonView(UsuarioViews.IndiscreetUser.class)
    @Operation(summary = "Registrar un usuario", description = "Crea un usuario nuevo. Se le asignará el rol especificado o uno por defecto.")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UsuarioDTO> save(@RequestBody UsuarioDTO usuarioDTO) {
        return new ResponseEntity<>(usuarioService.save(usuarioDTO), HttpStatus.CREATED);
    }

    // GET
    // http://localhost:8080/usuarios
    @GetMapping
    @JsonView(UsuarioViews.IndiscreetUser.class)
    @Operation(summary = "Listar todos los usuarios", description = "Devuelve el catálogo completo de usuarios registrados.")
    @ApiResponse(responseCode = "200", description = "Lista devuelta con éxito")
    @ApiResponse(responseCode = "204", description = "No hay usuarios registrados")
    public ResponseEntity<List<UsuarioDTO>> findAll() {
        List<UsuarioDTO> usuarioDTOs = usuarioService.findAll();
        if (usuarioDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarioDTOs);
    }

    // GET usuarios por rol
    // http://localhost:8080/2/usuarios
    @GetMapping("/{idRol}/usuarios")
    @JsonView(UsuarioViews.DiscreetUser.class)
    @Operation(summary = "Listar usuarios por Rol", description = "Busca a todos los usuarios que posean un rol específico.")
    @ApiResponse(responseCode = "200", description = "Usuarios encontrados")
    @ApiResponse(responseCode = "204", description = "No hay usuarios con ese rol")
    public ResponseEntity<List<UsuarioDTO>> findByRol(@PathVariable("idRol") Integer idRol) {
        List<UsuarioDTO> usuarioDTOs = usuarioService.findByRol(idRol);
        if (usuarioDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarioDTOs);
    }

    // GET
    // http://localhost:8080/usuarios/2
    @GetMapping("/{idUsuario}")
    @JsonView(UsuarioViews.ExtraIndiscreetUser.class)
    @Operation(summary = "Buscar usuario por ID", description = "Devuelve toda la información de un usuario específico.")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "El usuario no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UsuarioDTO> findById(@PathVariable("idUsuario") Integer idUsuario) {
        UsuarioDTO usuarioDTO = usuarioService.findById(idUsuario).orElseThrow(
                () -> new NotFoundException("Id: " + idUsuario + " no encontrado"));
        return new ResponseEntity<>(usuarioDTO, HttpStatus.OK);
    }

    // PUT MODIFICACIÓN GENERAL
    // http://localhost:8080/usuarios
    @PutMapping
    @JsonView(UsuarioViews.DiscreetUser.class)
    @Operation(summary = "Actualizar datos generales", description = "Modifica los datos básicos del usuario. No usar para contraseñas o roles.")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente")
    @ApiResponse(responseCode = "404", description = "El usuario a modificar no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UsuarioDTO> update(@RequestBody() UsuarioDTO usuarioDTO) {
        return usuarioService.update(usuarioDTO).map(
                ResponseEntity::ok).orElseGet(
                        () -> ResponseEntity.notFound().build());
    }

    // PUT MODIFICACIÓN CONTRASEÑA
    // PUT
    // http://localhost:8080/usuarios/5/contrasenha?contrasenhaNueva=####&contrasenhaVieja=#####
    @PutMapping("/{idUsuario}/contrasenha")
    @JsonView(UsuarioViews.DiscreetUser.class)
    @Operation(summary = "Cambiar contraseña", description = "Actualiza la contraseña de un usuario exigiendo la contraseña antigua por seguridad.")
    @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente")
    @ApiResponse(responseCode = "400", description = "La contraseña antigua es incorrecta", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "El usuario no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UsuarioDTO> cambiarContrasenha(@PathVariable("idUsuario") Integer idUsuario,
            @RequestParam String contrasenhaNueva, @RequestParam String contrasenhaVieja) {
        UsuarioDTO usuarioActualizado = usuarioService.changeContrasenha(idUsuario, contrasenhaNueva, contrasenhaVieja);
        return ResponseEntity.ok(usuarioActualizado);
    }

    // PUT AÑADIR ROL POR ADMIN
    // PUT http://localhost:8080/usuarios/5/roles?idRol=2&idAdmin=1
    @PutMapping("/{idUsuario}/roles")
    @JsonView(UsuarioViews.ExtraIndiscreetUser.class)
    @Operation(summary = "Añadir rol a usuario", description = "Permite a un administrador asignarle un rol extra a un usuario sin borrar los anteriores.")
    @ApiResponse(responseCode = "200", description = "Rol añadido exitosamente")
    @ApiResponse(responseCode = "400", description = "Acceso denegado (El que hace la petición no es Admin)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "El usuario o el rol no existen", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UsuarioDTO> anhadirRol(@PathVariable("idUsuario") Integer idUsuario,
            @RequestParam("idRol") Integer idRol,
            @RequestParam("idAdmin") Integer idAdmin) {
        UsuarioDTO usuarioActualizado = usuarioService.anhadirRol(idUsuario, idRol, idAdmin);
        return ResponseEntity.ok(usuarioActualizado);
    }

    // DELETE
    // http://localhost:8080/usuarios/2
    @DeleteMapping("/{idUsuario}")
    @JsonView(UsuarioViews.NotDiscreetUser.class)
    @Operation(summary = "Eliminar usuario físicamente", description = "Borra un usuario de la BD. Cuidado: puede romper dependencias si tiene registros asociados.")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado con éxito")
    @ApiResponse(responseCode = "404", description = "El usuario no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> deleteById(@PathVariable("idUsuario") Integer idUsuario) {
        if (usuarioService.deleteById(idUsuario)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // PUT (BORRADO LÓGICO)
    // http://localhost:8080/usuarios/desactivar/1
    @PutMapping("/desactivar/{idUsuario}")
    @JsonView(UsuarioViews.ExtraIndiscreetUser.class)
    @Operation(summary = "Activar/Desactivar usuario", description = "Realiza un borrado lógico invirtiendo el campo 'activo'.")
    @ApiResponse(responseCode = "200", description = "Estado modificado exitosamente")
    @ApiResponse(responseCode = "404", description = "El usuario no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UsuarioDTO> desactivateUser(@PathVariable("idUsuario") Integer idUsuario) {
        UsuarioDTO usuarioDTO = usuarioService.desactivateUser(idUsuario);
        return ResponseEntity.ok(usuarioDTO);
    }

    // GET LISTA DE ROLES DE UN USUARIO
    // http://localhost:8080/usuarios/1/roles
    @GetMapping("{idUsuario}/roles")
    @JsonView(RolViews.IndiscreetRol.class)
    @Operation(summary = "Ver roles de un usuario", description = "Devuelve la lista específica de roles que tiene asignados un usuario.")
    @ApiResponse(responseCode = "200", description = "Roles encontrados")
    @ApiResponse(responseCode = "204", description = "El usuario no tiene roles")
    @ApiResponse(responseCode = "404", description = "El usuario no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<RolDTO>> findRolByUsuario(@PathVariable("idUsuario") Integer idUsuario) {
        List<RolDTO> rolDTOs = usuarioService.findRolByUsuario(idUsuario);
        if (rolDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rolDTOs);
    }

    // QUITAR ROL A UN USUARIO
    // http://localhost:8080/usuarios/1/roles/2?idAdmin=1
    @PutMapping("{idUsuario}/roles/{idRol}")
    @Operation(summary = "Quitar rol a usuario", description = "Permite a un administrador removerle un rol específico a un usuario.")
    @ApiResponse(responseCode = "200", description = "Rol retirado exitosamente")
    @ApiResponse(responseCode = "400", description = "Acceso denegado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "El usuario o el rol no existen", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UsuarioRolDTO> removeRol(@PathVariable Integer idUsuario, @PathVariable Integer idRol,
            @RequestParam Integer idAdmin) {
        UsuarioRolDTO usuarioActualizado = usuarioService.removeRol(idUsuario, idRol, idAdmin);
        return ResponseEntity.ok(usuarioActualizado);
    }

}
