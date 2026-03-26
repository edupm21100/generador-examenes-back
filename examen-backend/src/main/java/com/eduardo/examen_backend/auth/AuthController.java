package com.eduardo.examen_backend.auth;

import com.eduardo.examen_backend.security.JwtService;
import com.eduardo.examen_backend.shared.exceptions.UnauthorizedException;
import com.eduardo.examen_backend.usuarios.Usuario;
import com.eduardo.examen_backend.usuarios.UsuarioRepository;
import com.eduardo.examen_backend.usuarios.UsuarioService;
import com.eduardo.examen_backend.usuarios.UsuarioViews;
import com.fasterxml.jackson.annotation.JsonView;
import com.eduardo.examen_backend.usuarios.UsuarioDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Servicios de acceso y registro de nuevos usuarios")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
            UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    @JsonView(UsuarioViews.IndiscreetUser.class)
    @Operation(summary = "Registro público", description = "Permite a un usuario anónimo crear su cuenta. Por defecto se le asignará el rol con ID 3 (USER).")
    @ApiResponse(responseCode = "201", description = "Usuario registrado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de registro inválidos o el correo ya existe")
    public ResponseEntity<UsuarioDTO> register(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO nuevoUsuario = usuarioService.save(usuarioDTO);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Valida las credenciales del usuario y devuelve un Token JWT necesario para el resto de peticiones.")
    @ApiResponse(responseCode = "200", description = "Autenticación correcta. Devuelve el token en un JSON")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas (correo o contraseña incorrectos)")
    @JsonView(UsuarioViews.IndiscreetUser.class)
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO loginDTO) {

        Usuario usuario = usuarioRepository.findByCorreoUsuario(loginDTO.getCorreoUsuario())
                .orElseThrow(() -> new UnauthorizedException("Credenciales incorrectas"));

        if (!usuario.isActivo()) {
            log.warn("Usuario: {} | Acción: Intento de login bloqueado (Cuenta inactiva)", loginDTO.getCorreoUsuario());
            throw new UnauthorizedException(
                    "Acceso denegado: Su cuenta está desactivada. Contacte con un Administrador.");
        }

        if (!passwordEncoder.matches(loginDTO.getContrasenhaUsuario(), usuario.getContrasenhaUsuario())) {
            throw new UnauthorizedException("Credenciales incorrectas");
        }

        String token = jwtService.generateToken(usuario.getCorreoUsuario());
        log.info("Usuario: {} | Acción: Login exitoso en el sistema.", loginDTO.getCorreoUsuario());
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }
}