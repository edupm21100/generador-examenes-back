package com.eduardo.examen_backend.services;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.models.Rol;
import com.eduardo.examen_backend.repositories.RolRepository;

@Service
public class RolService {
    private RolRepository rolRepository;
    private final ModelMapper modelMapper;

    public RolService(RolRepository rolRepository, ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.rolRepository = rolRepository;
    }

    public RolDTO save(RolDTO rolDTO) {
        Rol rol = modelMapper.map(rolDTO, Rol.class);
        return modelMapper.map(rolRepository.save(rol), RolDTO.class);
    }

    public List<RolDTO> findAll() {
        return rolRepository.findByActivoTrue().stream().map(
                rol -> modelMapper.map(rol, RolDTO.class)).toList();
    }

    public Optional<RolDTO> findById(Integer idRol) {
        return rolRepository.findById(idRol).map(
                rolDB -> modelMapper.map(rolDB, RolDTO.class));
    }

    public Optional<RolDTO> update(RolDTO rolDTO) {
        Rol rol = modelMapper.map(rolDTO, Rol.class);
        return rolRepository.findById(rol.getIdRol()).map(
                rolBD -> {
                    rolBD.setNombreRol(rol.getNombreRol());
                    rolBD.setActivo(rol.isActivo());
                    return modelMapper.map(rolRepository.save(rolBD), RolDTO.class);
                });
    }

    // BORRADO LÓGICO
    public RolDTO desactivateRol(Integer idRol) {
        Rol rol = rolRepository.findById(idRol).orElseThrow(
                () -> new RuntimeException("El usuario que se desactiva/activa no existe"));
        rol.setActivo(!rol.isActivo());
        Rol rolGuardado = rolRepository.save(rol);
        return modelMapper.map(rolGuardado, RolDTO.class);
    }

}