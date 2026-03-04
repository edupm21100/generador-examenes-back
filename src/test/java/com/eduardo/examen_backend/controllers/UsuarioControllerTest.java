package com.eduardo.examen_backend.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eduardo.examen_backend.exceptions.NotFoundException;
import com.eduardo.examen_backend.exceptions.BadRequestException;

import com.eduardo.examen_backend.dto.PasswordDTO;
import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.dto.UsuarioDTO;
import com.eduardo.examen_backend.dto.UsuarioRolDTO;
import com.eduardo.examen_backend.services.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UsuarioService usuarioService;


    @Test
    void findAll_CuandoHayUsuarios_DeberiaDevolver200YLista() throws Exception {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setIdUsuario(1);
        usuarioDTO.setNombreUsuario("Eduardo");
        usuarioDTO.setApellidoUsuario("Pérez");
        when(usuarioService.findAll()).thenReturn(Arrays.asList(usuarioDTO));

        mockMvc.perform(get("/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreUsuario").value("Eduardo"));
    }

    @Test
    void findAll_CuandoNoHayUsuarios_DeberiaDevolver200YListaVacia() throws Exception {
        when(usuarioService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Esperamos 200 OK
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void findById_CuandoExiste_DeberiaDevolver200() throws Exception {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setIdUsuario(1);
        usuarioDTO.setNombreUsuario("Eduardo");

        when(usuarioService.findById(1)).thenReturn(usuarioDTO);

        mockMvc.perform(get("/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUsuario").value("Eduardo"));
    }

    @Test
    void findById_CuandoNoExiste_DeberiaDevolver404() throws Exception {
        when(usuarioService.findById(99)).thenThrow(new NotFoundException("Usuario no encontrado"));

        mockMvc.perform(get("/usuarios/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void save_DeberiaDevolver201YUsuarioCreado() throws Exception {
        UsuarioDTO dtoEntrada = new UsuarioDTO();
        dtoEntrada.setNombreUsuario("Laura");
        dtoEntrada.setApellidoUsuario("García");
        dtoEntrada.setCorreoUsuario("laura@test.com"); 
        dtoEntrada.setContrasenhaUsuario("123456");

        UsuarioDTO dtoSalida = new UsuarioDTO();
        dtoSalida.setNombreUsuario("Laura");

        when(usuarioService.save(any(UsuarioDTO.class))).thenReturn(dtoSalida);

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreUsuario").value("Laura"));
    }

    @Test
    void update_DeberiaDevolver200() throws Exception {
        UsuarioDTO dtoEntrada = new UsuarioDTO();
        dtoEntrada.setIdUsuario(1);
        dtoEntrada.setNombreUsuario("Modificado");
        dtoEntrada.setApellidoUsuario("García");
        dtoEntrada.setCorreoUsuario("modificado@test.com");
        dtoEntrada.setContrasenhaUsuario("123456");

        when(usuarioService.update(any(UsuarioDTO.class))).thenReturn(dtoEntrada);

        mockMvc.perform(put("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUsuario").value("Modificado"));
    }

    @Test
    void cambiarContrasenha_DeberiaRecibirJSONYDevolver200() throws Exception {
        Integer idUsuario = 5;
        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setNewPassword("123456");
        passwordDTO.setOldPassword("000000");

        UsuarioDTO dtoSalida = new UsuarioDTO();
        dtoSalida.setIdUsuario(idUsuario);

        when(usuarioService.changeContrasenha(eq(idUsuario), any(PasswordDTO.class))).thenReturn(dtoSalida);

        mockMvc.perform(put("/usuarios/{idUsuario}/password", idUsuario)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void cambiarContrasenha_ConContrasenhaViejaIncorrecta_DeberiaDevolver400() throws Exception {
        Integer idUsuario = 5;
        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setNewPassword("123456");
        passwordDTO.setOldPassword("mal_password");

        when(usuarioService.changeContrasenha(eq(idUsuario), any(PasswordDTO.class)))
                .thenThrow(new BadRequestException("Contraseña inválida"));

        mockMvc.perform(put("/usuarios/{idUsuario}/password", idUsuario)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void desactivateUser_DeberiaDevolver200() throws Exception {
        UsuarioDTO dtoSalida = new UsuarioDTO();
        dtoSalida.setActivo(false);

        when(usuarioService.desactivateUser(1)).thenReturn(dtoSalida);

        mockMvc.perform(put("/usuarios/desactivar/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void anhadirRol_DeberiaDevolver200() throws Exception {
        UsuarioDTO dtoSalida = new UsuarioDTO();
        dtoSalida.setIdUsuario(1);

        when(usuarioService.anhadirRol(1, 2, 1)).thenReturn(dtoSalida);

        mockMvc.perform(put("/usuarios/1/roles")
                .param("idRol", "2")
                .param("idAdmin", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void removeRol_DeberiaDevolver200() throws Exception {
        UsuarioRolDTO dtoSalida = new UsuarioRolDTO();
        dtoSalida.setIdUsuario(1);
        dtoSalida.setIdRol(2);

        when(usuarioService.removeRol(1, 2, 1)).thenReturn(dtoSalida);

        mockMvc.perform(put("/usuarios/1/roles/2")
                .param("idAdmin", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findRolByUsuario_DeberiaDevolver200YLista() throws Exception {
        RolDTO rol = new RolDTO();
        rol.setNombreRol("ADMIN");

        when(usuarioService.findRolByUsuario(1)).thenReturn(List.of(rol));

        mockMvc.perform(get("/usuarios/1/roles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreRol").value("ADMIN"));
    }
}