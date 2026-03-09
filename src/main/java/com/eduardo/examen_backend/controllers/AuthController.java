package com.eduardo.examen_backend.controllers;

import com.eduardo.examen_backend.dto.LoginDTO;
import com.eduardo.examen_backend.dto.UsuarioDTO;
import com.eduardo.examen_backend.exceptions.UnauthorizedException;
import com.eduardo.examen_backend.models.Usuario;
import com.eduardo.examen_backend.repositories.UsuarioRepository;
import com.eduardo.examen_backend.security.JwtService;
import com.eduardo.examen_backend.services.UsuarioService;
import com.eduardo.examen_backend.views.UsuarioViews;
import com.fasterxml.jackson.annotation.JsonView;

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
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;


    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    @JsonView(UsuarioViews.IndiscreetUser.class)
    public ResponseEntity<UsuarioDTO> register(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO nuevoUsuario = usuarioService.save(usuarioDTO);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @JsonView(UsuarioViews.IndiscreetUser.class)
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO loginDTO) {

        Usuario usuario = usuarioRepository.findByCorreoUsuario(loginDTO.getCorreoUsuario())
                .orElseThrow(() -> new UnauthorizedException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(loginDTO.getContrasenhaUsuario(), usuario.getContrasenhaUsuario())) {
            throw new UnauthorizedException("Credenciales incorrectas");
        }

        String token = jwtService.generateToken(usuario.getCorreoUsuario());
        log.info("Usuario: {} | Acción: Login exitoso en el sistema.", loginDTO.getCorreoUsuario());
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }
}