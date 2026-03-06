package com.eduardo.examen_backend.services;

import com.eduardo.examen_backend.dto.IncidenciaDTO;
import com.eduardo.examen_backend.exceptions.NotFoundException;
import com.eduardo.examen_backend.repositories.IncidenciaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class IncidenciaServiceImpl implements IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final ModelMapper modelMapper;

    public IncidenciaServiceImpl(IncidenciaRepository incidenciaRepository, ModelMapper modelMapper) {
        this.incidenciaRepository = incidenciaRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<IncidenciaDTO> findAll() {
        return incidenciaRepository.findAll().stream()
                .map(incidencia -> modelMapper.map(incidencia, IncidenciaDTO.class))
                .toList();
    }

    @Override
    public IncidenciaDTO findById(Integer idIncidencia) {
        return incidenciaRepository.findById(idIncidencia)
                .map(incidencia -> modelMapper.map(incidencia, IncidenciaDTO.class))
                .orElseThrow(() -> new NotFoundException("La incidencia con ID " + idIncidencia + " no existe"));
    }
}