package com.eduardo.examen_backend.examenes.intentos;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.examen_backend.examenes.Examen;
import com.eduardo.examen_backend.examenes.ExamenRepository;
import com.eduardo.examen_backend.examenes.opciones.Opcion;
import com.eduardo.examen_backend.examenes.preguntas.Pregunta;
import com.eduardo.examen_backend.shared.exceptions.BadRequestException;
import com.eduardo.examen_backend.shared.exceptions.NotFoundException;
import com.eduardo.examen_backend.usuarios.Usuario;
import com.eduardo.examen_backend.usuarios.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IntentoServiceImpl implements IntentoService {

    private final IntentoRepository intentoRepository;
    private final ExamenRepository examenRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public IntentoDTO realizarExamen(String correoLogueado, IntentoDTO dto) {
        Usuario alumno = usuarioRepository.findByCorreoUsuario(correoLogueado)
            .orElseThrow(() -> new NotFoundException("Usuario no encontrado."));

        Examen examen = examenRepository.findById(dto.getIdExamen())
            .orElseThrow(() -> new NotFoundException("El examen no existe."));

        if (!examen.isActivo()) {
            throw new BadRequestException("El examen no está activo actualmente.");
        }

        if (examen.getPreguntas().isEmpty()) {
            throw new BadRequestException("El examen no tiene preguntas, no se puede realizar.");
        }

        // Crear el cascarón del Intento
        Intento intento = Intento.builder()
            .usuario(alumno)
            .examen(examen)
            .build();

        int respuestasCorrectas = 0;

        // Motor de Autocorrección
        for (RespuestaAlumnoDTO respuestaDTO : dto.getRespuestas()) {
            // 1. Validar que la pregunta existe en ESTE examen
            Pregunta preguntaReal = examen.getPreguntas().stream()
                .filter(p -> p.getIdPregunta().equals(respuestaDTO.getIdPregunta()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("La pregunta ID " + respuestaDTO.getIdPregunta() + " no pertenece a este examen."));

            Opcion opcionElegida = null;
            
            // 2. Si el alumno marcó una opción (no la dejó en blanco)
            if (respuestaDTO.getIdOpcionSeleccionada() != null) {
                opcionElegida = preguntaReal.getOpciones().stream()
                    .filter(o -> o.getIdOpcion().equals(respuestaDTO.getIdOpcionSeleccionada()))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("La opción ID " + respuestaDTO.getIdOpcionSeleccionada() + " no es válida para esta pregunta."));
                
                if (opcionElegida.isEsCorrecta()) {
                    respuestasCorrectas++;
                }
            }

            // 3. Crear la respuesta y atarla al intento (usando el Helper que vimos antes)
            RespuestaAlumno respuestaAlumno = RespuestaAlumno.builder()
                .pregunta(preguntaReal)
                .opcionSeleccionada(opcionElegida)
                .build();
            
            intento.addRespuesta(respuestaAlumno);
        }

        // 4. Calcular nota sobre 10
        double notaFinal = ((double) respuestasCorrectas / examen.getPreguntas().size()) * 10.0;
        intento.setNota(Math.round(notaFinal * 100.0) / 100.0); // Redondear a 2 decimales

        // 5. Guardar (el CascadeType guardará las respuestas también)
        Intento intentoGuardado = intentoRepository.save(intento);
        return mapearADTO(intentoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IntentoDTO> obtenerMisIntentos(String correoLogueado) {
        return intentoRepository.findByUsuario_CorreoUsuarioOrderByFechaRealizacionDesc(correoLogueado)
            .stream().map(this::mapearADTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public IntentoDTO obtenerDetalleIntento(Integer idIntento, String correoLogueado) {
        Intento intento = intentoRepository.findById(idIntento)
            .orElseThrow(() -> new NotFoundException("El intento no existe."));

        // Seguridad extra: Un alumno no puede ver el examen de otro
        if (!intento.getUsuario().getCorreoUsuario().equals(correoLogueado) && !intento.getUsuario().isAdmin()) {
            throw new BadRequestException("No tienes permiso para ver este examen.");
        }

        return mapearADTO(intento);
    }

    // --- Helpers ---
    private IntentoDTO mapearADTO(Intento intento) {
        List<RespuestaAlumnoDTO> respuestasDTO = intento.getRespuestas().stream().map(r -> {
            boolean correcta = r.getOpcionSeleccionada() != null && r.getOpcionSeleccionada().isEsCorrecta();
            return RespuestaAlumnoDTO.builder()
                .idPregunta(r.getPregunta().getIdPregunta())
                .idOpcionSeleccionada(r.getOpcionSeleccionada() != null ? r.getOpcionSeleccionada().getIdOpcion() : null)
                .esCorrecta(correcta)
                .build();
        }).toList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        return IntentoDTO.builder()
            .idIntento(intento.getIdIntento())
            .idExamen(intento.getExamen().getIdExamen())
            .tituloExamen(intento.getExamen().getTitulo())
            .nota(intento.getNota())
            .fechaRealizacion(intento.getFechaRealizacion().format(formatter))
            .respuestas(respuestasDTO)
            .build();
    }
}