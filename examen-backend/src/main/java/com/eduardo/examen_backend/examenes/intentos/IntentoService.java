package com.eduardo.examen_backend.examenes.intentos;

import java.util.List;

public interface IntentoService {
    IntentoDTO realizarExamen(String correoLogueado, IntentoDTO intentoDTO);
    List<IntentoDTO> obtenerMisIntentos(String correoLogueado);
    IntentoDTO obtenerDetalleIntento(Integer idIntento, String correoLogueado);
}