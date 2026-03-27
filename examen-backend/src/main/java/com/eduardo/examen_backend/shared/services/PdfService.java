package com.eduardo.examen_backend.shared.services;

import java.util.Map;

/**
 * Servicio encargado de la generación y exportación de documentos PDF.
 * Permite renderizar plantillas dinámicas inyectando variables de contexto.
 * * @author Eduardo
 * @version 1.0
 */
public interface PdfService {

    /**
     * Genera un archivo PDF a partir de una plantilla y un mapa de datos.
     *
     * @param templateName El nombre del archivo de la plantilla (sin extensión).
     * Debe encontrarse en el directorio resources/templates/.
     * @param variables    Un mapa (Clave-Valor) con los datos dinámicos que 
     * serán inyectados en la plantilla durante el renderizado.
     * @return Un array de bytes (byte[]) que representa el documento PDF generado.
     * @throws com.eduardo.examen_backend.shared.exceptions.PdfGenerationException Si ocurre un error de parseo o I/O.
     */
    byte[] generarPdfDesdeHtml(String templateName, Map<String, Object> variables);

}