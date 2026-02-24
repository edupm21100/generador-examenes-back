package com.eduardo.examen_backend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

import com.eduardo.examen_backend.models.Usuario;
import com.eduardo.examen_backend.services.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // POST
    // http://localhost:8080/usuarios
    // RECUERDA QUE SPRING SECURITY ESTÁ DESHABILITADO POR AHORA
    @PostMapping
    public ResponseEntity<Usuario> save(@RequestBody Usuario usuario) {
        return new ResponseEntity<>(usuarioService.save(usuario), HttpStatus.CREATED);
    }

    // GET
    // http://localhost:8080/usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    // GET
    // http://localhost:8080/usuarios/{id_usuario}
    @GetMapping("/{id_usuario}")
    public ResponseEntity<Usuario> findById(@PathVariable Integer id_usuario) {
        Optional<Usuario> usuarioOptional = usuarioService.getById(id_usuario);
        return usuarioOptional.map(ResponseEntity::ok).orElseGet(
                () -> ResponseEntity.notFound().build());
    }

    // PUT
    // http://localhost:8080/usuarios
    @PutMapping
    public ResponseEntity<Usuario> update(@RequestBody Usuario usuario) {
        return usuarioService.update(usuario).map(
                ResponseEntity::ok).orElseGet(
                        () -> ResponseEntity.notFound().build());
    }

    // DELETE
    // http://localhost:8080/usuarios/{id_usuario}
    @DeleteMapping("/{id_usuario}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id_usuario) {
        if (usuarioService.deleteById(id_usuario)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
