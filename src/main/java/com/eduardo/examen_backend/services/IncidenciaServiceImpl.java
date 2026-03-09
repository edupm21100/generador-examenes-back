package com.eduardo.examen_backend.services;

import com.eduardo.examen_backend.dto.IncidenciaDTO;
import com.eduardo.examen_backend.exceptions.NotFoundException;
import com.eduardo.examen_backend.repositories.IncidenciaRepository;

import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class IncidenciaServiceImpl implements IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final ModelMapper modelMapper;

    public IncidenciaServiceImpl(IncidenciaRepository incidenciaRepository, ModelMapper modelMapper) {
        this.incidenciaRepository = incidenciaRepository;
        this.modelMapper = modelMapper;
    }

    private String getUsuarioAccion() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return auth.getName();
        }
        return "Sistema/Anónimo";
    }

    @Override
    public List<IncidenciaDTO> findAll() {
    log.info("[Autor: {}] Ha consultado el listado histórico completo de incidencias y errores.", getUsuarioAccion());
        return incidenciaRepository.findAll().stream()
                .map(incidencia -> modelMapper.map(incidencia, IncidenciaDTO.class))
                .toList();
    }

    @Override
    public IncidenciaDTO findById(Integer idIncidencia) {
        log.info("[Autor: {}] Ha consultado la traza y detalles de la incidencia con ID {}.", getUsuarioAccion(), idIncidencia);
        return incidenciaRepository.findById(idIncidencia)
                .map(incidencia -> modelMapper.map(incidencia, IncidenciaDTO.class))
                .orElseThrow(() -> new NotFoundException("La incidencia con ID " + idIncidencia + " no existe"));
    }
}