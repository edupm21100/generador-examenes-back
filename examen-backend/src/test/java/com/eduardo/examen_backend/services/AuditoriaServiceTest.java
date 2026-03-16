package com.eduardo.examen_backend.services;

import com.eduardo.examen_backend.incidencias.Incidencia;
import com.eduardo.examen_backend.incidencias.IncidenciaRepository;
import com.eduardo.examen_backend.shared.services.AuditoriaServiceImpl;
import com.eduardo.examen_backend.usuarios.Usuario;
import com.eduardo.examen_backend.usuarios.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditoriaServiceTest {

    @Mock
    private IncidenciaRepository incidenciaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AuditoriaServiceImpl auditoriaService;

    @AfterEach
    void cleanUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registrarIncidencia_ConUsuarioLogueado_DeberiaGuardarCorrectamente() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/test");

        Exception ex = new RuntimeException("Error de prueba");
        
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("test@test.com");
        when(auth.getName()).thenReturn("test@test.com");
        SecurityContextHolder.setContext(securityContext);

        Usuario user = new Usuario();
        user.setIdUsuario(1);
        when(usuarioRepository.findByCorreoUsuario("test@test.com")).thenReturn(Optional.of(user));

        auditoriaService.registrarIncidencia(ex, request);

        verify(incidenciaRepository, times(1)).save(any(Incidencia.class));
        verify(usuarioRepository, times(1)).findByCorreoUsuario("test@test.com");
    }

    @Test
    void registrarIncidencia_SinUsuario_DeberiaGuardarComoAnonimo() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/public");
        Exception ex = new Exception("Error anónimo");

        auditoriaService.registrarIncidencia(ex, request);

        verify(incidenciaRepository, times(1)).save(any(Incidencia.class));
        verifyNoInteractions(usuarioRepository);
    }

@Test
    void registrarIncidencia_CuandoExcepcionEsExterna_DeberiaPonerOrigenDesconocido() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/ext");
        
        Exception ex = new RuntimeException("Error externo de prueba");
        
        StackTraceElement[] fakeStackTrace = {
            new StackTraceElement("org.springframework.Web", "doDispatch", "Web.java", 10)
        };
        
        ex.setStackTrace(fakeStackTrace);

        auditoriaService.registrarIncidencia(ex, request);

        verify(incidenciaRepository, times(1)).save(argThat(incidencia -> 
            incidencia.getClase().equals("Desconocida/Framework")
        ));
    }
}