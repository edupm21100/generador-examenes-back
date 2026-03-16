package com.eduardo.examen_backend.roles;

import java.util.HashSet;
import java.util.Set;

import com.eduardo.examen_backend.usuarios.Usuario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRol;
    
    @Column(name = "nombre_rol")
    private String nombreRol;
    
    @Column(name = "activo")
    private boolean activo;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuarios_roles",
        joinColumns = @JoinColumn(name = "id_rol"),
        inverseJoinColumns = @JoinColumn(name = "id_usuario")
    )
    @Builder.Default
    private Set<Usuario> usuarios = new HashSet<>();

}