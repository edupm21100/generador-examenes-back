package com.eduardo.examen_backend.services;

import com.eduardo.examen_backend.dto.IncidenciaDTO;
import com.eduardo.examen_backend.exceptions.NotFoundException;
import com.eduardo.examen_backend.models.Incidencia;
import com.eduardo.examen_backend.repositories.IncidenciaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidenciaServiceTest {

    @Mock
    private IncidenciaRepository incidenciaRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private IncidenciaServiceImpl incidenciaService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void findAll_DeberiaDevolverListaDeIncidenciasDTO_CuandoNoHayUsuarioLogueado() {
        Incidencia incidencia = new Incidencia();
        incidencia.setIdIncidencia(1);

        IncidenciaDTO incidenciaDTO = new IncidenciaDTO();
        incidenciaDTO.setIdIncidencia(1);

        when(incidenciaRepository.findAll()).thenReturn(List.of(incidencia));
        when(modelMapper.map(any(Incidencia.class), eq(IncidenciaDTO.class))).thenReturn(incidenciaDTO);

        List<IncidenciaDTO> resultado = incidenciaService.findAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).getIdIncidencia());
        verify(incidenciaRepository, times(1)).findAll();
    }

    @Test
    void findAll_DeberiaUsarNombreUsuario_CuandoHayUsuarioAutenticado() {
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(auth);
        lenient().when(auth.isAuthenticated()).thenReturn(true);
        lenient().when(auth.getPrincipal()).thenReturn("test@test.com");
        lenient().when(auth.getName()).thenReturn("test@test.com");

        SecurityContextHolder.setContext(securityContext);

        when(incidenciaRepository.findAll()).thenReturn(List.of());

        List<IncidenciaDTO> resultado = incidenciaService.findAll();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void findById_CuandoExiste_DeberiaDevolverIncidenciaDTO() {
        Integer idBuscado = 5;
        Incidencia incidencia = new Incidencia();
        incidencia.setIdIncidencia(idBuscado);

        IncidenciaDTO incidenciaDTO = new IncidenciaDTO();
        incidenciaDTO.setIdIncidencia(idBuscado);

        when(incidenciaRepository.findById(idBuscado)).thenReturn(Optional.of(incidencia));
        when(modelMapper.map(any(Incidencia.class), eq(IncidenciaDTO.class))).thenReturn(incidenciaDTO);

        IncidenciaDTO resultado = incidenciaService.findById(idBuscado);

        assertNotNull(resultado);
        assertEquals(idBuscado, resultado.getIdIncidencia());
        verify(incidenciaRepository, times(1)).findById(idBuscado);
    }

    @Test
    void findById_CuandoNoExiste_DeberiaLanzarNotFoundException() {
        Integer idBuscado = 99;
        when(incidenciaRepository.findById(idBuscado)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            incidenciaService.findById(idBuscado);
        });

        assertEquals("La incidencia con ID 99 no existe", exception.getMessage());
        verify(incidenciaRepository, times(1)).findById(idBuscado);
        verifyNoInteractions(modelMapper);
    }
}