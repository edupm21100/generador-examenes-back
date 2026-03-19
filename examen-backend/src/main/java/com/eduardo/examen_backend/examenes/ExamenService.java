package com.eduardo.examen_backend.examenes;

import java.util.List;

/**
 * Interfaz que define la lógica de negocio para la gestión de Plantillas de Exámenes.
 * Permite orquestar la creación del examen y la asignación de preguntas al mismo.
 */
public interface ExamenService {

    /**
     * Recupera el catálogo completo de exámenes del sistema (activos e inactivos).
     * @return Lista de objetos {@link ExamenDTO}.
     */
    List<ExamenDTO> obtenerTodos();

    /**
     * Recupera únicamente los exámenes que están visibles para los alumnos.
     * @return Lista de objetos {@link ExamenDTO} con estado activo = true.
     */
    List<ExamenDTO> obtenerActivos();

    /**
     * Busca la plantilla completa de un examen, incluyendo todas sus preguntas y opciones.
     * @param id Identificador numérico del examen.
     * @return Objeto {@link ExamenDTO} detallado.
     * @throws com.eduardo.examen_backend.shared.exceptions.NotFoundException si el ID no existe.
     */
    ExamenDTO obtenerPorId(Integer id);

    /**
     * Crea un "cascarón" vacío para un nuevo examen (sin preguntas asignadas aún).
     * @param examenDTO Datos básicos del examen (título, descripción).
     * @return Objeto {@link ExamenDTO} con el ID autogenerado.
     */
    ExamenDTO crearExamen(ExamenDTO examenDTO);

    /**
     * Actualiza la información básica de una plantilla de examen existente.
     * @param id Identificador del examen a modificar.
     * @param examenDTO Nuevos datos a aplicar.
     * @return Objeto {@link ExamenDTO} actualizado.
     */
    ExamenDTO actualizarExamen(Integer id, ExamenDTO examenDTO);

    /**
     * Elimina físicamente una plantilla de examen.
     * @param id Identificador del examen a eliminar.
     */
    void eliminarExamen(Integer id);

    /**
     * Conmuta el estado de visibilidad del examen (Activo <-> Inactivo).
     * @param id Identificador del examen.
     * @return Objeto {@link ExamenDTO} con el nuevo estado aplicado.
     */
    ExamenDTO cambiarEstadoActivo(Integer id);

    /**
     * Vincula una pregunta existente en el banco de preguntas a la plantilla de este examen.
     * @param idExamen Identificador de la plantilla.
     * @param idPregunta Identificador de la pregunta a añadir.
     * @return Objeto {@link ExamenDTO} actualizado con la nueva pregunta en su lista.
     * @throws com.eduardo.examen_backend.shared.exceptions.DuplicateException si la pregunta ya estaba vinculada.
     */
    ExamenDTO anhadirPregunta(Integer idExamen, Integer idPregunta);

    /**
     * Desvincula una pregunta de la plantilla del examen (No borra la pregunta del sistema).
     * @param idExamen Identificador de la plantilla.
     * @param idPregunta Identificador de la pregunta a retirar.
     * @return Objeto {@link ExamenDTO} actualizado.
     */
    ExamenDTO quitarPregunta(Integer idExamen, Integer idPregunta);
}