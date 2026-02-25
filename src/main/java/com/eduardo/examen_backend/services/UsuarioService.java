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
                    return modelMapper.map(usuarioRepository, UsuarioDTO.class);
                }).collect(Collectors.toList());
    }

    public Optional<UsuarioDTO> getById(Integer id_usuario) {
        return usuarioRepository.findById(id_usuario).map(
                usuarioDB -> {
                    return modelMapper.map(usuarioDB, UsuarioDTO.class);
                });
    }

    public boolean deleteById(Integer id_usuario) {
        return usuarioRepository.findById(id_usuario).map(
                usuario -> {
                    usuarioRepository.delete(usuario);
                    return true;
                }).orElse(false);
    }

    public Optional<UsuarioDTO> update(UsuarioDTO usuarioDTO) {
        // DTO USUARIO
        Usuario usuario = modelMapper.map(usuarioDTO, Usuario.class);

        return usuarioRepository.findById(usuario.getId_usuario()).map(
                usuarioBD -> {
                    usuarioBD.setNombre_usuario(usuario.getNombre_usuario());
                    usuarioBD.setApellido_usuario(usuario.getApellido_usuario());
                    usuarioBD.setCorreo_usuario(usuario.getCorreo_usuario());
                    usuarioBD.setContrasenha_usuario(usuario.getContrasenha_usuario());
                    usuarioBD.setActivo(usuario.isActivo());
                    return modelMapper.map(usuarioRepository.save(usuarioBD), UsuarioDTO.class);
                });
    }

}
