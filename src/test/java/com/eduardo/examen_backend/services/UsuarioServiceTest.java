package com.eduardo.examen_backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.dto.UsuarioDTO;
import com.eduardo.examen_backend.dto.UsuarioRolDTO;
import com.eduardo.examen_backend.models.Rol;
import com.eduardo.examen_backend.models.Usuario;
import com.eduardo.examen_backend.repositories.RolRepository;
import com.eduardo.examen_backend.repositories.UsuarioRepository;
import com.eduardo.examen_backend.exception.BadRequestException;
import com.eduardo.examen_backend.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void findUserByIdTestOK() {
        Integer idUsuario = 2;

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setNombreUsuario("Eduardo");

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setIdUsuario(idUsuario);
        usuarioDTO.setNombreUsuario("Eduardo");

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(modelMapper.map(usuario, UsuarioDTO.class)).thenReturn(usuarioDTO);

        Optional<UsuarioDTO> resultado = usuarioService.findById(idUsuario);

        assertTrue(resultado.isPresent(), "El Optional no debería estar vacío");

        assertEquals("Eduardo", resultado.get().getNombreUsuario(), "El nombre del usuario no coincide");

        verify(usuarioRepository, times(1)).findById(idUsuario);
    }

    @Test
    void findUserByIdTestMAL() {
        Integer idUsuario = -1;

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.empty());

        Optional<UsuarioDTO> resultado = usuarioService.findById(idUsuario);

        assertFalse(resultado.isPresent(), "La devolución debería estar vacía");
        verify(usuarioRepository, times(1)).findById(idUsuario);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void changeContrasenhaComprobacionEquivocadaMAL() {
        Integer idUsuario = 1;
        String contrasenhaAntigua = "$$$$";
        String contrasenhaIntroducida = "%%%%";
        String contrasenhaNueva = "####";

        Usuario usuarioBD = new Usuario();
        usuarioBD.setIdUsuario(idUsuario);
        usuarioBD.setContrasenhaUsuario(contrasenhaAntigua);

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioBD));

        BadRequestException excepcion = assertThrows(BadRequestException.class, () -> {
            usuarioService.changeContrasenha(idUsuario, contrasenhaNueva, contrasenhaIntroducida);
        });

        assertEquals("Contraseña invalida", excepcion.getMessage());

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void changeContrasenhaComprobacionCorrectaOK() {
        Integer idUsuario = 1;
        String contrasenhaAntigua = "$$$$";
        String contrasenhaConfirmacion = "$$$$";
        String contrasenhaNueva = "####";

        Usuario usuarioBD = new Usuario();
        usuarioBD.setIdUsuario(idUsuario);
        usuarioBD.setContrasenhaUsuario(contrasenhaAntigua);

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setIdUsuario(idUsuario);
        usuarioDTO.setContrasenhaUsuario(contrasenhaNueva);

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioBD));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioBD);
        when(modelMapper.map(usuarioBD, UsuarioDTO.class)).thenReturn(usuarioDTO);

        UsuarioDTO resultado = usuarioService.changeContrasenha(idUsuario, contrasenhaNueva, contrasenhaConfirmacion);

        assertNotNull(resultado, "Debería devolverse un usuario");
        assertEquals(contrasenhaNueva, resultado.getContrasenhaUsuario(), "La contraseña en el DTO debe ser la nueva");

        verify(usuarioRepository, times(1)).findById(idUsuario);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void saveUsuarioOK() {
        UsuarioDTO datosEntrada = new UsuarioDTO();
        datosEntrada.setNombreUsuario("Laura");

        Usuario usuarioMapeado = new Usuario();
        usuarioMapeado.setNombreUsuario("Laura");

        Rol rolFalso = new Rol();
        rolFalso.setIdRol(2);
        rolFalso.setNombreRol("USER");

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setIdUsuario(10);
        usuarioGuardado.setNombreUsuario("Laura");
        usuarioGuardado.getRoles().add(rolFalso);

        UsuarioDTO dtoSalida = new UsuarioDTO();
        dtoSalida.setIdUsuario(10);
        dtoSalida.setNombreUsuario("Laura");

        when(modelMapper.map(datosEntrada, Usuario.class)).thenReturn(usuarioMapeado);

        when(rolRepository.findById(anyInt())).thenReturn(Optional.of(rolFalso));

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        when(modelMapper.map(usuarioGuardado, UsuarioDTO.class)).thenReturn(dtoSalida);

        UsuarioDTO resultado = usuarioService.save(datosEntrada);

        assertNotNull(resultado, "El usuario guardado no debe ser nulo");
        assertEquals("Laura", resultado.getNombreUsuario(), "El nombre debe coincidir");
        assertEquals(10, resultado.getIdUsuario(), "Debería tener el ID generado por la BD");

        verify(rolRepository, times(1)).findById(anyInt());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void anhadirRolIfSolicitanteNoEsAdminMAL() {
        Integer idTarget = 5;
        Integer idRol = 2;
        Integer idImpostor = 99;

        Usuario usuarioImpostor = new Usuario();
        usuarioImpostor.setIdUsuario(idImpostor);
        Rol rolNormal = new Rol();
        rolNormal.setIdRol(2);
        usuarioImpostor.getRoles().add(rolNormal);

        when(usuarioRepository.findById(idImpostor)).thenReturn(Optional.of(usuarioImpostor));

        BadRequestException excepcion = assertThrows(BadRequestException.class, () -> {
            usuarioService.anhadirRol(idTarget, idRol, idImpostor);
        });

        assertEquals("Acceso denegado: No tienes permisos de Administrador", excepcion.getMessage());

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void findAllOK() {
        Usuario usuarioBD = new Usuario();
        usuarioBD.setIdUsuario(1);
        usuarioBD.setNombreUsuario("Eduardo");

        UsuarioDTO dtoEsperado = new UsuarioDTO();
        dtoEsperado.setIdUsuario(1);
        dtoEsperado.setNombreUsuario("Eduardo");

        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuarioBD));
        when(modelMapper.map(usuarioBD, UsuarioDTO.class)).thenReturn(dtoEsperado);

        List<UsuarioDTO> resultado = usuarioService.findAll();

        assertFalse(resultado.isEmpty(), "La lista no debería estar vacía");
        assertEquals(1, resultado.size(), "Debería haber 1 usuario en la lista");
        assertEquals("Eduardo", resultado.get(0).getNombreUsuario());

        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void deleteByIdOK() {
        Integer idUsuario = 1;
        Usuario usuarioBD = new Usuario();
        
        usuarioBD.setIdUsuario(idUsuario);

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioBD));

        boolean resultado = usuarioService.deleteById(idUsuario);

        assertTrue(resultado, "Debería devolver true indicando que se borró");

        verify(usuarioRepository, times(1)).findById(idUsuario);
        verify(usuarioRepository, times(1)).delete(usuarioBD);
    }

    @Test
    void desactivateUserOK() {
        Integer idUsuario = 1;

        Usuario usuarioBD = new Usuario();
        usuarioBD.setIdUsuario(idUsuario);
        usuarioBD.setActivo(true);

        UsuarioDTO dtoEsperado = new UsuarioDTO();
        dtoEsperado.setIdUsuario(idUsuario);
        dtoEsperado.setActivo(false);

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioBD));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioBD);
        when(modelMapper.map(usuarioBD, UsuarioDTO.class)).thenReturn(dtoEsperado);

        UsuarioDTO resultado = usuarioService.desactivateUser(idUsuario);

        assertNotNull(resultado);
        assertFalse(resultado.isActivo(), "El usuario debería estar desactivado (false)");

        verify(usuarioRepository, times(1)).findById(idUsuario);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void deleteByIdMAL() {
        Integer idUsuario = -1;

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.empty());

        boolean resultado = usuarioService.deleteById(idUsuario);

        assertFalse(resultado, "Debería devolver false indicando que no se encontró para borrar");
        verify(usuarioRepository, times(1)).findById(idUsuario);
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    @Test
    void desactivateUserMAL() {
        Integer idUsuario = -1;

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            usuarioService.desactivateUser(idUsuario);
        });

        assertEquals("El usuario que se desactiva/activa no existe", excepcion.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void updateOK() {
        UsuarioDTO dtoEntrada = new UsuarioDTO();
        dtoEntrada.setIdUsuario(1);
        dtoEntrada.setNombreUsuario("EduModificado");

        Usuario usuarioMapeado = new Usuario();
        usuarioMapeado.setIdUsuario(1);

        Usuario usuarioBD = new Usuario();
        usuarioBD.setIdUsuario(1);

        UsuarioDTO dtoSalida = new UsuarioDTO();
        dtoSalida.setIdUsuario(1);
        dtoSalida.setNombreUsuario("EduModificado");

        when(modelMapper.map(dtoEntrada, Usuario.class)).thenReturn(usuarioMapeado);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioBD));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioBD);
        when(modelMapper.map(usuarioBD, UsuarioDTO.class)).thenReturn(dtoSalida);

        Optional<UsuarioDTO> resultado = usuarioService.update(dtoEntrada);

        assertTrue(resultado.isPresent(), "Debería devolver el usuario actualizado");
        assertEquals("EduModificado", resultado.get().getNombreUsuario());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void updateMAL() {
        UsuarioDTO dtoEntrada = new UsuarioDTO();
        dtoEntrada.setIdUsuario(99);

        Usuario usuarioMapeado = new Usuario();
        usuarioMapeado.setIdUsuario(99);

        when(modelMapper.map(dtoEntrada, Usuario.class)).thenReturn(usuarioMapeado);
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        Optional<UsuarioDTO> resultado = usuarioService.update(dtoEntrada);

        assertFalse(resultado.isPresent(), "Debería devolver Optional vacío si no existe");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void findByRolOK() {
        Integer idRol = 2;
        Usuario usuarioBD = new Usuario();
        usuarioBD.setIdUsuario(1);

        UsuarioDTO dtoEsperado = new UsuarioDTO();
        dtoEsperado.setIdUsuario(1);

        when(usuarioRepository.findByRolesIdRol(idRol)).thenReturn(Arrays.asList(usuarioBD));
        when(modelMapper.map(usuarioBD, UsuarioDTO.class)).thenReturn(dtoEsperado);

        List<UsuarioDTO> resultado = usuarioService.findByRol(idRol);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(usuarioRepository, times(1)).findByRolesIdRol(idRol);
    }

    @Test
    void findRolByUsuarioOK() {
        Integer idUsuario = 1;
        
        Usuario usuarioBD = new Usuario();
        usuarioBD.setIdUsuario(idUsuario);
        Rol rol = new Rol();
        rol.setIdRol(2);
        usuarioBD.getRoles().add(rol);

        RolDTO rolDTOEsperado = new RolDTO();
        rolDTOEsperado.setIdRol(2);

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioBD));
        when(modelMapper.map(rol, RolDTO.class)).thenReturn(rolDTOEsperado);

        List<RolDTO> resultado = usuarioService.findRolByUsuario(idUsuario);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(usuarioRepository, times(1)).findById(idUsuario);
    }

    @Test
    void findRolByUsuarioMAL() {
        Integer idUsuario = 99;
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.empty());

        NotFoundException excepcion = assertThrows(NotFoundException.class, () -> {
            usuarioService.findRolByUsuario(idUsuario);
        });

        assertEquals("El usuario con ID " + idUsuario + " no existe", excepcion.getMessage());
    }

    @Test
    void anhadirRolOK() {
        Integer idTarget = 5;
        Integer idRol = 2;
        Integer idAdmin = 1;

        Usuario usuarioAdmin = new Usuario();
        usuarioAdmin.setIdUsuario(idAdmin);
        Rol rolAdministrador = new Rol();
        rolAdministrador.setIdRol(1);
        usuarioAdmin.getRoles().add(rolAdministrador);

        Usuario usuarioTarget = new Usuario();
        usuarioTarget.setIdUsuario(idTarget);
        Rol nuevoRol = new Rol();
        nuevoRol.setIdRol(idRol);

        UsuarioDTO dtoSalida = new UsuarioDTO();
        dtoSalida.setIdUsuario(idTarget);

        when(usuarioRepository.findById(idAdmin)).thenReturn(Optional.of(usuarioAdmin));
        when(usuarioRepository.findById(idTarget)).thenReturn(Optional.of(usuarioTarget));
        when(rolRepository.findById(idRol)).thenReturn(Optional.of(nuevoRol));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTarget);
        when(modelMapper.map(usuarioTarget, UsuarioDTO.class)).thenReturn(dtoSalida);

        UsuarioDTO resultado = usuarioService.anhadirRol(idTarget, idRol, idAdmin);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).save(usuarioTarget);
    }

    @Test
    void removeRolOK() {
        Integer idTarget = 5;
        Integer idRol = 2;
        Integer idAdmin = 1;

        Usuario usuarioAdmin = new Usuario();
        usuarioAdmin.setIdUsuario(idAdmin);
        Rol rolAdministrador = new Rol();
        rolAdministrador.setIdRol(1);
        usuarioAdmin.getRoles().add(rolAdministrador);

        Usuario usuarioTarget = new Usuario();
        usuarioTarget.setIdUsuario(idTarget);
        Rol rolAQuitar = new Rol();
        rolAQuitar.setIdRol(idRol);
        usuarioTarget.getRoles().add(rolAQuitar);

        when(usuarioRepository.findById(idAdmin)).thenReturn(Optional.of(usuarioAdmin));
        when(usuarioRepository.findById(idTarget)).thenReturn(Optional.of(usuarioTarget));
        when(rolRepository.findById(idRol)).thenReturn(Optional.of(rolAQuitar));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTarget);

        UsuarioRolDTO resultado = usuarioService.removeRol(idTarget, idRol, idAdmin);

        assertNotNull(resultado);
        assertEquals(idTarget, resultado.getIdUsuario());
        assertEquals(idRol, resultado.getIdRol());
        assertTrue(usuarioTarget.getRoles().isEmpty(), "El rol debió ser eliminado del Set");
        verify(usuarioRepository, times(1)).save(usuarioTarget);
    }

    @Test
    void removeRolIfSolicitanteNoEsAdminMAL() {
        Integer idTarget = 5;
        Integer idRol = 2;
        Integer idImpostor = 99;

        Usuario usuarioImpostor = new Usuario();
        usuarioImpostor.setIdUsuario(idImpostor);
        Rol rolNormal = new Rol();
        rolNormal.setIdRol(2); // No es admin
        usuarioImpostor.getRoles().add(rolNormal);

        when(usuarioRepository.findById(idImpostor)).thenReturn(Optional.of(usuarioImpostor));

        BadRequestException excepcion = assertThrows(BadRequestException.class, () -> {
            usuarioService.removeRol(idTarget, idRol, idImpostor);
        });

        assertEquals("Acceso denegado: No tienes permisos de Administrador", excepcion.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
