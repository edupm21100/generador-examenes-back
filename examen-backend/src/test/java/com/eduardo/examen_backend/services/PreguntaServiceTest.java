package com.eduardo.examen_backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

import com.eduardo.examen_backend.examenes.categorias.Categoria;
import com.eduardo.examen_backend.examenes.categorias.CategoriaRepository;
import com.eduardo.examen_backend.examenes.opciones.Opcion;
import com.eduardo.examen_backend.examenes.opciones.OpcionDTO;
import com.eduardo.examen_backend.examenes.preguntas.Pregunta;
import com.eduardo.examen_backend.examenes.preguntas.PreguntaDTO;
import com.eduardo.examen_backend.examenes.preguntas.PreguntaRepository;
import com.eduardo.examen_backend.examenes.preguntas.PreguntaServiceImpl;
import com.eduardo.examen_backend.shared.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class PreguntaServiceTest {

    @Mock
    private PreguntaRepository preguntaRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private PreguntaServiceImpl preguntaService;

    @Test
    void obtenerTodasFiltraInactivasTestOK() {
        // Preparamos una categoría falsa para el mapeo
        Categoria cat = new Categoria();
        cat.setIdCategoria(1);
        cat.setNombre("Java");

        // Pregunta 1: ACTIVA
        Pregunta p1 = new Pregunta();
        p1.setIdPregunta(1);
        p1.setActivo(true);
        p1.setCategoria(cat);

        // Pregunta 2: INACTIVA (Borrado lógico)
        Pregunta p2 = new Pregunta();
        p2.setIdPregunta(2);
        p2.setActivo(false);
        p2.setCategoria(cat);

        // Simulamos que la BD devuelve ambas
        when(preguntaRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        // Ejecutamos
        List<PreguntaDTO> resultado = preguntaService.obtenerTodas();

        // Verificamos que el filtro del Service haya quitado la inactiva
        assertNotNull(resultado);
        assertEquals(1, resultado.size(), "Debería devolver solo 1 pregunta (la activa)");
        assertEquals(1, resultado.get(0).getIdPregunta(), "La pregunta devuelta debe ser la ID 1");
    }

    @Test
    void obtenerPorCategoriaTestOK() {
        Integer idCategoria = 1;
        Categoria cat = new Categoria();
        cat.setIdCategoria(idCategoria);
        cat.setNombre("Spring");

        Pregunta p1 = new Pregunta();
        p1.setIdPregunta(10);
        p1.setActivo(true);
        p1.setCategoria(cat);

        when(categoriaRepository.existsById(idCategoria)).thenReturn(true);
        when(preguntaRepository.findByCategoria_IdCategoria(idCategoria)).thenReturn(Arrays.asList(p1));

        List<PreguntaDTO> resultado = preguntaService.obtenerPorCategoria(idCategoria);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(categoriaRepository, times(1)).existsById(idCategoria);
    }

    @Test
    void obtenerPorCategoriaInexistenteMAL() {
        Integer idCategoria = 99;
        when(categoriaRepository.existsById(idCategoria)).thenReturn(false);

        NotFoundException excepcion = assertThrows(NotFoundException.class, () -> {
            preguntaService.obtenerPorCategoria(idCategoria);
        });

        assertEquals("La categoría con ID 99 no existe.", excepcion.getMessage());
        verify(preguntaRepository, never()).findByCategoria_IdCategoria(any());
    }

    @Test
    void eliminarPreguntaBorradoLogicoTestOK() {
        Integer idPregunta = 5;

        Categoria cat = new Categoria();
        cat.setIdCategoria(1);
        cat.setNombre("General");
        Pregunta pBD = new Pregunta();
        pBD.setIdPregunta(idPregunta);
        pBD.setActivo(true);
        pBD.setCategoria(cat);

        when(preguntaRepository.findById(idPregunta)).thenReturn(Optional.of(pBD));
        when(preguntaRepository.save(any(Pregunta.class))).thenReturn(pBD);

        preguntaService.cambiarEstadoActivo(idPregunta);

        assertFalse(pBD.getActivo(), "La pregunta debería haber pasado a activo = false");
        verify(preguntaRepository, times(1)).save(pBD);
    }

    @Test
    void cambiarEstadoActivoTestOK() {
        Integer idPregunta = 7;
        Categoria cat = new Categoria();
        cat.setIdCategoria(1);
        cat.setNombre("Test");

        Pregunta pBD = new Pregunta();
        pBD.setIdPregunta(idPregunta);
        pBD.setActivo(false); // Nace apagada
        pBD.setCategoria(cat);

        when(preguntaRepository.findById(idPregunta)).thenReturn(Optional.of(pBD));
        when(preguntaRepository.save(any(Pregunta.class))).thenReturn(pBD);

        PreguntaDTO resultado = preguntaService.cambiarEstadoActivo(idPregunta);

        assertNotNull(resultado);
        assertTrue(pBD.getActivo(), "El interruptor debería haber encendido la pregunta");
        verify(preguntaRepository, times(1)).save(pBD);
    }

    @Test
    void obtenerPorIdTestOK() {
        Integer idPregunta = 1;
        Categoria cat = new Categoria();
        cat.setIdCategoria(1);
        cat.setNombre("Test");

        Pregunta pBD = new Pregunta();
        pBD.setIdPregunta(idPregunta);
        pBD.setEnunciado("¿Qué es Java?");
        pBD.setActivo(true);
        pBD.setCategoria(cat);

        when(preguntaRepository.findById(idPregunta)).thenReturn(Optional.of(pBD));

        PreguntaDTO resultado = preguntaService.obtenerPorId(idPregunta);

        assertNotNull(resultado);
        assertEquals("¿Qué es Java?", resultado.getEnunciado());
    }

    @Test
    void crearPreguntaTestOK() {
        Categoria catBD = new Categoria();
        catBD.setIdCategoria(1);
        catBD.setNombre("Java");

        PreguntaDTO dtoEntrada = new PreguntaDTO();
        dtoEntrada.setEnunciado("¿Es Java Orientado a Objetos?");
        dtoEntrada.setIdCategoria(1);
        OpcionDTO opDTO = new OpcionDTO();
        opDTO.setTexto("Verdadero");
        opDTO.setEsCorrecta(true);
        dtoEntrada.setOpciones(List.of(opDTO));

        Pregunta pGuardada = new Pregunta();
        pGuardada.setIdPregunta(10);
        pGuardada.setEnunciado("¿Es Java Orientado a Objetos?");
        pGuardada.setActivo(true);
        pGuardada.setCategoria(catBD);
        Opcion opBD = new Opcion();
        opBD.setTexto("Verdadero");
        opBD.setEsCorrecta(true);
        pGuardada.setOpciones(List.of(opBD));

        // 1. El servicio buscará la categoría para vincularla
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(catBD));
        // 2. Guardará la pregunta
        when(preguntaRepository.save(any())).thenReturn(pGuardada);

        PreguntaDTO resultado = preguntaService.crearPregunta(dtoEntrada);

        assertNotNull(resultado);
        assertEquals(10, resultado.getIdPregunta());
        verify(preguntaRepository, times(1)).save(any(Pregunta.class));
    }

    @Test
    void crearPreguntaCategoriaInexistenteMAL() {
        PreguntaDTO dtoEntrada = new PreguntaDTO();
        dtoEntrada.setIdCategoria(99);

        when(categoriaRepository.findById(99)).thenReturn(Optional.empty());

        NotFoundException excepcion = assertThrows(NotFoundException.class, () -> {
            preguntaService.crearPregunta(dtoEntrada);
        });

        assertEquals("La categoría especificada no existe.", excepcion.getMessage());
        verify(preguntaRepository, never()).save(any());
    }

    @Test
    void obtenerPorIdInexistenteMAL() {
        Integer idFalso = 999;

        when(preguntaRepository.findById(idFalso)).thenReturn(Optional.empty());

        NotFoundException excepcion = assertThrows(NotFoundException.class, () -> {
            preguntaService.obtenerPorId(idFalso);
        });

        assertEquals("La pregunta con ID 999 no existe.", excepcion.getMessage());
    }

    @Test
    void cambiarEstadoPreguntaInexistenteMAL() {
        Integer idFalso = 888;

        when(preguntaRepository.findById(idFalso)).thenReturn(Optional.empty());

        NotFoundException excepcion = assertThrows(NotFoundException.class, () -> {
            preguntaService.cambiarEstadoActivo(idFalso);
        });

        assertEquals("La pregunta con ID 888 no existe.", excepcion.getMessage());
        verify(preguntaRepository, never()).save(any());
    }
}