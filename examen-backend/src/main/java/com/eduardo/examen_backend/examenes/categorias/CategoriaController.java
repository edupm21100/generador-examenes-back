package com.eduardo.examen_backend.examenes.categorias;

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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Gestión de las temáticas para clasificar preguntas de exámenes")
@SecurityRequirement(name = "bearerAuth")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR', 'ALUMNO')")
    @JsonView(CategoriaViews.DiscreetCategory.class)
    @Operation(summary = "Listar todas las categorías", description = "Devuelve el catálogo de temáticas disponibles.")
    @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida")
    public ResponseEntity<List<CategoriaDTO>> listarCategorias() {
        return ResponseEntity.ok(categoriaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @JsonView(CategoriaViews.IndiscreetCategory.class)
    @Operation(summary = "Buscar categoría por ID", description = "Recupera los datos de una temática específica.")
    @ApiResponse(responseCode = "200", description = "Categoría localizada")
    @ApiResponse(responseCode = "404", description = "La categoría no existe")
    public ResponseEntity<CategoriaDTO> obtenerCategoria(@PathVariable Integer id) {
        return ResponseEntity.ok(categoriaService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @JsonView(CategoriaViews.IndiscreetCategory.class)
    @Operation(summary = "Crear nueva categoría", description = "Añade una temática nueva al sistema. Requiere rol ADMIN o PROFESOR.")
    @ApiResponse(responseCode = "201", description = "Categoría creada con éxito")
    @ApiResponse(responseCode = "409", description = "Conflicto: Ya existe una categoría con ese nombre")
    public ResponseEntity<CategoriaDTO> crearCategoria(@Valid @RequestBody CategoriaDTO dto) {
        return new ResponseEntity<>(categoriaService.crearCategoria(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    @JsonView(CategoriaViews.DiscreetCategory.class)
    @Operation(summary = "Actualizar categoría", description = "Modifica el nombre o descripción de una temática existente.")
    @ApiResponse(responseCode = "200", description = "Categoría actualizada")
    @ApiResponse(responseCode = "404", description = "La categoría no existe")
    @ApiResponse(responseCode = "409", description = "Conflicto: El nuevo nombre ya está en uso")
    public ResponseEntity<CategoriaDTO> actualizarCategoria(@PathVariable Integer id, @Valid @RequestBody CategoriaDTO dto) {
        return ResponseEntity.ok(categoriaService.actualizarCategoria(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar categoría", description = "Borra físicamente una temática. Solo permitido para ADMIN.")
    @ApiResponse(responseCode = "204", description = "Categoría eliminada con éxito")
    @ApiResponse(responseCode = "404", description = "La categoría no existe")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Integer id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}