package com.eduardo.examen_backend.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.eduardo.examen_backend.models.Usuario;
import com.eduardo.examen_backend.repositories.UsuarioRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailService(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }

@Override
public UserDetails loadUserByUsername(String correoUsuario) throws UsernameNotFoundException{
    Usuario usuario = usuarioRepository.findByCorreoUsuario(correoUsuario)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

    List<GrantedAuthority> authorities = usuario.getRoles().stream()
        .map(rol -> new SimpleGrantedAuthority("ROLE_"+rol.getNombreRol().toUpperCase()))
        .collect(Collectors.toList());

    return new org.springframework.security.core.userdetails.User(
        usuario.getCorreoUsuario(),
        usuario.getContrasenhaUsuario(),
        authorities
    );
}
}
