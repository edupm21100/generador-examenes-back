package com.eduardo.examen_backend.controllers;

import com.eduardo.examen_backend.dto.IncidenciaDTO;
import com.eduardo.examen_backend.services.IncidenciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incidencias")
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    public IncidenciaController(IncidenciaService incidenciaService) {
        this.incidenciaService = incidenciaService;
    }

    // GET: http://localhost:8080/incidencias
    @GetMapping
    public ResponseEntity<List<IncidenciaDTO>> findAll() {
        // Directo al ok(), devolviendo 200 siempre (aunque la lista esté vacía)
        return ResponseEntity.ok(incidenciaService.findAll());
    }

    // GET: http://localhost:8080/incidencias/1
    @GetMapping("/{idIncidencia}")
    public ResponseEntity<IncidenciaDTO> findById(@PathVariable Integer idIncidencia) {
        // Si no existe, el Service lanzará NotFoundException y tu GlobalExceptionHandler hará el resto
        return ResponseEntity.ok(incidenciaService.findById(idIncidencia));
    }
}