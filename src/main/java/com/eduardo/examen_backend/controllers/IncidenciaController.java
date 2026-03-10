package com.eduardo.examen_backend.controllers;

import com.eduardo.examen_backend.dto.IncidenciaDTO;
import com.eduardo.examen_backend.services.IncidenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incidencias")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Incidencias", description = "Consulta del historial de errores y trazas del sistema (Solo Admin)")
@SecurityRequirement(name = "bearerAuth")
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    public IncidenciaController(IncidenciaService incidenciaService) {
        this.incidenciaService = incidenciaService;
    }

    // LISTAR TODAS LAS INCIDENCIAS
    @GetMapping
    @Operation(summary = "Listar todas las incidencias", description = "Devuelve el historial completo de errores registrados, incluyendo la traza del stack y el usuario que provocó el error.")
    @ApiResponse(responseCode = "200", description = "Lista de incidencias obtenida correctamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado: No tienes el rol de administrador")
    public ResponseEntity<List<IncidenciaDTO>> findAll() {
        return ResponseEntity.ok(incidenciaService.findAll());
    }

    // BUSCAR INCIDENCIA POR ID
    @GetMapping("/{idIncidencia}")
    @Operation(summary = "Consultar detalle de incidencia", description = "Obtiene la información técnica detallada de una incidencia específica mediante su ID.")
    @ApiResponse(responseCode = "200", description = "Incidencia encontrada y devuelta")
    @ApiResponse(responseCode = "404", description = "La incidencia con ese ID no existe en la base de datos")
    public ResponseEntity<IncidenciaDTO> findById(@PathVariable Integer idIncidencia) {
        return ResponseEntity.ok(incidenciaService.findById(idIncidencia));
    }
}