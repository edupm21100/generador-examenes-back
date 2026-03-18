package com.eduardo.examen_backend.examenes.categorias;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.examen_backend.shared.exceptions.DuplicateException;
import com.eduardo.examen_backend.shared.exceptions.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> obtenerTodas() {
        return categoriaRepository.findAll().stream()
            .map(this::mapearADTO)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDTO obtenerPorId(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("La categoría con ID " + id + " no existe en el sistema."));
        return mapearADTO(categoria);
    }

    @Override
    @Transactional
    public CategoriaDTO crearCategoria(CategoriaDTO dto) {
        if (categoriaRepository.existsByNombre(dto.getNombre())) {
            throw new DuplicateException("Ya existe una categoría con el nombre: '" + dto.getNombre() + "'");
        }

        Categoria categoria = Categoria.builder()
            .nombre(dto.getNombre())
            .descripcion(dto.getDescripcion())
            .build();

        Categoria categoriaGuardada = categoriaRepository.save(categoria);
        return mapearADTO(categoriaGuardada);
    }

    @Override
    @Transactional
    public CategoriaDTO actualizarCategoria(Integer id, CategoriaDTO dto) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("No se puede actualizar. La categoría con ID " + id + " no existe."));

        if (!categoria.getNombre().equalsIgnoreCase(dto.getNombre()) && categoriaRepository.existsByNombre(dto.getNombre())) {
            throw new DuplicateException("El nombre '" + dto.getNombre() + "' ya está reservado por otra categoría.");
        }

        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());

        Categoria categoriaActualizada = categoriaRepository.save(categoria);
        return mapearADTO(categoriaActualizada);
    }

    @Override
    @Transactional
    public void eliminarCategoria(Integer id) {
        if (!categoriaRepository.existsById(id)) {
            throw new NotFoundException("No se puede eliminar. La categoría con ID " + id + " no existe.");
        }
        categoriaRepository.deleteById(id);
    }

    private CategoriaDTO mapearADTO(Categoria categoria) {
        return CategoriaDTO.builder()
            .idCategoria(categoria.getIdCategoria())
            .nombre(categoria.getNombre())
            .descripcion(categoria.getDescripcion())
            .build();
    }
}