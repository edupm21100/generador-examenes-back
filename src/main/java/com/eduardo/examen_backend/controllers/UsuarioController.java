package com.eduardo.examen_backend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduardo.examen_backend.dto.UsuarioDTO;
import com.eduardo.examen_backend.exception.NotFoundException;
import com.eduardo.examen_backend.services.UsuarioService;
import com.eduardo.examen_backend.views.UsuarioViews;
import com.fasterxml.jackson.annotation.JsonView;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // POST
    // http://localhost:8080/usuarios
    // RECUERDA QUE SPRING SECURITY ESTÁ DESHABILITADO POR AHORA
    @PostMapping
    public ResponseEntity<UsuarioDTO> save(@RequestBody UsuarioDTO usuarioDTO) {
        return new ResponseEntity<>(usuarioService.save(usuarioDTO), HttpStatus.CREATED);
    }

    // GET
    // http://localhost:8080/usuarios
    @GetMapping
    @JsonView(UsuarioViews.IndiscreetUser.class)
    public ResponseEntity<List<UsuarioDTO>> findAll() {
        List<UsuarioDTO> usuarioDTOs = usuarioService.findAll();
        if (usuarioDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarioDTOs);
    }

    // GET
    // http://localhost:8080/usuarios/{idUsuario}
    @GetMapping("/{idUsuario}")
    @JsonView(UsuarioViews.IndiscreetUser.class)
    public ResponseEntity<UsuarioDTO> findById(@PathVariable Integer idUsuario) {
        UsuarioDTO usuarioDTO = usuarioService.findById(idUsuario).orElseThrow(
                () -> new NotFoundException("Id: " + idUsuario + " no encontrado"));
        return new ResponseEntity<>(usuarioDTO, HttpStatus.OK);
    }

    // PUT
    // http://localhost:8080/usuarios
    @PutMapping
    public ResponseEntity<UsuarioDTO> update(@RequestBody UsuarioDTO usuarioDTO) {
        return usuarioService.update(usuarioDTO).map(
                ResponseEntity::ok).orElseGet(
                        () -> ResponseEntity.notFound().build());
    }

    // DELETE
    // http://localhost:8080/usuarios/{idUsuario}
    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer idUsuario) {
        if (usuarioService.deleteById(idUsuario)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


}
