package com.eduardo.examen_backend.controllers;

import com.eduardo.examen_backend.incidencias.IncidenciaController;
import com.eduardo.examen_backend.incidencias.IncidenciaDTO;
import com.eduardo.examen_backend.incidencias.IncidenciaService;
import com.eduardo.examen_backend.shared.exceptions.NotFoundException;
import com.eduardo.examen_backend.shared.services.AuditoriaService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;



import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IncidenciaController.class)
@Import(IncidenciaControllerTest.MethodSecurityConfig.class)
class IncidenciaControllerTest {

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityConfig {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IncidenciaService incidenciaService;

    @MockitoBean
    private AuditoriaService auditoriaService;

    @MockitoBean
    private com.eduardo.examen_backend.security.JwtService jwtService;

    @MockitoBean
    private com.eduardo.examen_backend.auth.CustomUserDetailService customUserDetailsService;

    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void findAll_ComoAdmin_DeberiaDevolver200() throws Exception {
        IncidenciaDTO dto = new IncidenciaDTO();
        dto.setIdIncidencia(1);
        
        when(incidenciaService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/incidencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].idIncidencia").value(1));
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = {"USER"})
    void findAll_ComoUsuarioNormal_DeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/incidencias"))
                .andExpect(status().isForbidden()); // 403 Access Denied
    }


    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void findById_ComoAdmin_CuandoExiste_DeberiaDevolver200() throws Exception {
        IncidenciaDTO dto = new IncidenciaDTO();
        dto.setIdIncidencia(5);

        when(incidenciaService.findById(5)).thenReturn(dto);

        mockMvc.perform(get("/incidencias/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idIncidencia").value(5));
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void findById_ComoAdmin_CuandoNoExiste_DeberiaDevolver404() throws Exception {
        when(incidenciaService.findById(99)).thenThrow(new NotFoundException("No existe"));

        mockMvc.perform(get("/incidencias/99"))
                .andExpect(status().isNotFound()); // 404 Not Found
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = {"USER"})
    void findById_ComoUsuarioNormal_DeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/incidencias/5"))
                .andExpect(status().isForbidden()); // 403 Access Denied
    }
}