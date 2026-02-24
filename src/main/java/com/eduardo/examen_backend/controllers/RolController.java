package com.eduardo.examen_backend.controllers;

import java.util.List;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduardo.examen_backend.models.Rol;
import com.eduardo.examen_backend.services.RolService;

@RestController
@RequestMapping("/roles")
public class RolController {

    private RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    // POST
    // http://localhost:8080/roles
    // RECUERDA QUE SPRING SECURITY ESTÁ DESHABILITADO POR AHORA
    @PostMapping
    public ResponseEntity<Rol> save(@RequestBody Rol rol) {
        return new ResponseEntity<>(rolService.save(rol), HttpStatus.CREATED);
    }

    // GET
    // http://localhost:8080/roles
    @GetMapping
    public ResponseEntity<List<Rol>> findAll() {
        return ResponseEntity.ok(rolService.findAll());
    }

    // GET
    // http://localhost:8080/roles/{id_rol}
    @GetMapping("/{id_rol}")
    public ResponseEntity<Rol> findById(@PathVariable Integer id_rol) {
        Optional<Rol> rolOptional = rolService.getById(id_rol);
        return rolOptional.map(ResponseEntity::ok).orElseGet(
                () -> ResponseEntity.notFound().build());
    }

    // PUT
    // http://localhost:8080/roles
    @PutMapping
    public ResponseEntity<Rol> update(@RequestBody Rol rol) {
        return rolService.update(rol).map(
                ResponseEntity::ok).orElseGet(
                        () -> ResponseEntity.notFound().build());
    }

    // DELETE
    // http://localhost:8080/roles/{id_rol}
    @DeleteMapping("/{id_rol}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id_rol) {
        if (rolService.deleteById(id_rol)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
