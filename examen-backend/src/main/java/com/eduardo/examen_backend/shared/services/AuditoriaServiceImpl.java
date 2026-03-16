package com.eduardo.examen_backend.shared.services;

import com.eduardo.examen_backend.incidencias.Incidencia;
import com.eduardo.examen_backend.incidencias.IncidenciaRepository;
import com.eduardo.examen_backend.usuarios.UsuarioRepository;

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
public class AuditoriaServiceImpl implements AuditoriaService {

    private final IncidenciaRepository incidenciaRepository;
    private final UsuarioRepository usuarioRepository;

    public AuditoriaServiceImpl(IncidenciaRepository incidenciaRepository, UsuarioRepository usuarioRepository) {
        this.incidenciaRepository = incidenciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void registrarIncidencia(Exception ex, HttpServletRequest request) {
        String endpoint = request.getMethod() + " " + request.getRequestURI();
        String origen = extraerClaseYMetodo(ex);
        
        new Incidencia();
        Incidencia incidencia = Incidencia.builder()
                .endpoint(endpoint)
                .tipo(ex.getClass().getSimpleName())
                .clase(origen.split("\\|")[0])
                .metodo(origen.split("\\|")[1])
                .traza(extraerTraza(ex))
                .fecha(LocalDateTime.now())
                .idUsuario(obtenerIdUsuarioLogueado())
                .build();
        
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