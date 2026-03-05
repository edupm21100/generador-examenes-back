package com.eduardo.examen_backend.services;

import com.eduardo.examen_backend.dto.IncidenciaDTO;
import com.eduardo.examen_backend.exceptions.NotFoundException;
import com.eduardo.examen_backend.models.Incidencia;
import com.eduardo.examen_backend.repositories.IncidenciaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final ModelMapper modelMapper;

    public IncidenciaService(IncidenciaRepository incidenciaRepository, ModelMapper modelMapper) {
        this.incidenciaRepository = incidenciaRepository;
        this.modelMapper = modelMapper;
    }

    // Listar TODAS
    public List<IncidenciaDTO> findAll() {
        return incidenciaRepository.findAll().stream()
                .map(incidencia -> modelMapper.map(incidencia, IncidenciaDTO.class))
                .toList();
    }

    // Buscar UNA por ID
    public IncidenciaDTO findById(Integer idIncidencia) {
        Incidencia incidencia = incidenciaRepository.findById(idIncidencia)
                .orElseThrow(() -> new NotFoundException("La incidencia con ID " + idIncidencia + " no existe"));
        
        return modelMapper.map(incidencia, IncidenciaDTO.class);
    }
}