package com.eduardo.examen_backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.eduardo.examen_backend.exceptions.NotFoundException;

import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.dto.UsuarioDTO;
import com.eduardo.examen_backend.models.Rol;
import com.eduardo.examen_backend.models.Usuario;
import com.eduardo.examen_backend.repositories.RolRepository;
import com.eduardo.examen_backend.repositories.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RolService rolService;

    @Test
    void saveRolOK() {
        RolDTO dtoEntrada = new RolDTO();
        dtoEntrada.setNombreRol("ADMIN");

        Rol rolMapeado = new Rol();
        rolMapeado.setNombreRol("ADMIN");

        Rol rolGuardado = new Rol();
        rolGuardado.setIdRol(1);
        rolGuardado.setNombreRol("ADMIN");

        RolDTO dtoSalida = new RolDTO();
        dtoSalida.setIdRol(1);
        dtoSalida.setNombreRol("ADMIN");

        when(modelMapper.map(dtoEntrada, Rol.class)).thenReturn(rolMapeado);
        when(rolRepository.save(any(Rol.class))).thenReturn(rolGuardado);
        when(modelMapper.map(rolGuardado, RolDTO.class)).thenReturn(dtoSalida);

        RolDTO resultado = rolService.save(dtoEntrada);

        assertNotNull(resultado);
        assertEquals(1, resultado.getIdRol());
        assertEquals("ADMIN", resultado.getNombreRol());

        verify(rolRepository, times(1)).save(any(Rol.class));
    }

    @Test
    void findAllOK() {
        Rol rolBD = new Rol();
        rolBD.setIdRol(1);

        RolDTO dtoEsperado = new RolDTO();
        dtoEsperado.setIdRol(1);

        lenient().when(rolRepository.findAll()).thenReturn(List.of(rolBD));
        lenient().when(rolRepository.findByActivoTrue()).thenReturn(List.of(rolBD));

        when(modelMapper.map(rolBD, RolDTO.class)).thenReturn(dtoEsperado);

        List<RolDTO> resultado = rolService.findAll();

        assertFalse(resultado.isEmpty(), "La lista devuelta no debería estar vacía");
        assertEquals(1, resultado.size());
    }

    @Test
    void finRolByIdOK() {
        Integer idBuscado = 1;
        Rol rolBD = new Rol();
        rolBD.setIdRol(idBuscado);

        RolDTO dtoEsperado = new RolDTO();
        dtoEsperado.setIdRol(idBuscado);

        when(rolRepository.findById(idBuscado)).thenReturn(Optional.of(rolBD));
        when(modelMapper.map(rolBD, RolDTO.class)).thenReturn(dtoEsperado);

        RolDTO resultado = rolService.findById(idBuscado);

        assertNotNull(resultado);
        assertEquals(idBuscado, resultado.getIdRol());
        verify(rolRepository, times(1)).findById(idBuscado);
    }

    @Test
    void finRolByIdMAL() {
        Integer idBuscado = 99;
        when(rolRepository.findById(idBuscado)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            rolService.findById(idBuscado);
        });

        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void updateRolOK() {
        RolDTO dtoEntrada = new RolDTO();
        dtoEntrada.setIdRol(1);
        dtoEntrada.setNombreRol("NUEVO_NOMBRE");
        dtoEntrada.setActivo(true);

        Rol rolBD = new Rol();
        rolBD.setIdRol(1);
        rolBD.setNombreRol("VIEJO_NOMBRE");

        RolDTO dtoSalida = new RolDTO();
        dtoSalida.setNombreRol("NUEVO_NOMBRE");

        when(rolRepository.findById(1)).thenReturn(Optional.of(rolBD));
        when(rolRepository.save(any(Rol.class))).thenReturn(rolBD);
        when(modelMapper.map(rolBD, RolDTO.class)).thenReturn(dtoSalida);

        RolDTO resultado = rolService.update(dtoEntrada);

        assertNotNull(resultado);
        assertEquals("NUEVO_NOMBRE", resultado.getNombreRol());
        verify(rolRepository, times(1)).save(rolBD);
    }

    @Test
    void updateRolMAL() {
        RolDTO dtoEntrada = new RolDTO();
        dtoEntrada.setIdRol(99);

        when(rolRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            rolService.update(dtoEntrada);
        });

        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void desactivateRolOK() {
        Integer idBuscado = 1;

        Rol rolBD = new Rol();
        rolBD.setIdRol(idBuscado);
        rolBD.setActivo(true);

        RolDTO dtoSalida = new RolDTO();
        dtoSalida.setActivo(false);

        when(rolRepository.findById(idBuscado)).thenReturn(Optional.of(rolBD));
        when(rolRepository.save(any(Rol.class))).thenReturn(rolBD);
        when(modelMapper.map(rolBD, RolDTO.class)).thenReturn(dtoSalida);

        RolDTO resultado = rolService.desactivateRol(idBuscado);

        assertFalse(resultado.isActivo(), "El estado debió cambiar a false");
        verify(rolRepository, times(1)).save(rolBD);
    }

    @Test
    void desactivateRolMAL() {
        Integer idBuscado = 99;

        lenient().when(rolRepository.findById(idBuscado)).thenReturn(Optional.empty());

        Exception excepcion = assertThrows(Exception.class, () -> {
            rolService.desactivateRol(idBuscado);
        });

        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void findUsuariosByRolOK() {
        Integer idRol = 1;
        Usuario usuarioBD = new Usuario();
        usuarioBD.setIdUsuario(10);

        UsuarioDTO dtoEsperado = new UsuarioDTO();
        dtoEsperado.setIdUsuario(10);

        when(rolRepository.existsById(idRol)).thenReturn(true);
        when(usuarioRepository.findByRolesIdRol(idRol)).thenReturn(List.of(usuarioBD));
        when(modelMapper.map(usuarioBD, UsuarioDTO.class)).thenReturn(dtoEsperado);

        List<UsuarioDTO> resultado = rolService.findUsuariosByRol(idRol);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(rolRepository, times(1)).existsById(idRol);
        verify(usuarioRepository, times(1)).findByRolesIdRol(idRol);
    }

    @Test
    void findUsuariosByRolMAL() {
        Integer idRol = 99;

        lenient().when(rolRepository.existsById(idRol)).thenReturn(false);

        Exception excepcion = assertThrows(Exception.class, () -> {
            rolService.findUsuariosByRol(idRol);
        });

        verify(usuarioRepository, never()).findByRolesIdRol(any());
    }
}