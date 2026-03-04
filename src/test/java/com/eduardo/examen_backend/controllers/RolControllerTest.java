package com.eduardo.examen_backend.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// Importamos tus excepciones
import com.eduardo.examen_backend.exceptions.NotFoundException;

import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.dto.UsuarioDTO;
import com.eduardo.examen_backend.services.RolService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(RolController.class)
class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RolService rolService;
    @Test
    void findAll_CuandoHayRoles_DeberiaDevolver200YLista() throws Exception {
        RolDTO rolDTO = new RolDTO();
        rolDTO.setNombreRol("ADMIN");

        when(rolService.findAll()).thenReturn(Arrays.asList(rolDTO));

        mockMvc.perform(get("/roles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreRol").value("ADMIN"));
    }

    @Test
    void findById_CuandoExiste_DeberiaDevolver200() throws Exception {
        RolDTO rolDTO = new RolDTO();
        rolDTO.setNombreRol("USER");

        when(rolService.findById(1)).thenReturn(rolDTO);

        mockMvc.perform(get("/roles/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreRol").value("USER"));
    }

    @Test
    void findById_CuandoNoExiste_DeberiaDevolver404() throws Exception {
        when(rolService.findById(99)).thenThrow(new NotFoundException("Rol no encontrado"));

        mockMvc.perform(get("/roles/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void save_DeberiaDevolver201YRolCreado() throws Exception {
        RolDTO dtoEntrada = new RolDTO();
        dtoEntrada.setNombreRol("MANAGER");

        RolDTO dtoSalida = new RolDTO();
        dtoSalida.setNombreRol("MANAGER");

        when(rolService.save(any(RolDTO.class))).thenReturn(dtoSalida);

        mockMvc.perform(post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreRol").value("MANAGER"));
    }

    @Test
    void update_CuandoExiste_DeberiaDevolver200() throws Exception {
        RolDTO dtoEntrada = new RolDTO();
        dtoEntrada.setIdRol(1);
        dtoEntrada.setNombreRol("ADMIN_MODIFICADO");

        RolDTO dtoSalida = new RolDTO();
        dtoSalida.setNombreRol("ADMIN_MODIFICADO");

        when(rolService.update(any(RolDTO.class))).thenReturn(dtoSalida);

        mockMvc.perform(put("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreRol").value("ADMIN_MODIFICADO"));
    }

    @Test
    void update_CuandoNoExiste_DeberiaDevolver404() throws Exception {
        RolDTO dtoEntrada = new RolDTO();
        dtoEntrada.setIdRol(-1);

        when(rolService.update(any(RolDTO.class))).thenThrow(new NotFoundException("Rol no encontrado"));

        mockMvc.perform(put("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isNotFound());
    }

    @Test
    void desactivateRol_DeberiaDevolver200() throws Exception {
        RolDTO dtoSalida = new RolDTO();
        dtoSalida.setActivo(false);

        when(rolService.desactivateRol(1)).thenReturn(dtoSalida);

        mockMvc.perform(put("/roles/desactivar/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findUsuariosByRol_DeberiaDevolver200YLista() throws Exception {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNombreUsuario("Eduardo");

        when(rolService.findUsuariosByRol(1)).thenReturn(Arrays.asList(usuarioDTO));

        mockMvc.perform(get("/roles/1/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    
    @Test
    void findUsuariosByRol_CuandoRolNoExiste_DeberiaDevolver404() throws Exception {
        when(rolService.findUsuariosByRol(-1)).thenThrow(new NotFoundException("Rol no encontrado"));

        mockMvc.perform(get("/roles/-1/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}