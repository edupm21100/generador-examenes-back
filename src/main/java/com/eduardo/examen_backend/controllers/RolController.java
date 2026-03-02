package com.eduardo.examen_backend.controllers;

import java.util.List;

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

import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.exception.NotFoundException;
import com.eduardo.examen_backend.services.RolService;
import com.eduardo.examen_backend.views.RolViews;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    // POST
    // http://localhost:8080/roles
    // RECUERDA QUE SPRING SECURITY ESTÁ DESHABILITADO POR AHORA
    @PostMapping
    public ResponseEntity<RolDTO> save(@RequestBody RolDTO rolDTO) {
        return new ResponseEntity<>(rolService.save(rolDTO), HttpStatus.CREATED);
    }

    // GET
    // http://localhost:8080/roles
    @GetMapping
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
    public ResponseEntity<RolDTO> findById(@PathVariable Integer idRol) {
        RolDTO rolDTO = rolService.findById(idRol).orElseThrow(
                () -> new NotFoundException("Id: " + idRol + " no encontrado"));
        return new ResponseEntity<>(rolDTO, HttpStatus.OK);
    }

    // PUT
    // http://localhost:8080/roles
    @PutMapping
    public ResponseEntity<RolDTO> update(@RequestBody RolDTO rolDTO) {
        return rolService.update(rolDTO).map(
                ResponseEntity::ok).orElseGet(
                        () -> ResponseEntity.notFound().build());
    }

    // DELETE
    // http://localhost:8080/roles/1
    @DeleteMapping("/{idRol}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer idRol) {
        if (rolService.deleteById(idRol)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // PUT (BORRADO LÓGICO)
    // http://localhost:8080/roles/desactivar/1
    @PutMapping("/desactivar/{idRol}")
    @JsonView(RolViews.IndiscreetRol.class)
    public ResponseEntity<RolDTO> desactivateRol(@PathVariable Integer idRol) {
        RolDTO rolDTO = rolService.desactivateRol(idRol);
        return ResponseEntity.ok(rolDTO);
    }
    
}
