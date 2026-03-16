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
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.eduardo.examen_backend.auth.PasswordDTO;
import com.eduardo.examen_backend.roles.Rol;
import com.eduardo.examen_backend.roles.RolDTO;
import com.eduardo.examen_backend.roles.RolRepository;
import com.eduardo.examen_backend.shared.exceptions.BadRequestException;
import com.eduardo.examen_backend.shared.exceptions.NotFoundException;
import com.eduardo.examen_backend.usuarios.Usuario;
import com.eduardo.examen_backend.usuarios.UsuarioDTO;
import com.eduardo.examen_backend.usuarios.UsuarioRepository;
import com.eduardo.examen_backend.usuarios.UsuarioRolDTO;
import com.eduardo.examen_backend.usuarios.UsuarioServiceImpl;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UsuarioServiceImpl usuarioService;

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

        UsuarioDTO resultado = usuarioService.findById(idUsuario);

        assertNotNull(resultado, "El resultado no debería ser nulo");
        assertEquals("Eduardo", resultado.getNombreUsuario(), "El nombre del usuario no coincide");
        verify(usuarioRepository, times(1)).findById(idUsuario);
    }

    @Test
    void findUserByIdTestMAL() {
        Integer idUsuario = -1;
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            usuarioService.findById(idUsuario);
        });

        verify(usuarioRepository, times(1)).findById(idUsuario);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void changeContrasenhaComprobacionEquivocadaMAL() {
        String correoLogueado = "test@test.com";
        String contrasenhaBD = "$2a$10$hash...";

        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setOldPassword("PassEquivocada");
        passwordDTO.setNewPassword("NuevaPass");

        Usuario usuarioBD = new Usuario();
        usuarioBD.setCorreoUsuario(correoLogueado);
        usuarioBD.setContrasenhaUsuario(contrasenhaBD);

        when(usuarioRepository.findByCorreoUsuario(correoLogueado)).thenReturn(Optional.of(usuarioBD));
        when(passwordEncoder.matches("PassEquivocada", contrasenhaBD)).thenReturn(false);

        BadRequestException excepcion = assertThrows(BadRequestException.class, () -> {
            usuarioService.changeContrasenha(correoLogueado, passwordDTO);
        });

        assertEquals("La contraseña antigua no es correcta", excepcion.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void changeContrasenhaComprobacionCorrectaOK() {
        String correoLogueado = "test@test.com";
        String contrasenhaBD = "$2a$10$hash...";

        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setOldPassword("PassCorrecta");
        passwordDTO.setNewPassword("NuevaPass");

        Usuario usuarioBD = new Usuario();
        usuarioBD.setIdUsuario(1);
        usuarioBD.setContrasenhaUsuario(contrasenhaBD);

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setContrasenhaUsuario("NuevaPass");

        when(usuarioRepository.findByCorreoUsuario(correoLogueado)).thenReturn(Optional.of(usuarioBD));
        when(passwordEncoder.matches("PassCorrecta", contrasenhaBD)).thenReturn(true);
        when(passwordEncoder.encode("NuevaPass")).thenReturn("HASH_NUEVO");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioBD);
        when(modelMapper.map(usuarioBD, UsuarioDTO.class)).thenReturn(usuarioDTO);

        UsuarioDTO resultado = usuarioService.changeContrasenha(correoLogueado, passwordDTO);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void saveUsuarioOK() {
        UsuarioDTO datosEntrada = new UsuarioDTO();
        datosEntrada.setNombreUsuario("Laura");
        datosEntrada.setCorreoUsuario("laura@test.com");
        datosEntrada.setContrasenhaUsuario("1234");

        Usuario usuarioMapeado = new Usuario();
        usuarioMapeado.setNombreUsuario("Laura");
        usuarioMapeado.setContrasenhaUsuario("1234");

        Rol rolFalso = new Rol();
        rolFalso.setIdRol(3);

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setIdUsuario(10);
        usuarioGuardado.setNombreUsuario("Laura");

        UsuarioDTO dtoSalida = new UsuarioDTO();
        dtoSalida.setIdUsuario(10);
        dtoSalida.setNombreUsuario("Laura");

        when(usuarioRepository.existsByCorreoUsuario(any())).thenReturn(false);
        when(modelMapper.map(datosEntrada, Usuario.class)).thenReturn(usuarioMapeado);
        when(passwordEncoder.encode("1234")).thenReturn("HASHED_1234");
        when(rolRepository.findById(anyInt())).thenReturn(Optional.of(rolFalso));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);
        when(modelMapper.map(usuarioGuardado, UsuarioDTO.class)).thenReturn(dtoSalida);

        UsuarioDTO resultado = usuarioService.save(datosEntrada);

        assertNotNull(resultado);
        assertEquals(10, resultado.getIdUsuario());
        verify(rolRepository, times(1)).findById(3);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void findAllOK() {
        Usuario usuarioBD = new Usuario();
        usuarioBD.setIdUsuario(1);
        usuarioBD.setNombreUsuario("Eduardo");

        UsuarioDTO dtoEsperado = new UsuarioDTO();
        dtoEsperado.setIdUsuario(1);
        dtoEsperado.setNombreUsuario("Eduardo");

        when(usuarioRepository.findByActivoTrue()).thenReturn(Arrays.asList(usuarioBD));
        when(modelMapper.map(usuarioBD, UsuarioDTO.class)).thenReturn(dtoEsperado);

        List<UsuarioDTO> resultado = usuarioService.findAll();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    void desactivateUserOK() {
        Integer idUsuario = 1;
        Usuario usuarioBD = new Usuario();
        usuarioBD.setIdUsuario(idUsuario);
        usuarioBD.setActivo(true);

        UsuarioDTO dtoEsperado = new UsuarioDTO();
        dtoEsperado.setActivo(false);

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioBD));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioBD);
        when(modelMapper.map(usuarioBD, UsuarioDTO.class)).thenReturn(dtoEsperado);

        UsuarioDTO resultado = usuarioService.desactivateUser(idUsuario);

        assertNotNull(resultado);
        assertFalse(resultado.getActivo());
    }

    @Test
    void updateOK() {
        UsuarioDTO dtoEntrada = new UsuarioDTO();
        dtoEntrada.setIdUsuario(1);
        dtoEntrada.setCorreoUsuario("test@test.com");

        Usuario usuarioBD = new Usuario();
        usuarioBD.setIdUsuario(1);
        usuarioBD.setCorreoUsuario("test@test.com");

        UsuarioDTO dtoSalida = new UsuarioDTO();
        dtoSalida.setIdUsuario(1);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioBD));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioBD);
        when(modelMapper.map(usuarioBD, UsuarioDTO.class)).thenReturn(dtoSalida);

        UsuarioDTO resultado = usuarioService.update(dtoEntrada);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
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
    }

    @Test
    void anhadirRolOK() {
        Integer idTarget = 5;
        Integer idRol = 2;

        Usuario usuarioTarget = new Usuario();
        usuarioTarget.setIdUsuario(idTarget);
        // NO TIENE ROLES AÚN

        Rol nuevoRol = new Rol();
        nuevoRol.setIdRol(idRol);

        UsuarioDTO dtoSalida = new UsuarioDTO();
        dtoSalida.setIdUsuario(idTarget);

        when(usuarioRepository.findById(idTarget)).thenReturn(Optional.of(usuarioTarget));
        when(rolRepository.findById(idRol)).thenReturn(Optional.of(nuevoRol));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTarget);
        when(modelMapper.map(usuarioTarget, UsuarioDTO.class)).thenReturn(dtoSalida);

        UsuarioDTO resultado = usuarioService.anhadirRol(idTarget, idRol); // 👈 Ya no se pasa idAdmin

        assertNotNull(resultado);
        assertTrue(usuarioTarget.getRoles().contains(nuevoRol));
        verify(usuarioRepository, times(1)).save(usuarioTarget);
    }

    @Test
    void anhadirRolYaExistenteMAL() {
        Integer idTarget = 5;
        Integer idRol = 2;

        Usuario usuarioTarget = new Usuario();
        usuarioTarget.setIdUsuario(idTarget);
        
        Rol rolExistente = new Rol();
        rolExistente.setIdRol(idRol);
        usuarioTarget.getRoles().add(rolExistente); // 👈 Ya tiene el rol

        when(usuarioRepository.findById(idTarget)).thenReturn(Optional.of(usuarioTarget));
        when(rolRepository.findById(idRol)).thenReturn(Optional.of(rolExistente));

        assertThrows(BadRequestException.class, () -> {
            usuarioService.anhadirRol(idTarget, idRol);
        });

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void removeRolOK() {
        Integer idTarget = 5;
        Integer idRol = 2;

        Usuario usuarioTarget = new Usuario();
        usuarioTarget.setIdUsuario(idTarget);
        
        Rol rolAQuitar = new Rol();
        rolAQuitar.setIdRol(idRol);
        Rol rolDeReserva = new Rol();
        rolDeReserva.setIdRol(3);
        
        usuarioTarget.getRoles().add(rolAQuitar);
        usuarioTarget.getRoles().add(rolDeReserva);

        when(usuarioRepository.findById(idTarget)).thenReturn(Optional.of(usuarioTarget));
        when(rolRepository.findById(idRol)).thenReturn(Optional.of(rolAQuitar));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTarget);

        UsuarioRolDTO resultado = usuarioService.removeRol(idTarget, idRol); 

        assertNotNull(resultado);
        assertFalse(usuarioTarget.getRoles().contains(rolAQuitar));
        verify(usuarioRepository, times(1)).save(usuarioTarget);
    }

    @Test
    void removeUltimoRolMAL() {
        Integer idTarget = 5;
        Integer idRol = 2;

        Usuario usuarioTarget = new Usuario();
        usuarioTarget.setIdUsuario(idTarget);
        
        Rol elUnicoRol = new Rol();
        elUnicoRol.setIdRol(idRol);
        usuarioTarget.getRoles().add(elUnicoRol);

        when(usuarioRepository.findById(idTarget)).thenReturn(Optional.of(usuarioTarget));
        when(rolRepository.findById(idRol)).thenReturn(Optional.of(elUnicoRol));

        assertThrows(BadRequestException.class, () -> {
            usuarioService.removeRol(idTarget, idRol);
        });

        verify(usuarioRepository, never()).save(any());
    }
}