package com.eduardo.examen_backend.examenes;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.examen_backend.examenes.intentos.Intento;
import com.eduardo.examen_backend.examenes.intentos.IntentoRepository;
import com.eduardo.examen_backend.examenes.opciones.OpcionDTO;
import com.eduardo.examen_backend.examenes.preguntas.Pregunta;
import com.eduardo.examen_backend.examenes.preguntas.PreguntaDTO;
import com.eduardo.examen_backend.examenes.preguntas.PreguntaRepository;
import com.eduardo.examen_backend.roles.Rol;
import com.eduardo.examen_backend.shared.exceptions.BadRequestException;
import com.eduardo.examen_backend.shared.exceptions.NotFoundException;
import com.eduardo.examen_backend.shared.services.PdfService;
import com.eduardo.examen_backend.usuarios.Usuario;
import com.eduardo.examen_backend.usuarios.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamenServiceImpl implements ExamenService {

    private final ExamenRepository examenRepository;
    private final PreguntaRepository preguntaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ExamenDTO> obtenerTodos() {
        return examenRepository.findAll().stream().map(this::mapearADTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamenDTO> obtenerActivos() {
        return examenRepository.findByActivoTrue().stream().map(this::mapearADTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ExamenDTO obtenerPorId(Integer id) {
        Examen examen = buscarExamenOThrow(id);
        return mapearADTO(examen);
    }

    @Override
    @Transactional
    public ExamenDTO crearExamen(ExamenDTO dto, String correoLogueado) {
        Usuario profesor = usuarioRepository.findByCorreoUsuario(correoLogueado)
                .orElseThrow(() -> new NotFoundException("El usuario logueado no existe en la base de datos."));
        Examen examen = Examen.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .activo(dto.getActivo() != null && dto.getActivo())
                .profesor(profesor)
                .build();
        Examen examenGuardado = examenRepository.save(examen);
        log.info("[Autor: {}] Nueva plantilla de examen creada con éxito. ID: {}", correoLogueado,
                examenGuardado.getIdExamen());
        return mapearADTO(examenGuardado);
    }

    @Override
    @Transactional
    public ExamenDTO actualizarExamen(Integer id, ExamenDTO dto, String correoLogueado) {
        Examen examen = buscarExamenOThrow(id);
        verificarPermisos(examen, correoLogueado);
        examen.setTitulo(dto.getTitulo());
        examen.setDescripcion(dto.getDescripcion());
        if (dto.getActivo() != null) {
            examen.setActivo(dto.getActivo());
        }
        return mapearADTO(examenRepository.save(examen));
    }

    @Override
    @Transactional
    public void eliminarExamen(Integer id) {
        if (!examenRepository.existsById(id)) {
            throw new NotFoundException("El examen con ID " + id + " no existe.");
        }
        examenRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ExamenDTO cambiarEstadoActivo(Integer id, String correoLogueado) {
        Examen examen = buscarExamenOThrow(id);
        verificarPermisos(examen, correoLogueado);
        examen.setActivo(!examen.isActivo());
        return mapearADTO(examenRepository.save(examen));
    }

@Override
    @Transactional
    public ExamenDTO anhadirPreguntas(Integer idExamen, List<Integer> idsPreguntas, String correoLogueado) {
        Examen examen = buscarExamenOThrow(idExamen);
        verificarPermisos(examen, correoLogueado);
        List<Pregunta> preguntas = preguntaRepository.findAllById(idsPreguntas);
        if (preguntas.size() != idsPreguntas.size()) {
            throw new NotFoundException("Algunas de las preguntas proporcionadas no existen en la base de datos.");
        }
        boolean hayPreguntasApagadas = preguntas.stream()
            .anyMatch(p -> p.getActivo() != null && !p.getActivo());

        if (hayPreguntasApagadas) {
            throw new BadRequestException("Operación rechazada: Estás intentando añadir preguntas que han sido dadas de baja por un Administrador.");
        }
        examen.getPreguntas().addAll(preguntas);
        log.info("[Autor: {}] Añadidas {} preguntas al Examen ID {}", correoLogueado, preguntas.size(), idExamen);
        return mapearADTO(examenRepository.save(examen));
    }

    @Override
    @Transactional
    public ExamenDTO quitarPreguntas(Integer idExamen, List<Integer> idsPreguntas, String correoLogueado) {
        Examen examen = buscarExamenOThrow(idExamen);
        verificarPermisos(examen, correoLogueado);

        List<Pregunta> preguntasAQuitar = preguntaRepository.findAllById(idsPreguntas);
        examen.getPreguntas().removeAll(preguntasAQuitar);

        log.info("[Autor: {}] Quitadas {} preguntas del Examen ID {}", correoLogueado, preguntasAQuitar.size(),
                idExamen);
        return mapearADTO(examenRepository.save(examen));
    }

    private final IntentoRepository intentoRepository;
    private final PdfService pdfService;

    @Override
    @Transactional(readOnly = true)
    public byte[] generarReporteNotasPdf(Integer idExamen, String correoLogueado) {
        Examen examen = buscarExamenOThrow(idExamen);
        verificarPermisos(examen, correoLogueado);

        List<Intento> todosLosIntentos = intentoRepository.findByExamen_IdExamenOrderByNotaDesc(idExamen);

        List<Intento> intentosFiltrados = todosLosIntentos.stream()
            .collect(Collectors.groupingBy(
                intento -> intento.getUsuario().getIdUsuario(),
                Collectors.maxBy(Comparator.comparing(Intento::getFechaRealizacion))
            ))
            .values().stream()
            .map(Optional::get)
            .sorted(Comparator.comparing(Intento::getNota).reversed())
            .toList();

        Map<String, Object> variables = new HashMap<>();
        variables.put("examen", examen);
        variables.put("listaIntentos", intentosFiltrados);
        variables.put("fecha", LocalDate.now().toString());

        return pdfService.generarPdfDesdeHtml("reporte_notas_examen", variables);
    }

    private ExamenDTO mapearADTO(Examen examen) {
        Set<PreguntaDTO> preguntasDTO = examen.getPreguntas().stream().filter(p -> p.getActivo() != null && p.getActivo())
            .map(p -> {
            List<OpcionDTO> opciones = p.getOpciones().stream().map(o -> OpcionDTO.builder().idOpcion(o.getIdOpcion())
                    .texto(o.getTexto()).esCorrecta(o.isEsCorrecta()).build()).toList();

            return PreguntaDTO.builder()
                    .idPregunta(p.getIdPregunta())
                    .enunciado(p.getEnunciado())
                    .idCategoria(p.getCategoria().getIdCategoria())
                    .nombreCategoria(p.getCategoria().getNombre())
                    .opciones(opciones)
                    .build();
        }).collect(Collectors.toSet());

        return ExamenDTO.builder()
                .idExamen(examen.getIdExamen())
                .titulo(examen.getTitulo())
                .descripcion(examen.getDescripcion())
                .activo(examen.isActivo())
                .idProfesor(examen.getProfesor() != null ? examen.getProfesor().getIdUsuario() : null)
                .nombreProfesor(examen.getProfesor() != null
                        ? examen.getProfesor().getNombreUsuario() + " " + examen.getProfesor().getApellidoUsuario()
                        : "Profesor Desconocido")
                .preguntas(preguntasDTO)
                .build();
    }

    private void verificarPermisos(Examen examen, String correoLogueado) {
        Usuario usuarioLogueado = usuarioRepository.findByCorreoUsuario(correoLogueado)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        boolean esAdmin = usuarioLogueado.getRoles().stream()
                .anyMatch(rol -> rol.getNombreRol().toUpperCase().contains("ADMIN"));

        log.warn("Usuario intentando modificar: {}. Roles encontrados en BD: {}. ¿Es Admin?: {}",
                correoLogueado,
                usuarioLogueado.getRoles().stream().map(Rol::getNombreRol).toList(),
                esAdmin);

        if (!esAdmin && !examen.getProfesor().getIdUsuario().equals(usuarioLogueado.getIdUsuario())) {
            throw new AuthorizationDeniedException(
                    "Acceso Denegado: Solo el creador del examen o un Administrador pueden modificarlo.");
        }
    }

    private Examen buscarExamenOThrow(Integer idExamen) {
        return examenRepository.findById(idExamen)
                .orElseThrow(() -> new NotFoundException("El examen con ID " + idExamen + " no existe."));
    }
}