package com.eduardo.examen_backend.examenes.intentos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.eduardo.examen_backend.examenes.Examen;
import com.eduardo.examen_backend.usuarios.Usuario;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "intentos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Intento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idIntento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_examen", nullable = false)
    private Examen examen;

    @Column(name = "fecha_realizacion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaRealizacion = LocalDateTime.now();

    @Column(name = "nota")
    private Double nota;

    // Relación con las respuestas que marcó el alumno en este intento
    @OneToMany(mappedBy = "intento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RespuestaAlumno> respuestas = new ArrayList<>();

    // Métodos Helper para mantener limpia la relación bidireccional
    public void addRespuesta(RespuestaAlumno respuesta) {
        respuestas.add(respuesta);
        respuesta.setIntento(this);
    }

    public void removeRespuesta(RespuestaAlumno respuesta) {
        respuestas.remove(respuesta);
        respuesta.setIntento(null);
    }
}