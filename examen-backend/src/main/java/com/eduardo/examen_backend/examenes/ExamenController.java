package com.eduardo.examen_backend.examenes;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/examenes")
@RequiredArgsConstructor
@Tag(name = "Exámenes", description = "Gestión de plantillas de exámenes y asignación de preguntas")
@SecurityRequirement(name = "bearerAuth")
public class ExamenController {

    private final ExamenService examenService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @JsonView(ExamenViews.DiscreetExam.class)
    @Operation(summary = "Listar todos los exámenes", description = "Devuelve todos los exámenes (activos e inactivos). Solo Profesores/Admin.")
    public ResponseEntity<List<ExamenDTO>> listarTodos() {
        return ResponseEntity.ok(examenService.obtenerTodos());
    }

    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR', 'ALUMNO')")
    @JsonView(ExamenViews.DiscreetExam.class)
    @Operation(summary = "Listar exámenes activos", description = "Catálogo de exámenes disponibles para que los alumnos los realicen.")
    public ResponseEntity<List<ExamenDTO>> listarActivos() {
        return ResponseEntity.ok(examenService.obtenerActivos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR', 'ALUMNO')")
    @JsonView(ExamenViews.IndiscreetExam.class)
    @Operation(summary = "Obtener detalle del examen", description = "Devuelve el examen junto con todas sus preguntas y opciones.")
    public ResponseEntity<ExamenDTO> obtenerExamen(@PathVariable Integer id) {
        return ResponseEntity.ok(examenService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @JsonView(ExamenViews.DiscreetExam.class)
    @Operation(summary = "Crear plantilla de examen", description = "Crea un examen vacío. El autor será el usuario logueado.")
    public ResponseEntity<ExamenDTO> crearExamen(@Valid @RequestBody ExamenDTO dto, Principal principal) {
        String correoLogueado = principal.getName();
        return new ResponseEntity<>(examenService.crearExamen(dto, correoLogueado), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @JsonView(ExamenViews.DiscreetExam.class)
    @Operation(summary = "Actualizar examen", description = "Modifica título y descripción.")
    public ResponseEntity<ExamenDTO> actualizarExamen(@PathVariable Integer id, @Valid @RequestBody ExamenDTO dto,
            Principal principal) {
        String correoLogueado = principal.getName();
        return ResponseEntity.ok(examenService.actualizarExamen(id, dto, correoLogueado));
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @JsonView(ExamenViews.DiscreetExam.class)
    @Operation(summary = "Activar/Desactivar examen", description = "Cambia la visibilidad del examen. Solo el dueño o Admin.")
    public ResponseEntity<ExamenDTO> cambiarEstado(@PathVariable Integer id, Principal principal) {
        return ResponseEntity.ok(examenService.cambiarEstadoActivo(id, principal.getName()));
    }

@PutMapping("/{idExamen}/preguntas")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @JsonView(ExamenViews.IndiscreetExam.class)
    @Operation(summary = "Añadir preguntas en lote", description = "Vincula un array de IDs de preguntas a este examen.")
    public ResponseEntity<ExamenDTO> anhadirPreguntas(
            @PathVariable Integer idExamen, 
            @RequestBody List<Integer> idsPreguntas,
            Principal principal) {
        
        return ResponseEntity.ok(examenService.anhadirPreguntas(idExamen, idsPreguntas, principal.getName()));
    }

    @DeleteMapping("/{idExamen}/preguntas")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @JsonView(ExamenViews.IndiscreetExam.class)
    @Operation(summary = "Quitar preguntas en lote", description = "Desvincula un array de IDs de preguntas del examen.")
    public ResponseEntity<ExamenDTO> quitarPreguntas(
            @PathVariable Integer idExamen,
            @RequestBody List<Integer> idsPreguntas,
            Principal principal) {
        return ResponseEntity.ok(examenService.quitarPreguntas(idExamen, idsPreguntas, principal.getName()));
    }
}