package com.eduardo.examen_backend.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.models.Rol;
import com.eduardo.examen_backend.repositories.RolRepository;

@Service
public class RolService {
    private RolRepository rolRepository;
    private final ModelMapper modelMapper;

    public RolService(RolRepository rolRepository, @Qualifier("rolModelMapper") ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.rolRepository = rolRepository;
    }

    public RolDTO save(RolDTO rolDTO) {
        Rol rol = modelMapper.map(rolDTO, Rol.class);
        return modelMapper.map(rolRepository.save(rol), RolDTO.class);
    }

    public List<RolDTO> findAll() {
        return rolRepository.findAll().stream().map(
                rol -> {
                    return modelMapper.map(rolRepository, RolDTO.class);
                }).collect(Collectors.toList());
    }

    public Optional<RolDTO> getById(Integer id_rol) {
        return rolRepository.findById(id_rol).map(
                rolDB -> {
                    return modelMapper.map(rolDB, RolDTO.class);
                });
    }

    public boolean deleteById(Integer id_rol) {
        return rolRepository.findById(id_rol).map(
                rol -> {
                    rolRepository.delete(rol);
                    return true;
                }).orElse(false);
    }

    public Optional<RolDTO> update(RolDTO rolDTO) {
        Rol rol = modelMapper.map(rolDTO, Rol.class);
        return rolRepository.findById(rol.getId_rol()).map(
                rolBD -> {
                    rolBD.setNombre_rol(rol.getNombre_rol());
                    rolBD.setActivo(rol.isActivo());
                   return modelMapper.map(rolRepository.save(rolBD), RolDTO.class);
                });
    }
}
