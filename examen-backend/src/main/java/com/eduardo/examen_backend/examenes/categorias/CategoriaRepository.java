package com.eduardo.examen_backend.examenes.categorias;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    boolean existsByNombre(String nombre);
    Optional<Categoria> findByNombre(String nombre);
}