package com.eduardo.examen_backend.services;

import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.eduardo.examen_backend.dto.UsuarioDTO;
import com.eduardo.examen_backend.models.Usuario;
import com.eduardo.examen_backend.repositories.UsuarioRepository;

@Service
public class UsuarioService {
    private UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    public UsuarioService(UsuarioRepository usuarioRepository,
            @Qualifier("usuarioModelMapper") ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
    }

    public UsuarioDTO save(UsuarioDTO usuarioDTO) {
        Usuario usuario = modelMapper.map(usuarioDTO, Usuario.class);
        return modelMapper.map(usuarioRepository.save(usuario), UsuarioDTO.class);
    }

    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAll().stream().map(
                usuario -> {
                    return modelMapper.map(usuario, UsuarioDTO.class);
                }).collect(Collectors.toList());
    }

    public Optional<UsuarioDTO> findById(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario).map(
                usuarioDB -> {
                    return modelMapper.map(usuarioDB, UsuarioDTO.class);
                });
    }

    public Optional<UsuarioDTO> update(UsuarioDTO usuarioDTO) {
        // DTO USUARIO
        Usuario usuario = modelMapper.map(usuarioDTO, Usuario.class);

        return usuarioRepository.findById(usuario.getIdUsuario()).map(
                usuarioBD -> {
                    usuarioBD.setNombreUsuario(usuario.getNombreUsuario());
                    usuarioBD.setApellidoUsuario(usuario.getApellidoUsuario());
                    usuarioBD.setCorreoUsuario(usuario.getCorreoUsuario());
                    usuarioBD.setContrasenhaUsuario(usuario.getContrasenhaUsuario());
                    usuarioBD.setActivo(usuario.isActivo());
                    return modelMapper.map(usuarioRepository.save(usuarioBD), UsuarioDTO.class);
                });
    }

    public boolean deleteById(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario).map(
                usuario -> {
                    usuarioRepository.delete(usuario);
                    return true;
                }).orElse(false);
    }

}
