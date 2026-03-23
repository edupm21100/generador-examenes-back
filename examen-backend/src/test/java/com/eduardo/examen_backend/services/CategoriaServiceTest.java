package com.eduardo.examen_backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.eduardo.examen_backend.examenes.categorias.Categoria;
import com.eduardo.examen_backend.examenes.categorias.CategoriaDTO;
import com.eduardo.examen_backend.examenes.categorias.CategoriaRepository;
import com.eduardo.examen_backend.examenes.categorias.CategoriaServiceImpl;
import com.eduardo.examen_backend.shared.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    @Test
    void obtenerTodasTestOK() {
        Categoria catBD = new Categoria();
        catBD.setIdCategoria(1);
        catBD.setNombre("Programación");

        CategoriaDTO dtoEsperado = new CategoriaDTO();
        dtoEsperado.setIdCategoria(1);
        dtoEsperado.setNombre("Programación");

        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(catBD));

        List<CategoriaDTO> resultado = categoriaService.obtenerTodas();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorIdTestOK() {
        Categoria catBD = new Categoria();
        catBD.setIdCategoria(1);

        CategoriaDTO dtoEsperado = new CategoriaDTO();
        dtoEsperado.setIdCategoria(1);

        when(categoriaRepository.findById(1)).thenReturn(Optional.of(catBD));

        CategoriaDTO resultado = categoriaService.obtenerPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getIdCategoria());
    }

    @Test
    void obtenerPorIdInexistenteMAL() {
        when(categoriaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            categoriaService.obtenerPorId(99);
        });

        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void crearCategoriaTestOK() {
        CategoriaDTO dtoEntrada = new CategoriaDTO();
        dtoEntrada.setNombre("Bases de Datos");

        Categoria catGuardada = new Categoria();
        catGuardada.setIdCategoria(5);
        catGuardada.setNombre("Bases de Datos");

        // Solo simulamos la base de datos, ¡nada de ModelMapper!
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(catGuardada);

        CategoriaDTO resultado = categoriaService.crearCategoria(dtoEntrada);

        assertNotNull(resultado, "El resultado no puede ser nulo");
        assertEquals(5, resultado.getIdCategoria(), "El ID debe ser 5, asignado por la BD");
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    void actualizarCategoriaTestOK() {
        Integer idCat = 1;
        CategoriaDTO dtoEntrada = new CategoriaDTO();
        dtoEntrada.setNombre("Nuevo Nombre");

        Categoria catBD = new Categoria();
        catBD.setIdCategoria(idCat);
        catBD.setNombre("Viejo Nombre");

        Categoria catGuardada = new Categoria();
        catGuardada.setIdCategoria(idCat);
        catGuardada.setNombre("Nuevo Nombre");

        when(categoriaRepository.findById(idCat)).thenReturn(Optional.of(catBD));
        when(categoriaRepository.save(any())).thenReturn(catGuardada);

        CategoriaDTO resultado = categoriaService.actualizarCategoria(idCat, dtoEntrada);

        assertNotNull(resultado);
        assertEquals("Nuevo Nombre", catBD.getNombre(), "El nombre de la entidad debió actualizarse");
        verify(categoriaRepository, times(1)).save(catBD);
    }

    @Test
    void eliminarCategoriaTestOK() {
        Integer idCat = 1;

        when(categoriaRepository.existsById(idCat)).thenReturn(true);

        categoriaService.eliminarCategoria(idCat);

        // Verificamos que se llamó al método correcto
        verify(categoriaRepository, times(1)).existsById(idCat);
        verify(categoriaRepository, times(1)).deleteById(idCat); 
    }
}