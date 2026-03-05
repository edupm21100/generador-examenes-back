package com.eduardo.examen_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.eduardo.examen_backend.models.Incidencia;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Integer> {
}