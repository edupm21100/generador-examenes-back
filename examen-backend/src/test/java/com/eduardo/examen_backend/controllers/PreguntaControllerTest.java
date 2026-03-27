package com.eduardo.examen_backend.controllers;


import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eduardo.examen_backend.examenes.opciones.OpcionDTO;
import com.eduardo.examen_backend.examenes.preguntas.PreguntaController;
import com.eduardo.examen_backend.examenes.preguntas.PreguntaDTO;
import com.eduardo.examen_backend.examenes.preguntas.PreguntaService;
import com.eduardo.examen_backend.shared.services.AuditoriaService;

@WebMvcTest(PreguntaController.class)
@Import(PreguntaControllerTest.MethodSecurityConfig.class)
class PreguntaControllerTest {

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityConfig {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PreguntaService preguntaService;

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
    void obtenerTodas_CuandoHayPreguntas_DeberiaDevolver200YLista() throws Exception {
        PreguntaDTO dto = new PreguntaDTO();
        dto.setIdPregunta(1);
        dto.setEnunciado("¿Qué es Java?");

        when(preguntaService.obtenerTodas()).thenReturn(Arrays.asList(dto));

        mockMvc.perform(get("/preguntas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].enunciado").value("¿Qué es Java?"));
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    void obtenerPorCategoria_CuandoEsValido_DeberiaDevolver200() throws Exception {
        PreguntaDTO dto = new PreguntaDTO();
        dto.setIdPregunta(2);
        dto.setIdCategoria(5);

        when(preguntaService.obtenerPorCategoria(5)).thenReturn(Arrays.asList(dto));

        mockMvc.perform(get("/preguntas")
                .param("idCategoria", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCategoria").value(5));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void cambiarEstado_ConRolAdmin_DeberiaDevolver200() throws Exception {
        PreguntaDTO dtoSalida = new PreguntaDTO();
        dtoSalida.setIdPregunta(1);
        dtoSalida.setActivo(true);

        when(preguntaService.cambiarEstadoActivo(1)).thenReturn(dtoSalida);

        mockMvc.perform(patch("/preguntas/1/estado")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    void cambiarEstado_ConRolProfesor_DeberiaDevolver403Forbidden() throws Exception {
        mockMvc.perform(patch("/preguntas/1/estado")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ALUMNO")
    void obtenerOpcionesAlumno_DeberiaDevolverOpcionesOcultandoLaSolucion() throws Exception {
        OpcionDTO opcion1 = OpcionDTO.builder()
                .idOpcion(1)
                .texto("Madrid")
                .esCorrecta(null)
                .build();
        
        OpcionDTO opcion2 = OpcionDTO.builder()
                .idOpcion(2)
                .texto("París")
                .esCorrecta(null)
                .build();

        when(preguntaService.obtenerOpcionesParaAlumno(1)).thenReturn(List.of(opcion1, opcion2));

        mockMvc.perform(get("/preguntas/1/opciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2)) // Debe haber 2 opciones
                .andExpect(jsonPath("$[0].idOpcion").value(1)) // Comprobamos que viaja el ID para que el frontend pueda responder
                .andExpect(jsonPath("$[0].texto").value("Madrid"))
                .andExpect(jsonPath("$[0].esCorrecta").isEmpty()); 
    }
}