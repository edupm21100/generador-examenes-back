package com.eduardo.examen_backend.examenes.preguntas;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.examen_backend.examenes.categorias.Categoria;
import com.eduardo.examen_backend.examenes.categorias.CategoriaRepository;
import com.eduardo.examen_backend.examenes.opciones.Opcion;
import com.eduardo.examen_backend.examenes.opciones.OpcionDTO;
import com.eduardo.examen_backend.shared.exceptions.BadRequestException;
import com.eduardo.examen_backend.shared.exceptions.NotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreguntaServiceImpl implements PreguntaService {

    private final PreguntaRepository preguntaRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PreguntaDTO> obtenerTodas() {
        return preguntaRepository.findAll().stream()
                .filter(p -> p.getActivo() != null && p.getActivo())
                .map(this::mapearADTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PreguntaDTO> obtenerPorCategoria(Integer idCategoria) {
        if (!categoriaRepository.existsById(idCategoria)) {
            throw new NotFoundException("La categoría con ID " + idCategoria + " no existe.");
        }
        return preguntaRepository.findByCategoria_IdCategoria(idCategoria).stream()
                .filter(p -> p.getActivo() != null && p.getActivo()) // Solo las activas
                .map(this::mapearADTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PreguntaDTO obtenerPorId(Integer id) {
        Pregunta pregunta = preguntaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("La pregunta con ID " + id + " no existe."));
        return mapearADTO(pregunta);
    }

    @Override
    @Transactional
    public PreguntaDTO crearPregunta(PreguntaDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new NotFoundException("La categoría especificada no existe."));

        boolean tieneCorrecta = dto.getOpciones().stream().anyMatch(OpcionDTO::getEsCorrecta);
        if (!tieneCorrecta) {
            throw new BadRequestException("La pregunta debe tener al menos una opción correcta.");
        }

        // 3. Crear la entidad Pregunta
        Pregunta pregunta = Pregunta.builder()
                .enunciado(dto.getEnunciado())
                .categoria(categoria)
                .build();

        // 4. Transformar y atar las opciones a la pregunta
        for (OpcionDTO optDto : dto.getOpciones()) {
            Opcion nuevaOpcion = Opcion.builder()
                    .texto(optDto.getTexto())
                    .esCorrecta(optDto.getEsCorrecta() != null && optDto.getEsCorrecta())
                    .build();
            pregunta.addOpcion(nuevaOpcion);
        }

        // 5. Guardar (El CascadeType.ALL guarda la pregunta y sus opciones
        // automáticamente)
        Pregunta preguntaGuardada = preguntaRepository.save(pregunta);
        log.info("Pregunta creada de categoría {}", preguntaGuardada.getCategoria().getNombre());
        return mapearADTO(preguntaGuardada);
    }

    @Override
    @Transactional
    public PreguntaDTO cambiarEstadoActivo(Integer id) {
        Pregunta pregunta = preguntaRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("La pregunta con ID " + id + " no existe."));
        
        boolean estadoActual = pregunta.getActivo() != null && pregunta.getActivo();
        
        pregunta.setActivo(!estadoActual);
        
        Pregunta preguntaGuardada = preguntaRepository.save(pregunta);
        log.info("Pregunta ID {} ha cambiado su estado a: {}", id, Boolean.TRUE.equals(preguntaGuardada.getActivo()) ? "ACTIVA" : "DESACTIVADA");
        
        return mapearADTO(preguntaGuardada);
    }

    // --- Métodos Helper ---
    private PreguntaDTO mapearADTO(Pregunta pregunta) {
        List<OpcionDTO> opcionesDTO = pregunta.getOpciones().stream()
                .map(o -> OpcionDTO.builder()
                        .idOpcion(o.getIdOpcion())
                        .texto(o.getTexto())
                        .esCorrecta(o.isEsCorrecta())
                        .build())
                        .toList();

        return PreguntaDTO.builder()
                .idPregunta(pregunta.getIdPregunta())
                .enunciado(pregunta.getEnunciado())
                .idCategoria(pregunta.getCategoria().getIdCategoria())
                .nombreCategoria(pregunta.getCategoria().getNombre())
                .activo(pregunta.getActivo() != null ? pregunta.getActivo() : false)
                .opciones(opcionesDTO)
                .build();
    }
}