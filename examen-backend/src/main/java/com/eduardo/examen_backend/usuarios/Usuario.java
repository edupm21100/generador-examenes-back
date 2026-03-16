package com.eduardo.examen_backend.usuarios;

import java.util.HashSet;
import java.util.Set;

import com.eduardo.examen_backend.roles.Rol;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @Builder.Default
    private boolean activo = true;

    //HERRAMIENTA PARA ACTUALIZAR LA TABLA INTERMEDIA
    @ManyToMany(fetch = FetchType.EAGER)
    @Builder.Default
    @JoinTable(
        name = "usuarios_roles",
        joinColumns = @JoinColumn(name = "id_usuario"),
        inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private Set<Rol> roles = new HashSet<>();
    
    //DETECTOR DE ADMINS
    @Transient
    private boolean admin;
}
