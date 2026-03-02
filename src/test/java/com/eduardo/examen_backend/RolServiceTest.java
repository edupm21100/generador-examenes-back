package com.eduardo.examen_backend;

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

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.models.Rol;
import com.eduardo.examen_backend.models.Usuario;
import com.eduardo.examen_backend.repositories.RolRepository;
import com.eduardo.examen_backend.repositories.UsuarioRepository;
import com.eduardo.examen_backend.services.RolService;

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
    void deleteByIdOK() {
        Integer idRol = 3;

        Rol rolBD = new Rol();
        rolBD.setIdRol(idRol);

        Usuario usuarioAfectado = new Usuario();
        usuarioAfectado.setIdUsuario(100);
        
        usuarioAfectado.getRoles().add(rolBD); 
        rolBD.getUsuarios().add(usuarioAfectado); 

        when(rolRepository.findById(idRol)).thenReturn(Optional.of(rolBD));

        boolean resultado = rolService.deleteById(idRol);

        assertTrue(resultado, "Debería devolver true indicando que se borró con éxito");

        assertTrue(rolBD.getUsuarios().isEmpty(), "El rol debió soltar al usuario (lista vacía)");
        assertTrue(usuarioAfectado.getRoles().isEmpty(), "Al usuario se le debió quitar el rol (lista vacía)");

        verify(rolRepository, times(1)).findById(idRol);
        verify(rolRepository, times(1)).delete(rolBD);
    }

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
    void findALLOK() {
        Rol rolBD = new Rol();
        rolBD.setIdRol(1);
        
        RolDTO dtoEsperado = new RolDTO();
        dtoEsperado.setIdRol(1);

        when(rolRepository.findAll()).thenReturn(List.of(rolBD));
        when(modelMapper.map(rolBD, RolDTO.class)).thenReturn(dtoEsperado);

        List<RolDTO> resultado = rolService.findAll();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(rolRepository, times(1)).findAll();
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

        Optional<RolDTO> resultado = rolService.findById(idBuscado);

        assertTrue(resultado.isPresent());
        assertEquals(idBuscado, resultado.get().getIdRol());
        verify(rolRepository, times(1)).findById(idBuscado);
    }

    @Test
    void finRolByIdMAL() {
        Integer idBuscado = 99;
        when(rolRepository.findById(idBuscado)).thenReturn(Optional.empty());

        Optional<RolDTO> resultado = rolService.findById(idBuscado);

        assertFalse(resultado.isPresent());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void updateRolOK() {
        RolDTO dtoEntrada = new RolDTO();
        dtoEntrada.setIdRol(1);
        dtoEntrada.setNombreRol("NUEVO_NOMBRE");
        dtoEntrada.setActivo(true);

        Rol rolMapeado = new Rol();
        rolMapeado.setIdRol(1);
        rolMapeado.setNombreRol("NUEVO_NOMBRE");
        rolMapeado.setActivo(true);

        Rol rolBD = new Rol();
        rolBD.setIdRol(1);
        rolBD.setNombreRol("VIEJO_NOMBRE");

        RolDTO dtoSalida = new RolDTO();
        dtoSalida.setNombreRol("NUEVO_NOMBRE");

        when(modelMapper.map(dtoEntrada, Rol.class)).thenReturn(rolMapeado);
        when(rolRepository.findById(1)).thenReturn(Optional.of(rolBD));
        when(rolRepository.save(any(Rol.class))).thenReturn(rolBD);
        when(modelMapper.map(rolBD, RolDTO.class)).thenReturn(dtoSalida);

        Optional<RolDTO> resultado = rolService.update(dtoEntrada);

        assertTrue(resultado.isPresent());
        assertEquals("NUEVO_NOMBRE", resultado.get().getNombreRol());
        verify(rolRepository, times(1)).save(rolBD);
    }

    @Test
    void updateRolOKMAL() {
        RolDTO dtoEntrada = new RolDTO();
        dtoEntrada.setIdRol(99);
        
        Rol rolMapeado = new Rol();
        rolMapeado.setIdRol(99);

        when(modelMapper.map(dtoEntrada, Rol.class)).thenReturn(rolMapeado);
        when(rolRepository.findById(99)).thenReturn(Optional.empty());

        Optional<RolDTO> resultado = rolService.update(dtoEntrada);

        assertFalse(resultado.isPresent());
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
        when(rolRepository.findById(idBuscado)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            rolService.desactivateRol(idBuscado);
        });

        assertEquals("El usuario que se desactiva/activa no existe", excepcion.getMessage());
        verify(rolRepository, never()).save(any(Rol.class));
    }
}
