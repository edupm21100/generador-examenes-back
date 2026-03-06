package com.eduardo.examen_backend.controllers;

import com.eduardo.examen_backend.dto.IncidenciaDTO;
import com.eduardo.examen_backend.services.IncidenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incidencias")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Incidencias", description = "Gestión de incidencias del sistema (Solo Admin)")
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    public IncidenciaController(IncidenciaService incidenciaService) {
        this.incidenciaService = incidenciaService;
    }

    // LISTAR TODAS LAS INCIDENCIAS
    @GetMapping
    @Operation(summary = "Listar todas las incidencias", description = "Devuelve el catálogo completo de incidencias.")
    @ApiResponse(responseCode = "200", description = "Lista devuelta con éxito")
    @ApiResponse(responseCode = "403", description = "Acceso denegado (No es Admin)")
    public ResponseEntity<List<IncidenciaDTO>> findAll() {
        return ResponseEntity.ok(incidenciaService.findAll());
    }

    // BUSCAR INCIDENCIA POR ID
    @GetMapping("/{idIncidencia}")
    @Operation(summary = "Buscar incidencia por ID", description = "Devuelve toda la información de una incidencia específica.")
    @ApiResponse(responseCode = "200", description = "Incidencia encontrada")
    @ApiResponse(responseCode = "403", description = "Acceso denegado (No es Admin)")
    @ApiResponse(responseCode = "404", description = "La incidencia no existe")
    public ResponseEntity<IncidenciaDTO> findById(@PathVariable Integer idIncidencia) {
        return ResponseEntity.ok(incidenciaService.findById(idIncidencia));
    }
}