package com.eduardo.examen_backend.examenes.intentos;

import java.util.List;

/**
 * Interfaz que gestiona la resolución de exámenes por parte de los usuarios.
 * Contiene el motor de autocorrección y la gestión del historial de calificaciones.
 */
public interface IntentoService {

    /**
     * Procesa las respuestas enviadas por un alumno, calcula la nota final y guarda el historial.
     * @param correoLogueado Correo extraído del Token JWT por seguridad.
     * @param intentoDTO Objeto con el ID del examen y la lista de respuestas marcadas.
     * @return Objeto {@link IntentoDTO} con la nota calculada y el detalle de aciertos/fallos.
     * @throws com.eduardo.examen_backend.shared.exceptions.BadRequestException si el examen no está activo o se envían respuestas inválidas.
     */
    IntentoDTO realizarExamen(String correoLogueado, IntentoDTO intentoDTO);

    /**
     * Recupera el historial de exámenes realizados por el usuario actual, ordenados por fecha descendente.
     * @param correoLogueado Correo extraído del Token JWT.
     * @return Lista de objetos {@link IntentoDTO} básicos (sin el desglose de preguntas).
     */
    List<IntentoDTO> obtenerMisIntentos(String correoLogueado);

    /**
     * Recupera el detalle completo de un examen ya realizado (plantilla + respuestas del alumno).
     * @param idIntento Identificador del intento a consultar.
     * @param correoLogueado Correo extraído del Token JWT para validar permisos.
     * @return Objeto {@link IntentoDTO} detallado.
     * @throws com.eduardo.examen_backend.shared.exceptions.BadRequestException si un alumno intenta ver el examen de otro.
     */
    IntentoDTO obtenerDetalleIntento(Integer idIntento, String correoLogueado);
}