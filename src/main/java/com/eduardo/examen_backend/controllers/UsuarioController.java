package com.eduardo.examen_backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.eduardo.examen_backend.dto.PasswordDTO;
import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.dto.UsuarioDTO;
import com.eduardo.examen_backend.dto.UsuarioRolDTO;
import com.eduardo.examen_backend.services.UsuarioService;
import com.eduardo.examen_backend.views.RolViews;
import com.eduardo.examen_backend.views.UsuarioViews;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Gestión integral de usuarios, contraseñas y asignación de roles")
public class UsuarioController {
    
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    //CREAR USUARIO
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(UsuarioViews.IndiscreetUser.class)
    @Operation(summary = "Registrar un usuario", description = "Crea un usuario nuevo internamente (Solo Admin).")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos")
    @ApiResponse(responseCode = "403", description = "Acceso denegado (No es Admin)")
    public ResponseEntity<UsuarioDTO> save(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        return new ResponseEntity<>(usuarioService.save(usuarioDTO), HttpStatus.CREATED);
    }

    //OBTENER LISTA DE USUARIOS ADMIN
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(UsuarioViews.IndiscreetUser.class)
    @Operation(summary = "Listar todos los usuarios", description = "Devuelve el catálogo completo de usuarios registrados.")
    @ApiResponse(responseCode = "200", description = "Lista devuelta con éxito")
    @ApiResponse(responseCode = "403", description = "Acceso denegado (No es Admin)")
    public ResponseEntity<List<UsuarioDTO>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    //OBTENER USUARIO EN VIRTUD DE UN ID
    @GetMapping("/{idUsuario}")
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(UsuarioViews.ExtraIndiscreetUser.class)
    @Operation(summary = "Buscar usuario por ID", description = "Devuelve toda la información de un usuario específico.")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "403", description = "Acceso denegado (No es Admin)")
    @ApiResponse(responseCode = "404", description = "El usuario no existe")
    public ResponseEntity<UsuarioDTO> findById(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(usuarioService.findById(idUsuario));
    }

    //ACTUALIZAR UN USARIO SIENDO ADMIN
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(UsuarioViews.DiscreetUser.class)
    @Operation(summary = "Actualizar datos generales", description = "Modifica los datos básicos del usuario. Solo Admin.")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o correo en uso")
    @ApiResponse(responseCode = "403", description = "Acceso denegado (No es Admin)")
    @ApiResponse(responseCode = "404", description = "El usuario a modificar no existe")
    public ResponseEntity<UsuarioDTO> update(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.update(usuarioDTO));
    }

    //CAMBIAR CONTRASEÑA
    @PutMapping("/me/password")
    @JsonView(UsuarioViews.DiscreetUser.class)
    @Operation(summary = "Cambiar mi contraseña", description = "Actualiza la contraseña del usuario logueado.")
    @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente")
    @ApiResponse(responseCode = "400", description = "La contraseña antigua es incorrecta")
    public ResponseEntity<UsuarioDTO> cambiarMiContrasenha(
            @Valid @RequestBody PasswordDTO passwordChangePetition,
            Authentication authentication) { 
        String correoLogueado = authentication.getName(); 
        return ResponseEntity.ok(usuarioService.changeContrasenha(correoLogueado, passwordChangePetition));
    }

    //ASIGNAR UN ROL A UN USUARIO SI ES ADMIN
    @PutMapping("/{idUsuario}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(UsuarioViews.ExtraIndiscreetUser.class)
    @Operation(summary = "Añadir rol a usuario", description = "Asigna un rol extra a un usuario.")
    @ApiResponse(responseCode = "200", description = "Rol añadido exitosamente")
    public ResponseEntity<UsuarioDTO> anhadirRol(@PathVariable Integer idUsuario, @RequestParam Integer idRol) {
        return ResponseEntity.ok(usuarioService.anhadirRol(idUsuario, idRol));
    }

    //DESACTIVAR USUARIO
    @PutMapping("/desactivar/{idUsuario}")
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(UsuarioViews.ExtraIndiscreetUser.class)
    @Operation(summary = "Activar/Desactivar usuario", description = "Realiza un borrado lógico invirtiendo el campo 'activo'.")
    @ApiResponse(responseCode = "200", description = "Estado modificado exitosamente")
    public ResponseEntity<UsuarioDTO> desactivateUser(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(usuarioService.desactivateUser(idUsuario));
    }

    //OBTENER ROLES DE UN USARIO SI SE ES ADMIN
    @GetMapping("/{idUsuario}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(RolViews.IndiscreetRol.class)
    @Operation(summary = "Ver roles de un usuario", description = "Devuelve los roles que tiene asignados un usuario.")
    @ApiResponse(responseCode = "200", description = "Roles encontrados")
    public ResponseEntity<List<RolDTO>> findRolByUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(usuarioService.findRolByUsuario(idUsuario));
    }

    //QUITAR UN ROL DE UN USUARIO SIENDO ADMIN
    @PutMapping("/{idUsuario}/roles/{idRol}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Quitar rol a usuario", description = "Remueve un rol específico a un usuario.")
    @ApiResponse(responseCode = "200", description = "Rol retirado exitosamente")
    public ResponseEntity<UsuarioRolDTO> removeRol(@PathVariable Integer idUsuario, @PathVariable Integer idRol) {
        return ResponseEntity.ok(usuarioService.removeRol(idUsuario, idRol));
    }
}