package com.eduardo.examen_backend.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eduardo.examen_backend.dto.RolDTO;
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
    void findAll_CuandoNoHayRoles_DeberiaDevolver204() throws Exception {
        when(rolService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/roles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void findById_CuandoExiste_DeberiaDevolver200() throws Exception {
        RolDTO rolDTO = new RolDTO();
        rolDTO.setNombreRol("USER");

        when(rolService.findById(1)).thenReturn(Optional.of(rolDTO));

        mockMvc.perform(get("/roles/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreRol").value("USER"));
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

        when(rolService.update(any(RolDTO.class))).thenReturn(Optional.of(dtoSalida));

        mockMvc.perform(put("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreRol").value("ADMIN_MODIFICADO"));
    }

    @Test
    void deleteById_CuandoExiste_DeberiaDevolver204() throws Exception {
        when(rolService.deleteById(1)).thenReturn(true);

        mockMvc.perform(delete("/roles/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteById_CuandoNoExiste_DeberiaDevolver404() throws Exception {
        when(rolService.deleteById(99)).thenReturn(false);

        mockMvc.perform(delete("/roles/99"))
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
}