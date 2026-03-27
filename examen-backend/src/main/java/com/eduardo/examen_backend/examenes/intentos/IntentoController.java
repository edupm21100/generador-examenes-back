package com.eduardo.examen_backend.examenes.intentos;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("/intentos")
@RequiredArgsConstructor
@Tag(name = "Intentos (Exámenes realizados)", description = "Resolución de exámenes por parte de los alumnos y consulta de calificaciones")
@SecurityRequirement(name = "bearerAuth")
public class IntentoController {

    private final IntentoService intentoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ALUMNO', 'PROFESOR', 'ADMIN')")
    @JsonView(IntentoViews.IndiscreetTry.class)
    @Operation(summary = "Realizar un examen", description = "Envía las respuestas de un examen para ser evaluadas automáticamente.")
    public ResponseEntity<IntentoDTO> realizarExamen(@Valid @RequestBody IntentoDTO intentoDTO, Principal principal) {
        String correoLogueado = principal.getName();
        return new ResponseEntity<>(intentoService.realizarExamen(correoLogueado, intentoDTO), HttpStatus.CREATED);
    }

    @GetMapping("/mis-notas")
    @PreAuthorize("hasAnyRole('ALUMNO', 'PROFESOR', 'ADMIN')")
    @JsonView(IntentoViews.IndiscreetTry.class)
    @Operation(summary = "Ver mi historial de exámenes", description = "Devuelve los intentos y notas del usuario autenticado.")
    public ResponseEntity<List<IntentoDTO>> obtenerMisIntentos(Principal principal) {
        return ResponseEntity.ok(intentoService.obtenerMisIntentos(principal.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ALUMNO', 'PROFESOR', 'ADMIN')")
    @JsonView(IntentoViews.IndiscreetTry.class)
    @Operation(summary = "Ver detalle de un intento", description = "Muestra el examen completo corregido. Un alumno solo puede ver el suyo.")
    public ResponseEntity<IntentoDTO> obtenerDetalleIntento(@PathVariable Integer id, Principal principal) {
        return ResponseEntity.ok(intentoService.obtenerDetalleIntento(id, principal.getName()));
    }

    @GetMapping("/{id}/reporte/pdf")
    @PreAuthorize("hasAnyRole('ALUMNO', 'PROFESOR', 'ADMIN')")
    @Operation(summary = "Exportar intento a PDF", description = "Genera un PDF con las respuestas y la nota de un intento concreto.")
    public ResponseEntity<byte[]> descargarReporteIntento(@PathVariable Integer id, Principal principal) {

        byte[] pdfBytes = intentoService.generarReporteIntentoPdf(id, principal.getName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Intento_" + id + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}