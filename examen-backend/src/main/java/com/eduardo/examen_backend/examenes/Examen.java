package com.eduardo.examen_backend.examenes;

import java.util.HashSet;
import java.util.Set;

import com.eduardo.examen_backend.examenes.preguntas.Pregunta;
import com.eduardo.examen_backend.usuarios.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.ForeignKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "examenes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Examen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idExamen;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "activo")
    @Builder.Default
    private boolean activo = true;

    // TABLA INTERMEDIA: Relación Muchos a Muchos con Preguntas
    @ManyToMany(fetch = FetchType.LAZY)
    @Builder.Default
    @JoinTable(name = "examenes_preguntas", joinColumns = @JoinColumn(name = "id_examen"), inverseJoinColumns = @JoinColumn(name = "id_pregunta"))
    private Set<Pregunta> preguntas = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profesor", nullable = false, foreignKey = @ForeignKey(name = "fk_examenes_profesor")
    )
    private Usuario profesor;
}