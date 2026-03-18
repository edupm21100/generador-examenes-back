package com.eduardo.examen_backend.examenes.preguntas;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/preguntas")
@RequiredArgsConstructor
@Tag(name = "Preguntas", description = "Gestión del banco de preguntas y sus opciones de respuesta")
@SecurityRequirement(name = "bearerAuth")
public class PreguntaController {

    private final PreguntaService preguntaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @JsonView(PreguntaViews.IndiscreetQuestion.class)
    @Operation(summary = "Listar preguntas", description = "Devuelve todas las preguntas. Se puede filtrar por categoría.")
    public ResponseEntity<List<PreguntaDTO>> listarPreguntas(
            @RequestParam(required = false) Integer idCategoria) {
        
        if (idCategoria != null) {
            return ResponseEntity.ok(preguntaService.obtenerPorCategoria(idCategoria));
        }
        return ResponseEntity.ok(preguntaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR', 'ALUMNO')")
    @JsonView(PreguntaViews.IndiscreetQuestion.class)
    @Operation(summary = "Obtener una pregunta", description = "Recupera una pregunta y sus opciones por su ID.")
    public ResponseEntity<PreguntaDTO> obtenerPregunta(@PathVariable Integer id) {
        return ResponseEntity.ok(preguntaService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @JsonView(PreguntaViews.IndiscreetQuestion.class)
    @Operation(summary = "Crear pregunta", description = "Crea una pregunta junto con su array de opciones.")
    @ApiResponse(responseCode = "201", description = "Pregunta creada exitosamente")
    public ResponseEntity<PreguntaDTO> crearPregunta(@Valid @RequestBody PreguntaDTO dto) {
        return new ResponseEntity<>(preguntaService.crearPregunta(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @Operation(summary = "Eliminar pregunta", description = "Borra la pregunta y todas sus opciones en cascada.")
    public ResponseEntity<Void> eliminarPregunta(@PathVariable Integer id) {
        preguntaService.eliminarPregunta(id);
        return ResponseEntity.noContent().build();
    }
}