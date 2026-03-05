package com.eduardo.examen_backend.models;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

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
import jakarta.persistence.Transient;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;
    @Column(name = "nombre_usuario")
    private String nombreUsuario;
    @Column(name = "apellido_usuario")
    private String apellidoUsuario;
    @Column(name = "correo_usuario")
    private String correoUsuario;
    @Column(name = "contrasenha_usuario")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String contrasenhaUsuario;
    @Column(name = "activo")
    private boolean activo;

    public Usuario(String nombreUsuario, String apellidoUsuario, String correoUsuario, String contrasenhaUsuario,
            boolean activo) {
        this.nombreUsuario = nombreUsuario;
        this.apellidoUsuario = apellidoUsuario;
        this.correoUsuario = correoUsuario;
        this.contrasenhaUsuario = contrasenhaUsuario;
        this.activo = activo;
    }

    public Usuario() {
        // EXIGENCIAS DE HIBERNATE
    }

    // * * * * * * * * * * * *GETTERS & SETTERS* * * * * * * * * * * * * * *\\
    public void setIdUsuario(Integer idUsuario) {this.idUsuario = idUsuario;}
    public int getIdUsuario() {return idUsuario;}

    public String getNombreUsuario() {return nombreUsuario;}
    public void setNombreUsuario(String nombreUsuario) {this.nombreUsuario = nombreUsuario; }

    public String getApellidoUsuario() {return apellidoUsuario;}
    public void setApellidoUsuario(String apellidoUsuario) {this.apellidoUsuario = apellidoUsuario;}

    public String getCorreoUsuario() {return correoUsuario;}
    public void setCorreoUsuario(String correoUsuario) {this.correoUsuario = correoUsuario;}

    public String getContrasenhaUsuario() {return contrasenhaUsuario;}
    public void setContrasenhaUsuario(String contrasenhaUsuario) {this.contrasenhaUsuario = contrasenhaUsuario;}

    public boolean isActivo() {return activo;}
    public void setActivo(boolean activo) {this.activo = activo;}

    //HERRAMIENTA PARA ACTUALIZAR LA TABLA INTERMEDIA
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuarios_roles",
        joinColumns = @JoinColumn(name = "id_usuario"),
        inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private Set<Rol> roles = new HashSet<>();
    public Set<Rol> getRoles() {return roles;}
    public void setRoles(Set<Rol> roles) {this.roles = roles;}

    //DETECTOR DE ADMINS
    @Transient
    private boolean admin;

    public boolean isAdmin() {return admin;}
    public void setAdmin(boolean admin) {this.admin = admin;}

    
}
