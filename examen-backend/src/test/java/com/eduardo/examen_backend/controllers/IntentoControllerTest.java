package com.eduardo.examen_backend.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import com.eduardo.examen_backend.examenes.intentos.IntentoController;
import com.eduardo.examen_backend.examenes.intentos.IntentoDTO;
import com.eduardo.examen_backend.examenes.intentos.IntentoService;
import com.eduardo.examen_backend.shared.exceptions.BadRequestException;
import com.eduardo.examen_backend.shared.services.AuditoriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(IntentoController.class)
class IntentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private IntentoService intentoService;

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
    @WithMockUser(roles = "ALUMNO")
    void realizarExamen_CuandoTodoEsCorrecto_DeberiaDevolver201YNota() throws Exception {
        IntentoDTO dtoEntrada = new IntentoDTO();
        dtoEntrada.setIdExamen(100);

        IntentoDTO dtoSalida = new IntentoDTO();
        dtoSalida.setIdIntento(1);
        dtoSalida.setNota(8.5);

        Principal mockPrincipal = () -> "alumno@test.com";

        when(intentoService.realizarExamen(eq("alumno@test.com"), any(IntentoDTO.class))).thenReturn(dtoSalida);

        mockMvc.perform(post("/intentos")
                .principal(mockPrincipal)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nota").value(8.5));
    }

    @Test
    @WithMockUser(roles = "ALUMNO")
    void realizarExamen_CuandoHayFraude_DeberiaDevolver400() throws Exception {
        IntentoDTO dtoEntrada = new IntentoDTO();
        Principal mockPrincipal = () -> "tramposo@test.com";

        when(intentoService.realizarExamen(eq("tramposo@test.com"), any(IntentoDTO.class)))
                .thenThrow(new BadRequestException("Intento de fraude detectado"));

        mockMvc.perform(post("/intentos")
                .principal(mockPrincipal)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ALUMNO", username = "alumno@test.com")
    void descargarReporteIntento_DeberiaDevolverPdf() throws Exception {
        byte[] pdfFalso = "PDF Intento".getBytes();
        when(intentoService.generarReporteIntentoPdf(eq(1), anyString())).thenReturn(pdfFalso);

        Principal mockPrincipal = () -> "alumno@test.com";

        mockMvc.perform(get("/intentos/1/reporte/pdf")
                .principal(mockPrincipal))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
                .andExpect(content().bytes(pdfFalso));
    }
}