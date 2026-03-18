package com.eduardo.examen_backend.examenes.preguntas;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreguntaRepository extends JpaRepository<Pregunta, Integer> {
    List<Pregunta> findByCategoria_IdCategoria(Integer idCategoria);
}