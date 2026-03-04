package com.eduardo.examen_backend.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.dto.UsuarioDTO;
import com.eduardo.examen_backend.exceptions.DuplicateException;
import com.eduardo.examen_backend.exceptions.NotFoundException;
import com.eduardo.examen_backend.models.Rol;
import com.eduardo.examen_backend.repositories.RolRepository;
import com.eduardo.examen_backend.repositories.UsuarioRepository;

@Service
public class RolService {
    private RolRepository rolRepository;
    private UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    public RolService(RolRepository rolRepository, ModelMapper modelMapper, UsuarioRepository usuarioRepository) {
        this.modelMapper = modelMapper;
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public RolDTO save(RolDTO rolDTO) {
        if (rolRepository.existsByNombreRol(rolDTO.getNombreRol())) {
            throw new DuplicateException("El rol que se pretende añadir ya existe");
        }
        Rol rol = modelMapper.map(rolDTO, Rol.class);
        return modelMapper.map(rolRepository.save(rol), RolDTO.class);
    }

    public List<RolDTO> findAll() {
        return rolRepository.findByActivoTrue().stream().map(
                rol -> modelMapper.map(rol, RolDTO.class)).toList();
    }

    public RolDTO findById(Integer idRol) {
        return rolRepository.findById(idRol)
                .map(rolDB -> modelMapper.map(rolDB, RolDTO.class))
                .orElseThrow(() -> new NotFoundException("Id: " + idRol + " no encontrado"));
    }

    public RolDTO update(RolDTO rolDTO) {
        Rol rol = rolRepository.findById(rolDTO.getIdRol())
                .orElseThrow(() -> new NotFoundException("El rol que se pretende modificar no existe"));
        if (!rol.getNombreRol().equals(rolDTO.getNombreRol()) &&
                rolRepository.existsByNombreRol(rolDTO.getNombreRol())) {
            throw new DuplicateException("Ese rol '" + rolDTO.getNombreRol() + "' ya existe");
        }
        rol.setNombreRol(rolDTO.getNombreRol());
        return modelMapper.map(rolRepository.save(rol), RolDTO.class);
    }

    // BORRADO LÓGICO
    public RolDTO desactivateRol(Integer idRol) {
        Rol rol = rolRepository.findById(idRol).orElseThrow(
                () -> new NotFoundException("El Rol con ID " + idRol + " no existe"));
        rol.setActivo(!rol.isActivo());
        Rol rolGuardado = rolRepository.save(rol);
        return modelMapper.map(rolGuardado, RolDTO.class);
    }

    // BUSCAR USUARIOS POR ROLES
    public List<UsuarioDTO> findUsuariosByRol(Integer idRol) {
        if (!rolRepository.existsById(idRol)) {
            throw new NotFoundException("No se pueden listar usuarios: El Rol con ID " + idRol + " no existe");
        }
        return usuarioRepository.findByRolesIdRol(idRol).stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioDTO.class))
                .toList();
    }

}