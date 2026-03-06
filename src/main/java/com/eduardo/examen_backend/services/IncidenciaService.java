package com.eduardo.examen_backend.services;

import com.eduardo.examen_backend.dto.IncidenciaDTO;
import java.util.List;

public interface IncidenciaService {
    
    List<IncidenciaDTO> findAll();
    
    IncidenciaDTO findById(Integer idIncidencia);
}