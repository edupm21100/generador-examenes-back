package com.eduardo.examen_backend.services;

import java.util.List;

import java.util.Optional;
import org.springframework.stereotype.Service;

import com.eduardo.examen_backend.models.Usuario;
import com.eduardo.examen_backend.repositories.UsuarioRepository;

@Service
public class UsuarioService {
    private UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario save(Usuario usuario){
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> findAll(){
        return usuarioRepository.findAll();
    }

    public Usuario getById(Integer id_usuario){
        return usuarioRepository.findById(id_usuario).get();
    }

    public void deleteById(Integer id_usuario){
        usuarioRepository.deleteById(id_usuario);
    }

    public Usuario update(Usuario usuario){
        Optional<Usuario> usuarioBBDD = usuarioRepository.findById(usuario.getId_usuario());
        Usuario usuarioBD = usuarioBBDD.get();

        usuarioBD.setNombre_usuario(usuario.getNombre_usuario());
        usuarioBD.setApellido_usuario(usuario.getApellido_usuario());
        usuarioBD.setCorreo_usuario(usuario.getCorreo_usuario());
        usuarioBD.setContrasenha_usuario(usuario.getContrasenha_usuario());
        usuarioBD.setActivo(usuario.isActivo());

        return usuarioRepository.save(usuarioBD);
    }


}
