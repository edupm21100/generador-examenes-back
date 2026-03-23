package com.eduardo.examen_backend.examenes;

import com.eduardo.examen_backend.examenes.preguntas.PreguntaViews;

public class ExamenViews {
    public interface DiscreetExam {}
    public interface IndiscreetExam extends DiscreetExam, PreguntaViews.DiscreetQuestion {}
}    