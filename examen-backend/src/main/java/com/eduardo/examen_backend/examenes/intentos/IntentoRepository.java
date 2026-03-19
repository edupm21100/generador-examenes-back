package com.eduardo.examen_backend.examenes.intentos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntentoRepository extends JpaRepository<Intento, Integer> {
    List<Intento> findByUsuario_CorreoUsuarioOrderByFechaRealizacionDesc(String correoUsuario);
    List<Intento> findByExamen_IdExamenOrderByNotaDesc(Integer idExamen);
}