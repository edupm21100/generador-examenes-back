package com.eduardo.examen_backend.examenes.intentos;

import com.eduardo.examen_backend.examenes.opciones.Opcion;
import com.eduardo.examen_backend.examenes.preguntas.Pregunta;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "respuestas_alumnos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaAlumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRespuestaAlumno;

    // A qué intento (examen real del alumno) pertenece esta respuesta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_intento", nullable = false)
    private Intento intento;

    // Qué pregunta concreta de la bolsa estaba respondiendo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pregunta", nullable = false)
    private Pregunta pregunta;

    // Qué opción marcó (puede ser nula si la dejó en blanco)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_opcion")
    private Opcion opcionSeleccionada;
}