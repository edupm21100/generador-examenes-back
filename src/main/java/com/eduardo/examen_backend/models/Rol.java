package com.eduardo.examen_backend.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "roles")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRol;
    @Column(name = "nombre_rol")
    private String nombreRol;
    @Column(name = "activo")
    private boolean activo;

    public Rol(String nombreRol, boolean activo) {
        this.nombreRol = nombreRol;
        this.activo = activo;
    }

    // CONSTRUCTOR VACIO POR GRACIA Y EXIGENCIA DEL MÉTODO HIBERNATE
    public Rol() {
    }

    // * * * * * * * * * * * *GETTERS & SETTERS* * * * * * * * * * * * * * *\\
    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuarios_roles",
        joinColumns = @JoinColumn(name = "id_rol"),
        inverseJoinColumns = @JoinColumn(name = "id_usuario")
    )
    private Set<Usuario> usuarios = new HashSet<>();
    
    public Set<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Set<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

}
