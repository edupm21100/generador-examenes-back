package com.eduardo.examen_backend.examenes.preguntas;

import java.util.List;

/**
 * Interfaz que define las operaciones de negocio para la gestión del banco de Preguntas.
 * Gestiona automáticamente el ciclo de vida de las opciones (respuestas) asociadas.
 */
public interface PreguntaService {

    /**
     * Recupera el entero banco de preguntas registradas en el sistema.
     * @return Lista de objetos {@link PreguntaDTO} con sus opciones.
     */
    List<PreguntaDTO> obtenerTodas();

    /**
     * Recupera todas las preguntas asociadas a una temática específica.
     * @param idCategoria Identificador de la categoría a filtrar.
     * @return Lista de objetos {@link PreguntaDTO} pertenecientes a la categoría.
     */
    List<PreguntaDTO> obtenerPorCategoria(Integer idCategoria);

    /**
     * Busca una pregunta específica por su identificador.
     * @param id Identificador numérico de la pregunta.
     * @return Objeto {@link PreguntaDTO} con sus opciones integradas.
     * @throws com.eduardo.examen_backend.shared.exceptions.NotFoundException si el ID no existe.
     */
    PreguntaDTO obtenerPorId(Integer id);

    /**
     * Crea una nueva pregunta en el banco junto con su array de opciones.
     * Valida que la categoría exista y que al menos una opción sea marcada como correcta.
     * @param preguntaDTO Datos de la nueva pregunta y sus respuestas.
     * @return Objeto {@link PreguntaDTO} persistido con sus IDs autogenerados.
     * @throws com.eduardo.examen_backend.shared.exceptions.NotFoundException si la categoría asignada no existe.
     * @throws com.eduardo.examen_backend.shared.exceptions.BadRequestException si ninguna de las opciones es correcta.
     */
    PreguntaDTO crearPregunta(PreguntaDTO preguntaDTO);

    /**
     * Elimina una pregunta de la base de datos.
     * Por eliminación en cascada, también borrará físicamente todas sus opciones asociadas.
     * @param id Identificador de la pregunta a eliminar.
     * @throws com.eduardo.examen_backend.shared.exceptions.NotFoundException si el ID no existe.
     */
    void eliminarPregunta(Integer id);
}