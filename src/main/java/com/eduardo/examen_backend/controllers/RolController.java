package com.eduardo.examen_backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.dto.UsuarioDTO;
import com.eduardo.examen_backend.services.RolService;
import com.eduardo.examen_backend.views.RolViews;
import com.eduardo.examen_backend.views.UsuarioViews;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestión del catálogo de roles del sistema.
 * <p>
 * Permite la administración de los perfiles de usuario (ADMIN, USER, etc.),
 * incluyendo su creación, actualización y la gestión de su estado activo.
 * </p>
 * * @author Eduardo
 * * @version 0.01
 */
@RestController
@RequestMapping("/roles")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Roles", description = "Gestión de los roles de acceso del sistema (Solo Admin)")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    // CREAR ROL
    @PostMapping
    @Operation(summary = "Crear un nuevo rol", description = "Guarda un nuevo rol en la base de datos.")
    @ApiResponse(responseCode = "201", description = "Rol creado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "403", description = "Acceso denegado (No es Admin)")
    public ResponseEntity<RolDTO> save(@RequestBody RolDTO rolDTO) {
        return new ResponseEntity<>(rolService.save(rolDTO), HttpStatus.CREATED);
    }

    // LISTAR TODOS LOS ROLES
    @GetMapping
    @Operation(summary = "Listar todos los roles", description = "Devuelve una lista completa de todos los roles registrados.")
    @ApiResponse(responseCode = "200", description = "Lista devuelta con éxito")
    @ApiResponse(responseCode = "204", description = "No hay roles registrados")
    @ApiResponse(responseCode = "403", description = "Acceso denegado (No es Admin)")
    public ResponseEntity<List<RolDTO>> findAll() {
        return ResponseEntity.ok(rolService.findAll());
    }

    // BUSCAR ROL POR ID
    @GetMapping("/{idRol}")
    @Operation(summary = "Buscar rol por ID", description = "Obtiene los detalles de un rol específico mediante su ID.")
    @ApiResponse(responseCode = "200", description = "Rol encontrado")
    @ApiResponse(responseCode = "403", description = "Acceso denegado (No es Admin)")
    @ApiResponse(responseCode = "404", description = "El rol no existe")
    public ResponseEntity<RolDTO> findById(@PathVariable Integer idRol) {
        return ResponseEntity.ok(rolService.findById(idRol));
    }

    // ACTUALIZAR ROL
    @PutMapping
    @Operation(summary = "Actualizar rol", description = "Modifica los datos de un rol existente enviando el objeto completo.")
    @ApiResponse(responseCode = "200", description = "Rol actualizado correctamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado (No es Admin)")
    @ApiResponse(responseCode = "404", description = "El rol a modificar no existe")
    public ResponseEntity<RolDTO> update(@RequestBody RolDTO rolDTO) {
        return ResponseEntity.ok(rolService.update(rolDTO));
    }

    // ACTIVAR / DESACTIVAR ROL
    @PutMapping("/desactivar/{idRol}")
    @JsonView(RolViews.IndiscreetRol.class)
    @Operation(summary = "Activar/Desactivar rol (Borrado lógico)", description = "Invierte el estado 'activo' de un rol.")
    @ApiResponse(responseCode = "200", description = "Estado modificado exitosamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado (No es Admin)")
    @ApiResponse(responseCode = "404", description = "El rol no existe")
    public ResponseEntity<RolDTO> desactivateRol(@PathVariable Integer idRol) {
        return ResponseEntity.ok(rolService.desactivateRol(idRol));
    }

    // OBTENER USUARIOS DE UN DETERMINADO ROL
    @GetMapping("/{idRol}/usuarios")
    @JsonView(UsuarioViews.DiscreetUser.class)
    @Operation(summary = "Listar usuarios por Rol", description = "Devuelve los usuarios que poseen un rol específico.")
    @ApiResponse(responseCode = "200", description = "Lista devuelta con éxito")
    @ApiResponse(responseCode = "403", description = "Acceso denegado (No es Admin)")
    public ResponseEntity<List<UsuarioDTO>> findUsuariosByRol(@PathVariable Integer idRol) {
        return ResponseEntity.ok(rolService.findUsuariosByRol(idRol));
    }
}