package com.eduardo.examen_backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.eduardo.examen_backend.auth.CustomUserDetailService;
import com.eduardo.examen_backend.roles.Rol;
import com.eduardo.examen_backend.usuarios.Usuario;
import com.eduardo.examen_backend.usuarios.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @Test
    void loadUserByUsernameTestOK() {
        String correoTest = "admin@test.com";
        
        Rol rolAdmin = new Rol();
        rolAdmin.setIdRol(1);
        rolAdmin.setNombreRol("ADMIN");

        Usuario usuarioBD = new Usuario();
        usuarioBD.setIdUsuario(1);
        usuarioBD.setCorreoUsuario(correoTest);
        usuarioBD.setContrasenhaUsuario("12345");
        usuarioBD.setRoles(Set.of(rolAdmin));
        usuarioBD.setActivo(true);

        when(usuarioRepository.findByCorreoUsuario(correoTest)).thenReturn(Optional.of(usuarioBD));

        UserDetails resultado = customUserDetailService.loadUserByUsername(correoTest);

        assertNotNull(resultado, "El UserDetails no debe ser nulo");
        assertEquals(correoTest, resultado.getUsername(), "El username debe coincidir con el correo");
        assertEquals("12345", resultado.getPassword(), "La contraseña debe coincidir");
        
        boolean tieneRolAdmin = resultado.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        assertTrue(tieneRolAdmin, "Debería tener la autoridad ROLE_ADMIN mapeada");
        
        verify(usuarioRepository, times(1)).findByCorreoUsuario(correoTest);
    }

    @Test
    void loadUserByUsernameUsuarioInexistenteMAL() {
        String correoFantasma = "fantasma@test.com";

        when(usuarioRepository.findByCorreoUsuario(correoFantasma)).thenReturn(Optional.empty());

        UsernameNotFoundException excepcion = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailService.loadUserByUsername(correoFantasma);
        });

        assertEquals("Usuario no encontrado", excepcion.getMessage());
        verify(usuarioRepository, times(1)).findByCorreoUsuario(correoFantasma);
    }
}