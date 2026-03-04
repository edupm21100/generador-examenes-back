package com.eduardo.examen_backend.controllers;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.exceptions.ErrorResponse;
import com.eduardo.examen_backend.services.RolService;
import com.eduardo.examen_backend.views.RolViews;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la gestión del catálogo de roles del sistema.
 * <p>
 * Permite la administración de los perfiles de usuario (ADMIN, USER, etc.),
 * incluyendo su creación, actualización y la gestión de su estado activo.
 * </p>
 * * @author Eduardo
 * @version 0.01
 */
@RestController
@RequestMapping("/roles")
@Tag(name = "Roles", description = "Gestión de los roles de acceso del sistema")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    // POST
    // http://localhost:8080/roles
    // RECUERDA QUE SPRING SECURITY ESTÁ DESHABILITADO POR AHORA
    /**
     * Crea un nuevo rol en la base de datos.
     * * @param rolDTO Objeto con los datos del nuevo rol (nombre, descripción, etc.).
     * @return ResponseEntity con el {@link RolDTO} creado y estado 201.
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo rol", description = "Guarda un nuevo rol en la base de datos.")
    @ApiResponse(responseCode = "201", description = "Rol creado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<RolDTO> save(@RequestBody RolDTO rolDTO) {
        return new ResponseEntity<>(rolService.save(rolDTO), HttpStatus.CREATED);
    }

    // GET
    // http://localhost:8080/roles
    /**
     * Recupera todos los roles disponibles en el sistema.
     * * @return Lista de {@link RolDTO}. Devuelve 204 (No Content) si la lista está vacía.
     */
    @GetMapping
    @Operation(summary = "Listar todos los roles", description = "Devuelve una lista completa de todos los roles registrados. Devuelve 204 si no hay ninguno.")
    @ApiResponse(responseCode = "200", description = "Lista devuelta con éxito")
    @ApiResponse(responseCode = "204", description = "No hay roles registrados")
    public ResponseEntity<List<RolDTO>> findAll() {
        List<RolDTO> rolDTOs = rolService.findAll();
        if (rolDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rolDTOs);
    }

    // GET
    // http://localhost:8080/roles/1
    @GetMapping("/{idRol}")
    @Operation(summary = "Buscar rol por ID", description = "Obtiene los detalles de un rol específico mediante su ID.")
    @ApiResponse(responseCode = "200", description = "Rol encontrado")
    @ApiResponse(responseCode = "404", description = "El rol no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<RolDTO> findById(@PathVariable Integer idRol) throws NotFoundException {
        RolDTO rolDTO = rolService.findById(idRol).orElseThrow(
                NotFoundException::new);
        return new ResponseEntity<>(rolDTO, HttpStatus.OK);
    }

    // PUT
    // http://localhost:8080/roles
    @PutMapping
    @Operation(summary = "Actualizar rol", description = "Modifica los datos de un rol existente enviando el objeto completo.")
    @ApiResponse(responseCode = "200", description = "Rol actualizado correctamente")
    @ApiResponse(responseCode = "404", description = "El rol a modificar no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<RolDTO> update(@RequestBody RolDTO rolDTO) {
        return rolService.update(rolDTO).map(
                ResponseEntity::ok).orElseGet(
                        () -> ResponseEntity.notFound().build());
    }

    // PUT (BORRADO LÓGICO)
    // http://localhost:8080/roles/desactivar/1
    /**
     * Cambia el estado de activación de un rol.
     * <p>
     * Si el rol está activo, lo desactiva; si está inactivo, lo activa.
     * </p>
     * * @param idRol ID del rol a modificar.
     * @return El {@link RolDTO} con el nuevo estado de activación.
     * @throws RuntimeException Si el rol no existe en el sistema.
     */
    @PutMapping("/desactivar/{idRol}")
    @JsonView(RolViews.IndiscreetRol.class)
    @Operation(summary = "Activar/Desactivar rol (Borrado lógico)", description = "Invierte el estado 'activo' de un rol sin borrarlo de la base de datos.")
    @ApiResponse(responseCode = "200", description = "Estado modificado exitosamente")
    @ApiResponse(responseCode = "404", description = "El rol no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<RolDTO> desactivateRol(@PathVariable Integer idRol) {
        RolDTO rolDTO = rolService.desactivateRol(idRol);
        return ResponseEntity.ok(rolDTO);
    }

}
