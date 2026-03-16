package com.eduardo.examen_backend.shared.services;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Servicio interceptor encargado de la auditoría y registro de errores del sistema.
 * Captura las excepciones no controladas, extrae su traza y el usuario responsable,
 * y las persiste en la base de datos como incidencias.
 * * @author Eduardo
 * @version 1.0
 */
public interface AuditoriaService {

    /**
     * Procesa una excepción generada durante una petición HTTP y la registra en el sistema.
     * Analiza el StackTrace para determinar la clase y línea exactas donde se originó el fallo.
     *
     * @param ex La excepción capturada por el manejador global.
     * @param request La petición HTTP original que provocó el error (para extraer el endpoint).
     */
    void registrarIncidencia(Exception ex, HttpServletRequest request);
}