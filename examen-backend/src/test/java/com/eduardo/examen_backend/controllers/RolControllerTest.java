package com.eduardo.examen_backend.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eduardo.examen_backend.roles.RolController;
import com.eduardo.examen_backend.roles.RolDTO;
import com.eduardo.examen_backend.roles.RolService;
import com.eduardo.examen_backend.shared.exceptions.NotFoundException;
import com.eduardo.examen_backend.shared.services.AuditoriaService;
import com.eduardo.examen_backend.usuarios.UsuarioDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(RolController.class)
class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RolService rolService;

    @MockitoBean
    private com.eduardo.examen_backend.incidencias.IncidenciaRepository incidenciaRepository;

    @MockitoBean
    private com.eduardo.examen_backend.usuarios.UsuarioRepository usuarioRepository;
    
    @MockitoBean
    private com.eduardo.examen_backend.security.JwtService jwtService;

    @MockitoBean
    private com.eduardo.examen_backend.auth.CustomUserDetailService customUserDetailsService;
    
    @MockitoBean
    private AuditoriaService auditoriaService;

    @Test
    @WithMockUser(roles = "ADMIN")
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
    @WithMockUser(roles = "ADMIN")
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
    @WithMockUser(roles = "ADMIN")
    void findById_CuandoNoExiste_DeberiaDevolver404() throws Exception {
        when(rolService.findById(99)).thenThrow(new NotFoundException("Rol no encontrado"));

        mockMvc.perform(get("/roles/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void save_DeberiaDevolver201YRolCreado() throws Exception {
        RolDTO dtoEntrada = new RolDTO();
        dtoEntrada.setNombreRol("MANAGER");

        RolDTO dtoSalida = new RolDTO();
        dtoSalida.setNombreRol("MANAGER");

        when(rolService.save(any(RolDTO.class))).thenReturn(dtoSalida);

        mockMvc.perform(post("/roles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreRol").value("MANAGER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_CuandoExiste_DeberiaDevolver200() throws Exception {
        RolDTO dtoEntrada = new RolDTO();
        dtoEntrada.setIdRol(1);
        dtoEntrada.setNombreRol("ADMIN_MODIFICADO");

        RolDTO dtoSalida = new RolDTO();
        dtoSalida.setNombreRol("ADMIN_MODIFICADO");

        when(rolService.update(any(RolDTO.class))).thenReturn(dtoSalida);

        mockMvc.perform(put("/roles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreRol").value("ADMIN_MODIFICADO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_CuandoNoExiste_DeberiaDevolver404() throws Exception {
        RolDTO dtoEntrada = new RolDTO();
        dtoEntrada.setIdRol(-1);

        when(rolService.update(any(RolDTO.class))).thenThrow(new NotFoundException("Rol no encontrado"));

        mockMvc.perform(put("/roles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void desactivateRol_DeberiaDevolver200() throws Exception {
        RolDTO dtoSalida = new RolDTO();
        dtoSalida.setActivo(false);

        when(rolService.desactivateRol(1)).thenReturn(dtoSalida);

        mockMvc.perform(put("/roles/desactivar/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findUsuariosByRol_DeberiaDevolver200YLista() throws Exception {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNombreUsuario("Eduardo");

        when(rolService.findUsuariosByRol(1)).thenReturn(Arrays.asList(usuarioDTO));

        mockMvc.perform(get("/roles/1/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void findUsuariosByRol_CuandoRolNoExiste_DeberiaDevolver404() throws Exception {
        when(rolService.findUsuariosByRol(-1)).thenThrow(new NotFoundException("Rol no encontrado"));

        mockMvc.perform(get("/roles/-1/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}