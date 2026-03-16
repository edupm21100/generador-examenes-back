package com.eduardo.examen_backend.usuarios;

import java.util.List;
import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.eduardo.examen_backend.auth.PasswordDTO;
import com.eduardo.examen_backend.roles.RolDTO;
import com.eduardo.examen_backend.roles.RolViews;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Gestión integral de usuarios, contraseñas y asignación de roles")
@Tag(name = "Usuarios", description = "Gestión integral de usuarios, contraseñas y asignación de roles")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // CREAR USUARIO
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(UsuarioViews.IndiscreetUser.class)
    @Operation(summary = "Registrar un usuario", description = "Crea un usuario nuevo internamente. Requiere rol ADMIN. Se deben validar los campos obligatorios como el correo único.")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Error de validación: Datos inválidos, incompletos o correo ya registrado")
    @ApiResponse(responseCode = "403", description = "Acceso denegado: Se requiere privilegios de administrador")
    public ResponseEntity<UsuarioDTO> save(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        return new ResponseEntity<>(usuarioService.save(usuarioDTO), HttpStatus.CREATED);
    }

    // OBTENER LISTA DE USUARIOS ADMIN
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(UsuarioViews.IndiscreetUser.class)
    @Operation(summary = "Listar todos los usuarios", description = "Devuelve el catálogo completo de usuarios registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa: Lista devuelta")
    @ApiResponse(responseCode = "403", description = "Acceso denegado: Solo administradores pueden listar usuarios")
    public ResponseEntity<List<UsuarioDTO>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    // OBTENER USUARIO EN VIRTUD DE UN ID
    @GetMapping("/{idUsuario}")
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(UsuarioViews.ExtraIndiscreetUser.class)
    @Operation(summary = "Buscar usuario por ID", description = "Recupera los detalles completos de un usuario a través de su identificador numérico.")
    @ApiResponse(responseCode = "200", description = "Usuario localizado")
    @ApiResponse(responseCode = "404", description = "Error: El usuario con el ID proporcionado no existe")
    public ResponseEntity<UsuarioDTO> findById(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(usuarioService.findById(idUsuario));
    }

    // ACTUALIZAR UN USARIO SIENDO ADMIN
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(UsuarioViews.DiscreetUser.class)
    @Operation(summary = "Actualizar datos generales", description = "Modifica nombre, apellidos o correo. No actualiza contraseñas ni roles directamente.")
    @ApiResponse(responseCode = "200", description = "Cambios guardados con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada incorrectos o inconsistentes")
    public ResponseEntity<UsuarioDTO> update(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.update(usuarioDTO));
    }

    // CAMBIAR CONTRASEÑA
    @PutMapping("/me/password")
    @JsonView(UsuarioViews.DiscreetUser.class)
    @Operation(summary = "Cambiar mi propia contraseña", description = "Permite al usuario autenticado cambiar su contraseña actual por una nueva tras validarla.")
    @ApiResponse(responseCode = "200", description = "Contraseña actualizada. Se recomienda volver a loguear")
    @ApiResponse(responseCode = "400", description = "La contraseña antigua no coincide con nuestros registros")
    public ResponseEntity<UsuarioDTO> cambiarMiContrasenha(
            @Valid @RequestBody PasswordDTO passwordChangePetition,
            Principal principal) {
        String correoLogueado = principal.getName();

        return ResponseEntity.ok(usuarioService.changeContrasenha(correoLogueado, passwordChangePetition));
    }

    // ASIGNAR UN ROL A UN USUARIO SI ES ADMIN
    @PutMapping("/{idUsuario}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(UsuarioViews.ExtraIndiscreetUser.class)
    @Operation(summary = "Asignar rol a usuario", description = "Vincula un rol existente a un usuario.")
    @ApiResponse(responseCode = "200", description = "Relación usuario-rol establecida")
    public ResponseEntity<UsuarioDTO> anhadirRol(@PathVariable Integer idUsuario, @RequestParam Integer idRol) {
        return ResponseEntity.ok(usuarioService.anhadirRol(idUsuario, idRol));
    }

    // DESACTIVAR USUARIO
    @PutMapping("/desactivar/{idUsuario}")
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(UsuarioViews.ExtraIndiscreetUser.class)
    @Operation(summary = "Baja/Alta lógica", description = "Invierte el estado 'activo' del usuario sin eliminar sus datos.")
    @ApiResponse(responseCode = "200", description = "Estado conmutado con éxito")
    public ResponseEntity<UsuarioDTO> desactivateUser(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(usuarioService.desactivateUser(idUsuario));
    }

    // OBTENER ROLES DE UN USARIO SI SE ES ADMIN
    @GetMapping("/{idUsuario}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(RolViews.IndiscreetRol.class)
    @Operation(summary = "Obtener roles asignados", description = "Lista todos los roles que posee el usuario consultado.")
    @ApiResponse(responseCode = "200", description = "Lista de roles obtenida")
    public ResponseEntity<List<RolDTO>> findRolByUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(usuarioService.findRolByUsuario(idUsuario));
    }

    // QUITAR UN ROL DE UN USUARIO SIENDO ADMIN
    @PutMapping("/{idUsuario}/roles/{idRol}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Revocar rol", description = "Elimina la vinculación entre un usuario y un rol específico.")
    @ApiResponse(responseCode = "200", description = "Rol retirado correctamente")
    public ResponseEntity<UsuarioRolDTO> removeRol(@PathVariable Integer idUsuario, @PathVariable Integer idRol) {
        return ResponseEntity.ok(usuarioService.removeRol(idUsuario, idRol));
    }
}