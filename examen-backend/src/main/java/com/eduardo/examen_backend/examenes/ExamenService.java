package com.eduardo.examen_backend.examenes;

import java.util.List;

/**
 * Interfaz para la gestión de Plantillas de Exámenes.
 */
public interface ExamenService {

    List<ExamenDTO> obtenerTodos();
    List<ExamenDTO> obtenerActivos();
    ExamenDTO obtenerPorId(Integer id);
    ExamenDTO crearExamen(ExamenDTO examenDTO);
    ExamenDTO actualizarExamen(Integer id, ExamenDTO examenDTO);
    void eliminarExamen(Integer id);
    ExamenDTO cambiarEstadoActivo(Integer id);

    // --- Gestión de Preguntas en el Examen ---
    /**
     * Añade una pregunta existente a la plantilla del examen.
     * @throws com.eduardo.examen_backend.shared.exceptions.DuplicateException si la pregunta ya está en el examen.
     */
    ExamenDTO anhadirPregunta(Integer idExamen, Integer idPregunta);

    /**
     * Retira una pregunta de la plantilla del examen (No borra la pregunta de la BD).
     */
    ExamenDTO quitarPregunta(Integer idExamen, Integer idPregunta);
}