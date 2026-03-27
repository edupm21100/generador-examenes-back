package com.eduardo.examen_backend.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import java.security.Principal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import com.eduardo.examen_backend.examenes.ExamenController;
import com.eduardo.examen_backend.examenes.ExamenDTO;
import com.eduardo.examen_backend.examenes.ExamenService;
import com.eduardo.examen_backend.shared.exceptions.NotFoundException;
import com.eduardo.examen_backend.shared.services.AuditoriaService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ExamenController.class)
class ExamenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExamenService examenService;

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
    @WithMockUser(roles = "PROFESOR")
    void cambiarEstado_CuandoEsDueño_DeberiaDevolver200() throws Exception {
        ExamenDTO dtoSalida = new ExamenDTO();
        dtoSalida.setIdExamen(1);
        dtoSalida.setActivo(true);

        Principal mockPrincipal = () -> "profesor@test.com";

        when(examenService.cambiarEstadoActivo(1, "profesor@test.com")).thenReturn(dtoSalida);

        mockMvc.perform(put("/examenes/1/estado")
                .principal(mockPrincipal)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    void cambiarEstado_CuandoNoExiste_DeberiaDevolver404() throws Exception {
        Principal mockPrincipal = () -> "profesor@test.com";

        when(examenService.cambiarEstadoActivo(anyInt(), eq("profesor@test.com")))
                .thenThrow(new NotFoundException("El examen no existe"));

        mockMvc.perform(put("/examenes/999/estado")
                .principal(mockPrincipal)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ALUMNO")
    void obtenerTodos_DeberiaDevolver200() throws Exception {
        ExamenDTO dto = new ExamenDTO();
        dto.setIdExamen(1);
        dto.setTitulo("Matemáticas");

        when(examenService.obtenerTodos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/examenes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Matemáticas"));
    }

    @Test
    @WithMockUser(roles = "ALUMNO")
    void obtenerExamen_DeberiaDevolver200() throws Exception {
        ExamenDTO dto = new ExamenDTO();
        dto.setIdExamen(1);
        dto.setTitulo("Física");

        when(examenService.obtenerPorId(1)).thenReturn(dto);

        mockMvc.perform(get("/examenes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Física"));
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    void crearExamen_DeberiaDevolver201() throws Exception {
        Principal mockPrincipal = () -> "profe@test.com";
        ExamenDTO dtoEntrada = new ExamenDTO();
        dtoEntrada.setTitulo("Nuevo Examen");

        ExamenDTO dtoSalida = new ExamenDTO();
        dtoSalida.setIdExamen(5);

        when(examenService.crearExamen(any(ExamenDTO.class), eq("profe@test.com"))).thenReturn(dtoSalida);

        mockMvc.perform(post("/examenes")
                .principal(mockPrincipal)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dtoEntrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idExamen").value(5));
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    void anhadirPreguntasEnLote_DeberiaDevolver200() throws Exception {
        Principal mockPrincipal = () -> "profe@test.com";
        List<Integer> ids = List.of(1, 2, 3);

        ExamenDTO dtoSalida = new ExamenDTO();
        dtoSalida.setIdExamen(1);

        when(examenService.anhadirPreguntas(eq(1), any(), eq("profe@test.com"))).thenReturn(dtoSalida);

        mockMvc.perform(put("/examenes/1/preguntas")
                .principal(mockPrincipal)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(ids)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    void quitarPreguntasEnLote_DeberiaDevolver200() throws Exception {
        Principal mockPrincipal = () -> "profe@test.com";
        List<Integer> ids = List.of(1, 2);

        ExamenDTO dtoSalida = new ExamenDTO();
        dtoSalida.setIdExamen(1);

        when(examenService.quitarPreguntas(eq(1), any(), eq("profe@test.com"))).thenReturn(dtoSalida);

        mockMvc.perform(delete("/examenes/1/preguntas")
                .principal(mockPrincipal)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(ids)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROFESOR", username = "profe@test.com")
    void descargarNotasExamen_DeberiaDevolverPdf() throws Exception {
        byte[] pdfFalso = "PDF Notas".getBytes();
        when(examenService.generarReporteNotasPdf(eq(1), anyString())).thenReturn(pdfFalso);

        Principal mockPrincipal = () -> "profe@test.com";

        mockMvc.perform(get("/examenes/1/intentos/reporte/pdf")
                .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
                .andExpect(content().bytes(pdfFalso));
    }
}