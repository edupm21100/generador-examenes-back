package com.eduardo.examen_backend.examenes;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.examen_backend.examenes.opciones.OpcionDTO;
import com.eduardo.examen_backend.examenes.preguntas.Pregunta;
import com.eduardo.examen_backend.examenes.preguntas.PreguntaDTO;
import com.eduardo.examen_backend.examenes.preguntas.PreguntaRepository;
import com.eduardo.examen_backend.shared.exceptions.DuplicateException;
import com.eduardo.examen_backend.shared.exceptions.NotFoundException;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamenServiceImpl implements ExamenService {

    private final ExamenRepository examenRepository;
    private final PreguntaRepository preguntaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ExamenDTO> obtenerTodos() {
        return examenRepository.findAll().stream().map(this::mapearADTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamenDTO> obtenerActivos() {
        return examenRepository.findByActivoTrue().stream().map(this::mapearADTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ExamenDTO obtenerPorId(Integer id) {
        Examen examen = buscarExamenOThrow(id);
        return mapearADTO(examen);
    }

@Override
    @Transactional
    public ExamenDTO crearExamen(ExamenDTO dto) {
        Examen examen = Examen.builder().titulo(dto.getTitulo()).descripcion(dto.getDescripcion()).activo(dto.isActivo()).build();
        Examen examenGuardado = examenRepository.save(examen);
        
        // LOG CRÍTICO
        log.info("Nueva plantilla de examen creada con éxito. ID: {}, Título: '{}'", examenGuardado.getIdExamen(), examenGuardado.getTitulo());
        
        return mapearADTO(examenGuardado);
    }

    @Override
    @Transactional
    public ExamenDTO actualizarExamen(Integer id, ExamenDTO dto) {
        Examen examen = buscarExamenOThrow(id);
        examen.setTitulo(dto.getTitulo());
        examen.setDescripcion(dto.getDescripcion());
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
    public ExamenDTO cambiarEstadoActivo(Integer id) {
        Examen examen = buscarExamenOThrow(id);
        examen.setActivo(!examen.isActivo());
        return mapearADTO(examenRepository.save(examen));
    }

    @Override
    @Transactional
    public ExamenDTO anhadirPregunta(Integer idExamen, Integer idPregunta) {
        Examen examen = buscarExamenOThrow(idExamen);
        Pregunta pregunta = preguntaRepository.findById(idPregunta)
            .orElseThrow(() -> new NotFoundException("La pregunta con ID " + idPregunta + " no existe."));

        if (examen.getPreguntas().contains(pregunta)) {
            throw new DuplicateException("La pregunta ya pertenece a este examen.");
        }

        examen.getPreguntas().add(pregunta);
        log.info("Pregunta ID {} añadida exitosamente al Examen ID {}", idPregunta, idExamen);
        return mapearADTO(examenRepository.save(examen));
    }

    @Override
    @Transactional
    public ExamenDTO quitarPregunta(Integer idExamen, Integer idPregunta) {
        Examen examen = buscarExamenOThrow(idExamen);
        Pregunta pregunta = preguntaRepository.findById(idPregunta)
            .orElseThrow(() -> new NotFoundException("La pregunta con ID " + idPregunta + " no existe."));

        examen.getPreguntas().remove(pregunta);
        return mapearADTO(examenRepository.save(examen));
    }

    private Examen buscarExamenOThrow(Integer id) {
        return examenRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("El examen con ID " + id + " no existe."));
    }

    private ExamenDTO mapearADTO(Examen examen) {
        Set<PreguntaDTO> preguntasDTO = examen.getPreguntas().stream().map(p -> {
            List<OpcionDTO> opciones = p.getOpciones().stream().map(o -> 
                OpcionDTO.builder().idOpcion(o.getIdOpcion()).texto(o.getTexto()).esCorrecta(o.isEsCorrecta()).build()
            ).toList();

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
            .preguntas(preguntasDTO)
            .build();
    }
}