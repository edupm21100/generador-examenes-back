package com.eduardo.examen_backend.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eduardo.examen_backend.examenes.categorias.CategoriaController;
import com.eduardo.examen_backend.examenes.categorias.CategoriaDTO;
import com.eduardo.examen_backend.examenes.categorias.CategoriaService;
import com.eduardo.examen_backend.shared.services.AuditoriaService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CategoriaController.class)
@Import(CategoriaControllerTest.MethodSecurityConfig.class) // Activamos seguridad en los métodos
class CategoriaControllerTest {

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityConfig {
    }

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CategoriaService categoriaService;

    // Dependencias del Filtro JWT de Seguridad
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
    void obtenerTodas_DeberiaDevolver200YLista() throws Exception {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setIdCategoria(1);
        dto.setNombre("Backend");

        when(categoriaService.obtenerTodas()).thenReturn(Arrays.asList(dto));

        mockMvc.perform(get("/categorias")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Backend"));
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    void crearCategoria_ConRolProfesor_DeberiaDevolver201() throws Exception {
        CategoriaDTO dtoEntrada = new CategoriaDTO();
        dtoEntrada.setNombre("DevOps");

        CategoriaDTO dtoSalida = new CategoriaDTO();
        dtoSalida.setIdCategoria(2);
        dtoSalida.setNombre("DevOps");

        when(categoriaService.crearCategoria(any(CategoriaDTO.class))).thenReturn(dtoSalida);

        mockMvc.perform(post("/categorias")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("DevOps"));
    }

    @Test
    @WithMockUser(roles = "ALUMNO") // El alumno no debería poder crear categorías
    void crearCategoria_ConRolAlumno_DeberiaDevolver403() throws Exception {
        CategoriaDTO dtoEntrada = new CategoriaDTO();
        dtoEntrada.setNombre("Hack");

        // Al usar @EnableMethodSecurity arriba, Spring Security bloqueará esto con un 403
        mockMvc.perform(post("/categorias")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isForbidden());
    }
}