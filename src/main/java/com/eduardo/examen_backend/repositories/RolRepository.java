package com.eduardo.examen_backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eduardo.examen_backend.models.Rol;

public interface RolRepository extends JpaRepository<Rol, Integer>{
    List<Rol> findByUsuariosIdUsuario(Integer idUsuario);
    List<Rol> findByActivoTrue();
    boolean existsByNombreRol(String nombreRol);
}
