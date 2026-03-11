package com.eduardo.examen_backend.services;

import com.eduardo.examen_backend.models.Incidencia;
import com.eduardo.examen_backend.repositories.IncidenciaRepository;
import com.eduardo.examen_backend.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Slf4j
@Service
public class AuditoriaServiceImpl {

    private final IncidenciaRepository incidenciaRepository;
    private final UsuarioRepository usuarioRepository;

    public AuditoriaServiceImpl(IncidenciaRepository incidenciaRepository, UsuarioRepository usuarioRepository) {
        this.incidenciaRepository = incidenciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void registrarIncidencia(Exception ex, HttpServletRequest request) {
        String endpoint = request.getMethod() + " " + request.getRequestURI();
        String origen = extraerClaseYMetodo(ex);
        
        Incidencia incidencia = new Incidencia(
                endpoint, 
                ex.getClass().getSimpleName(), 
                origen.split("\\|")[0],
                origen.split("\\|")[1],
                extraerTraza(ex), 
                LocalDateTime.now(), 
                obtenerIdUsuarioLogueado()
        );
        
        incidenciaRepository.save(incidencia);
    }

    private String extraerTraza(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private String extraerClaseYMetodo(Exception ex) {
        for (StackTraceElement element : ex.getStackTrace()) {
            if (element.getClassName().startsWith("com.eduardo.examen_backend") && !element.getMethodName().contains("$")) {
                return element.getClassName() + "|" + element.getMethodName() + " (Línea: " + element.getLineNumber() + ")";
            }
        }
        return "Desconocida/Framework|Desconocido";
    }

    private Integer obtenerIdUsuarioLogueado() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
                return usuarioRepository.findByCorreoUsuario(auth.getName())
                        .map(usuario -> usuario.getIdUsuario())
                        .orElse(null);
            }
        } catch (Exception e) {
            log.error("Error al extraer usuario logueado en auditoría", e);
        }
        return null;
    }
}