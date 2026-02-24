package com.eduardo.examen_backend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduardo.examen_backend.models.Usuario;
import com.eduardo.examen_backend.services.UsuarioService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    //POST
    //http://localhost:8080/usuarios
    //RECUERDA QUE SPRING SECURITY ESTÁ DESHABILITADO POR AHORA
    @PostMapping
    public Usuario save(@RequestBody Usuario usuario){
        return usuarioService.save(usuario);
    }

    //GET
    //http://localhost:8080/usuarios
    @GetMapping
    public List<Usuario> findAll(){
        return usuarioService.findAll();
    }

    //GET
    //http://localhost:8080/usuarios/{id_usuario}
    @GetMapping("/{id_usuario}")
    public Usuario findById(@PathVariable Integer id_usuario){
        return usuarioService.getById(id_usuario);
    }

    //PUT
    //http://localhost:8080/usuarios
    @PutMapping
    public Usuario update(@RequestBody Usuario usuario){
        return usuarioService.update(usuario);
    }

    //DELETE
    //http://localhost:8080/usuarios/{id_usuario}
    @DeleteMapping("/{id_usuario}")
    public void deleteById(@PathVariable Integer id_usuario){
        usuarioService.deleteById(id_usuario);
    }

}
