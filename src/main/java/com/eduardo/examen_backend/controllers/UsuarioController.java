package com.eduardo.examen_backend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eduardo.examen_backend.dto.PasswordDTO;
import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.dto.UsuarioDTO;
import com.eduardo.examen_backend.dto.UsuarioRolDTO;
import com.eduardo.examen_backend.exceptions.ErrorResponse;
import com.eduardo.examen_backend.exceptions.NotFoundException;
import com.eduardo.examen_backend.services.UsuarioService;
import com.eduardo.examen_backend.views.RolViews;
import com.eduardo.examen_backend.views.UsuarioViews;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controlador REST encargado de gestionar el ciclo de vida de los usuarios.
 * <p>
 * Proporciona endpoints para el registro, consulta, actualización de datos
 * sensibles (contraseñas) y administración de roles. Implementa seguridad
 * mediante validación de roles administrativos en operaciones críticas.
 * </p>
 * * @author Eduardo
 * 
 * @version 0.01
 */
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
    /**
     * Registra un nuevo usuario en el sistema.
     * * @param usuarioDTO Objeto con la información del usuario a crear.
     * 
     * @return ResponseEntity con el {@link UsuarioDTO} creado y estado 201
     *         (Created).
     */
    @PostMapping
    @JsonView(UsuarioViews.IndiscreetUser.class)
    @Operation(summary = "Registrar un usuario", description = "Crea un usuario nuevo. Se le asignará el rol especificado o uno por defecto.")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UsuarioDTO> save(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        return new ResponseEntity<>(usuarioService.save(usuarioDTO), HttpStatus.CREATED);
    }

    // GET
    // http://localhost:8080/usuarios
    @GetMapping
    @JsonView(UsuarioViews.IndiscreetUser.class)
    @Operation(summary = "Listar todos los usuarios", description = "Devuelve el catálogo completo de usuarios registrados.")
    @ApiResponse(responseCode = "200", description = "Lista devuelta con éxito (puede devolver un array vacío '[]' si no hay registros)")
    public ResponseEntity<List<UsuarioDTO>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());

    }

    // GET
    // http://localhost:8080/usuarios/2
    @GetMapping("/{idUsuario}")
    @JsonView(UsuarioViews.ExtraIndiscreetUser.class)
    @Operation(summary = "Buscar usuario por ID", description = "Devuelve toda la información de un usuario específico.")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "El usuario no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UsuarioDTO> findById(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(usuarioService.findById(idUsuario));
    }

    // PUT MODIFICACIÓN GENERAL
    // http://localhost:8080/usuarios
    @PutMapping
    @JsonView(UsuarioViews.DiscreetUser.class)
    @Operation(summary = "Actualizar datos generales", description = "Modifica los datos básicos del usuario. No usar para contraseñas o roles.")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o correo en uso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "El usuario a modificar no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UsuarioDTO> update(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.update(usuarioDTO));
    }

    // PUT MODIFICACIÓN CONTRASEÑA
    // PUT http://localhost:8080/usuarios/5/password
    @PutMapping("/{idUsuario}/password")
    @JsonView(UsuarioViews.DiscreetUser.class)
    @Operation(summary = "Cambiar contraseña", description = "Actualiza la contraseña de un usuario exigiendo la contraseña antigua por seguridad.")
    @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o la contraseña antigua es incorrecta", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "El usuario no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UsuarioDTO> cambiarContrasenha(@PathVariable Integer idUsuario,
            @Valid @RequestBody PasswordDTO passwordChangePetition) {
        return ResponseEntity.ok(usuarioService.changeContrasenha(idUsuario, passwordChangePetition));
    }

    // PUT AÑADIR ROL POR ADMIN
    // PUT http://localhost:8080/usuarios/5/roles?idRol=2&idAdmin=1
    /**
     * Asigna un rol adicional a un usuario. Requiere permisos de administrador.
     * * @param idUsuario Usuario que recibirá el rol.
     * 
     * @param idRol   ID del rol a asignar.
     * @param idAdmin ID del usuario que realiza la petición (debe ser
     *                Administrador).
     * @return Usuario con su nueva lista de roles.
     * @throws BadRequestException
     */
    @PutMapping("/{idUsuario}/roles")
    @JsonView(UsuarioViews.ExtraIndiscreetUser.class)
    @Operation(summary = "Añadir rol a usuario", description = "Permite a un administrador asignarle un rol extra a un usuario sin borrar los anteriores.")
    @ApiResponse(responseCode = "200", description = "Rol añadido exitosamente")
    @ApiResponse(responseCode = "400", description = "Acceso denegado (El que hace la petición no es Admin)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "El usuario o el rol no existen", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UsuarioDTO> anhadirRol(@PathVariable Integer idUsuario, @RequestParam Integer idRol,
            @RequestParam Integer idAdmin) {
        return ResponseEntity.ok(usuarioService.anhadirRol(idUsuario, idRol, idAdmin));
    }

    // PUT (BORRADO LÓGICO)
    // http://localhost:8080/usuarios/desactivar/1
    @PutMapping("/desactivar/{idUsuario}")
    @JsonView(UsuarioViews.ExtraIndiscreetUser.class)
    @Operation(summary = "Activar/Desactivar usuario", description = "Realiza un borrado lógico invirtiendo el campo 'activo'.")
    @ApiResponse(responseCode = "200", description = "Estado modificado exitosamente")
    @ApiResponse(responseCode = "404", description = "El usuario no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UsuarioDTO> desactivateUser(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(usuarioService.desactivateUser(idUsuario));
    }

    // GET LISTA DE ROLES DE UN USUARIO
    // http://localhost:8080/usuarios/1/roles
    /**
     * Consulta el catálogo de roles que posee un usuario determinado.
     * <p>
     * Permite auditar qué permisos o etiquetas de seguridad tiene un usuario
     * extrayendo su colección de roles desde la relación ManyToMany.
     * </p>
     * * @param idUsuario Identificador del usuario cuya lista de roles se desea
     * consultar.
     * 
     * @return ResponseEntity con la {@link List} de {@link RolDTO} asignados.
     *         Devuelve 204 si el usuario existe pero no tiene roles, o 404 si el
     *         usuario no existe.
     * @throws NotFoundException
     */
    @GetMapping("{idUsuario}/roles")
    @JsonView(RolViews.IndiscreetRol.class)
    @Operation(summary = "Ver roles de un usuario", description = "Devuelve la lista específica de roles que tiene asignados un usuario.")
    @ApiResponse(responseCode = "200", description = "Roles encontrados")
    @ApiResponse(responseCode = "204", description = "El usuario no tiene roles")
    @ApiResponse(responseCode = "404", description = "El usuario no existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<RolDTO>> findRolByUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(usuarioService.findRolByUsuario(idUsuario));
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
        return ResponseEntity.ok(usuarioService.removeRol(idUsuario, idRol, idAdmin));
    }

}
