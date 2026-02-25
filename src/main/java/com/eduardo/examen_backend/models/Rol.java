package com.eduardo.examen_backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_rol;
    @Column(name = "nombre_rol")
    private String nombre_rol;
    @Column(name = "activo")
    private boolean activo;

    public Rol(String nombre_rol, boolean activo) {
        this.nombre_rol = nombre_rol;
        this.activo = activo;
    }

    // CONSTRUCTOR VACIO POR GRACIA Y EXIGENCIA DEL MÉTODO HIBERNATE
    public Rol() {
    }

    // * * * * * * * * * * * *GETTERS & SETTERS* * * * * * * * * * * * * * *\\
    public void setId_rol(Integer id_rol) {
        this.id_rol = id_rol;
    }

    public Integer getId_rol() {
        return id_rol;
    }

    public String getNombre_rol() {
        return nombre_rol;
    }

    public void setNombre_rol(String nombre_rol) {
        this.nombre_rol = nombre_rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

}
