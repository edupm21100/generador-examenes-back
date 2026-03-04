package com.eduardo.examen_backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eduardo.examen_backend.models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    List<Usuario> findByRolesIdRol(Integer idRol);
    List<Usuario> findByActivoTrue();
}