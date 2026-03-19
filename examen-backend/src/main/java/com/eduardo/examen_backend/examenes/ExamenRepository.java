package com.eduardo.examen_backend.examenes;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamenRepository extends JpaRepository<Examen, Integer> {
    List<Examen> findByActivoTrue();
}