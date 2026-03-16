package com.eduardo.examen_backend.incidencias;

import java.util.List;

/**
 * Servicio dedicado a la consulta del registro histórico de incidencias y errores.
 * Permite a los administradores auditar los fallos capturados automáticamente por el sistema.
 * * @author Eduardo
 * @version 1.0
 */
public interface IncidenciaService {
    
    /**
     * Recupera el catálogo completo de incidencias registradas en la base de datos,
     * incluyendo trazas de error y endpoints afectados.
     * * @return Lista de DTOs con la información de todas las incidencias.
     */
    List<IncidenciaDTO> findAll();
    
    /**
     * Busca los detalles técnicos profundos de una incidencia específica a través de su identificador.
     * * @param idIncidencia Identificador numérico único de la incidencia.
     * @return El DTO con la traza completa y metadatos del error encontrado.
     */
    IncidenciaDTO findById(Integer idIncidencia);
}