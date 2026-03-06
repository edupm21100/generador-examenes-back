package com.eduardo.examen_backend.services;

import com.eduardo.examen_backend.dto.PasswordDTO;
import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.dto.UsuarioDTO;
import com.eduardo.examen_backend.dto.UsuarioRolDTO;

import java.util.List;

public interface UsuarioService {
    
    UsuarioDTO save(UsuarioDTO usuarioDTO);
    
    List<UsuarioDTO> findAll();
    
    UsuarioDTO findById(Integer idUsuario);
    
    UsuarioDTO update(UsuarioDTO usuarioDTO);
    
    UsuarioDTO anhadirRol(Integer idUsuarioTarget, Integer idRolNuevo);
    
    UsuarioDTO changeContrasenha( String correoLogueado, PasswordDTO passwordDTO);
    
    UsuarioDTO desactivateUser(Integer idUsuario);
    
    List<RolDTO> findRolByUsuario(Integer idUsuario);
    
    UsuarioRolDTO removeRol(Integer idUsuarioTarget, Integer idRolEliminar);
}