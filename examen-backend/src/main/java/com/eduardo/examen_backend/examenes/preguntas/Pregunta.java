package com.eduardo.examen_backend.examenes.preguntas;

import java.util.ArrayList;
import java.util.List;

import com.eduardo.examen_backend.examenes.categorias.Categoria;
import com.eduardo.examen_backend.examenes.opciones.Opcion;

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
@Table(name = "preguntas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPregunta;

    @Column(name = "enunciado", nullable = false, columnDefinition = "TEXT")
    private String enunciado;

    // Relación con Categoría (Geografía, Mates, etc.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    // Relación bidireccional con sus Opciones (Respuestas)
    @OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Opcion> opciones = new ArrayList<>();

    // Método de utilidad (Helper) para mantener la sincronización bidireccional
    public void addOpcion(Opcion opcion) {
        opciones.add(opcion);
        opcion.setPregunta(this);
    }

    public void removeOpcion(Opcion opcion) {
        opciones.remove(opcion);
        opcion.setPregunta(null);
    }
}