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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/roles")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Roles", description = "Gestión de los roles de acceso del sistema (Solo Admin)")
@SecurityRequirement(name = "bearerAuth")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    // CREAR ROL
    @PostMapping
    @Operation(summary = "Crear un nuevo rol", description = "Registra un perfil de acceso (ej. MODERADOR) en el sistema. Requiere privilegios de ADMIN.")
    @ApiResponse(responseCode = "201", description = "Rol creado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o nombre de rol duplicado")
    @ApiResponse(responseCode = "403", description = "Acceso denegado: No tienes permisos de administrador")
    public ResponseEntity<RolDTO> save(@RequestBody RolDTO rolDTO) {
        return new ResponseEntity<>(rolService.save(rolDTO), HttpStatus.CREATED);
    }

    // LISTAR TODOS LOS ROLES
    @GetMapping
    @Operation(summary = "Listar todos los roles", description = "Obtiene la lista completa de roles definidos, incluyendo su estado de activación.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa: Lista recuperada")
    @ApiResponse(responseCode = "204", description = "No se encontraron roles en el sistema")
    public ResponseEntity<List<RolDTO>> findAll() {
        return ResponseEntity.ok(rolService.findAll());
    }

    // BUSCAR ROL POR ID
    @GetMapping("/{idRol}")
    @Operation(summary = "Buscar rol por ID", description = "Recupera la información detallada de un rol específico mediante su identificador único.")
    @ApiResponse(responseCode = "200", description = "Rol localizado")
    @ApiResponse(responseCode = "404", description = "El ID proporcionado no corresponde a ningún rol")
    public ResponseEntity<RolDTO> findById(@PathVariable Integer idRol) {
        return ResponseEntity.ok(rolService.findById(idRol));
    }

    // ACTUALIZAR ROL
    @PutMapping
    @Operation(summary = "Actualizar rol", description = "Modifica las propiedades de un rol existente. Se debe enviar el objeto completo en el cuerpo.")
    @ApiResponse(responseCode = "200", description = "Actualización realizada con éxito")
    @ApiResponse(responseCode = "404", description = "No se pudo actualizar: El rol no existe")
    public ResponseEntity<RolDTO> update(@RequestBody RolDTO rolDTO) {
        return ResponseEntity.ok(rolService.update(rolDTO));
    }

    // ACTIVAR / DESACTIVAR ROL
    @PutMapping("/desactivar/{idRol}")
    @JsonView(RolViews.IndiscreetRol.class)
    @Operation(summary = "Conmutar estado del rol", description = "Activa o desactiva un rol (borrado lógico). Los roles desactivados no pueden ser asignados a nuevos usuarios.")
    @ApiResponse(responseCode = "200", description = "Estado del rol actualizado")
    public ResponseEntity<RolDTO> desactivateRol(@PathVariable Integer idRol) {
        return ResponseEntity.ok(rolService.desactivateRol(idRol));
    }

    // OBTENER USUARIOS DE UN DETERMINADO ROL
    @GetMapping("/{idRol}/usuarios")
    @JsonView(UsuarioViews.DiscreetUser.class)
    @Operation(summary = "Listar usuarios por Rol", description = "Obtiene la lista de todos los usuarios que tienen asignado este rol específico.")
    @ApiResponse(responseCode = "200", description = "Usuarios recuperados con éxito")
    @ApiResponse(responseCode = "404", description = "El rol especificado no existe")
    public ResponseEntity<List<UsuarioDTO>> findUsuariosByRol(@PathVariable Integer idRol) {
        return ResponseEntity.ok(rolService.findUsuariosByRol(idRol));
    }
}