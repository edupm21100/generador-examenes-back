package com.eduardo.examen_backend.services;

import java.util.List;

import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.dto.UsuarioDTO;

public interface RolService {

    RolDTO save(RolDTO rolDTO);

    List<RolDTO> findAll();

    RolDTO findById(Integer idRol);

    RolDTO update(RolDTO rolDTO);

    RolDTO desactivateRol(Integer idRol);

    List<UsuarioDTO> findUsuariosByRol(Integer idRol);
}