package com.eduardo.examen_backend.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.dto.UsuarioDTO;
import com.eduardo.examen_backend.exceptions.BadRequestException;
import com.eduardo.examen_backend.exceptions.DuplicateException;
import com.eduardo.examen_backend.exceptions.NotFoundException;
import com.eduardo.examen_backend.models.Rol;
import com.eduardo.examen_backend.repositories.RolRepository;
import com.eduardo.examen_backend.repositories.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;
    private static final Integer ID_ROL_ADMIN = 1;

    public RolServiceImpl(RolRepository rolRepository, ModelMapper modelMapper, UsuarioRepository usuarioRepository) {
        this.modelMapper = modelMapper;
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private String getUsuarioAccion() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "Sistema/Anónimo";
    }

    @Override
    @Transactional
    public RolDTO save(RolDTO rolDTO) {
        if (rolRepository.existsByNombreRol(rolDTO.getNombreRol())) {
            throw new DuplicateException("El rol que se pretende añadir ya existe");
        }
        Rol rol = modelMapper.map(rolDTO, Rol.class);
        log.info("[Autor: {}] Ha creado un nuevo rol en el sistema: '{}' con ID {}", 
        getUsuarioAccion(), rol.getNombreRol(), rol.getIdRol());
        return modelMapper.map(rolRepository.save(rol), RolDTO.class);
    }

    @Override
    public List<RolDTO> findAll() {
        log.info("[Autor: {}] Ha consultado el catálogo completo de roles activos.", getUsuarioAccion());
        return rolRepository.findByActivoTrue().stream()
                .map(rol -> modelMapper.map(rol, RolDTO.class)).toList();
    }

    @Override
    public RolDTO findById(Integer idRol) {
        log.info("[Autor: {}] Ha consultado los detalles del rol con ID {}.", getUsuarioAccion(), idRol);
        return rolRepository.findById(idRol)
                .map(rolDB -> modelMapper.map(rolDB, RolDTO.class))
                .orElseThrow(() -> new NotFoundException("Id: " + idRol + " no encontrado"));
    }

 @Override
    @Transactional
    public RolDTO update(RolDTO rolDTO) {
        Rol rol = rolRepository.findById(rolDTO.getIdRol())
            .orElseThrow(() -> new NotFoundException("El rol que se pretende modificar no existe"));
        if (rol.getIdRol().equals(ID_ROL_ADMIN) && !rolDTO.getNombreRol().equalsIgnoreCase(rol.getNombreRol())) {
            throw new BadRequestException("No puedes cambiar el nombre del rol administrador principal.");
        }
        if (!rol.getNombreRol().equals(rolDTO.getNombreRol()) &&
            rolRepository.existsByNombreRol(rolDTO.getNombreRol())) {
            throw new DuplicateException("Ese rol '" + rolDTO.getNombreRol() + "' ya existe");
        }
        String nombreAntiguo = rol.getNombreRol();
        rol.setNombreRol(rolDTO.getNombreRol());
        Rol rolGuardado = rolRepository.save(rol);
        log.info("[Autor: {}] Ha actualizado el rol ID {}. Nombre anterior: '{}' | Nombre nuevo: '{}'", 
            getUsuarioAccion(), rolGuardado.getIdRol(), nombreAntiguo, rolGuardado.getNombreRol());
        return modelMapper.map(rolGuardado, RolDTO.class);
    }

@Override
    @Transactional
    public RolDTO desactivateRol(Integer idRol) {
        Rol rol = rolRepository.findById(idRol).orElseThrow(
            () -> new NotFoundException("El Rol con ID " + idRol + " no existe"));
        if (rol.getIdRol().equals(ID_ROL_ADMIN)) {
            throw new BadRequestException(
            "Error Crítico: No se puede desactivar el rol de Administrador. Riesgo de bloqueo total del sistema.");
        }
        rol.setActivo(!rol.isActivo());
        Rol rolGuardado = rolRepository.save(rol);
        String estado = rolGuardado.isActivo() ? "REACTIVADO" : "DESACTIVADO";
        log.info("[Autor: {}] Ha {} el rol '{}' (ID: {}).", 
            getUsuarioAccion(), estado, rolGuardado.getNombreRol(), rolGuardado.getIdRol());
        return modelMapper.map(rolGuardado, RolDTO.class);
    }

    @Override
    public List<UsuarioDTO> findUsuariosByRol(Integer idRol) {
        if (!rolRepository.existsById(idRol)) {
            throw new NotFoundException("No se pueden listar usuarios: El Rol con ID " + idRol + " no existe");
        }
        log.info("[Autor: {}] Ha listado los usuarios que poseen el rol con ID {}.", getUsuarioAccion(), idRol);
        return usuarioRepository.findByRolesIdRol(idRol).stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioDTO.class))
                .toList();
    }
}