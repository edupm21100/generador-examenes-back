package com.eduardo.examen_backend.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.security.Principal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.eduardo.examen_backend.exceptions.NotFoundException;
import com.eduardo.examen_backend.exceptions.BadRequestException;
import com.eduardo.examen_backend.dto.PasswordDTO;
import com.eduardo.examen_backend.dto.RolDTO;
import com.eduardo.examen_backend.dto.UsuarioDTO;
import com.eduardo.examen_backend.dto.UsuarioRolDTO;
import com.eduardo.examen_backend.services.AuditoriaService;
import com.eduardo.examen_backend.services.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private com.eduardo.examen_backend.repositories.IncidenciaRepository incidenciaRepository;

    @MockitoBean
    private com.eduardo.examen_backend.repositories.UsuarioRepository usuarioRepository;

    @MockitoBean
    private com.eduardo.examen_backend.security.JwtService jwtService;

    @MockitoBean
    private com.eduardo.examen_backend.security.CustomUserDetailService customUserDetailsService;

    @MockitoBean
    private AuditoriaService auditoriaService;

    @Test
    @WithMockUser(roles = "ADMIN")
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
    @WithMockUser(roles = "ADMIN")
    void findAll_CuandoNoHayUsuarios_DeberiaDevolver200YListaVacia() throws Exception {
        when(usuarioService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
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
    @WithMockUser(roles = "ADMIN")
    void findById_CuandoNoExiste_DeberiaDevolver404() throws Exception {
        when(usuarioService.findById(99)).thenThrow(new NotFoundException("Usuario no encontrado"));

        mockMvc.perform(get("/usuarios/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void save_DeberiaDevolver201YUsuarioCreado() throws Exception {
        UsuarioDTO dtoEntrada = new UsuarioDTO();
        dtoEntrada.setNombreUsuario("Laura");
        // Añadimos datos válidos para que pase el @Valid
        dtoEntrada.setApellidoUsuario("García");
        dtoEntrada.setCorreoUsuario("laura@test.com");
        dtoEntrada.setContrasenhaUsuario("12345678");
        UsuarioDTO dtoSalida = new UsuarioDTO();
        dtoSalida.setNombreUsuario("Laura");

        when(usuarioService.save(any(UsuarioDTO.class))).thenReturn(dtoSalida);

        mockMvc.perform(post("/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreUsuario").value("Laura"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_DeberiaDevolver200() throws Exception {
        UsuarioDTO dtoEntrada = new UsuarioDTO();
        dtoEntrada.setIdUsuario(1);
        dtoEntrada.setNombreUsuario("Modificado");
        dtoEntrada.setApellidoUsuario("Perez");
        dtoEntrada.setCorreoUsuario("modificado@test.com");
        dtoEntrada.setContrasenhaUsuario("12345678");

        when(usuarioService.update(any(UsuarioDTO.class))).thenReturn(dtoEntrada);

        mockMvc.perform(put("/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUsuario").value("Modificado"));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void cambiarContrasenha_DeberiaRecibirJSONYDevolver200() throws Exception {
        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setNewPassword("12345678");
        passwordDTO.setOldPassword("00000000");

        UsuarioDTO dtoSalida = new UsuarioDTO();
        dtoSalida.setIdUsuario(5);

        when(usuarioService.changeContrasenha(eq("test@test.com"), any(PasswordDTO.class))).thenReturn(dtoSalida);

        Principal mockPrincipal = () -> "test@test.com";

        mockMvc.perform(put("/usuarios/me/password")
                .principal(mockPrincipal)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordDTO)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void cambiarContrasenha_ConContrasenhaViejaIncorrecta_DeberiaDevolver400() throws Exception {
        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setNewPassword("123456");
        passwordDTO.setOldPassword("mal_password");

        when(usuarioService.changeContrasenha(eq("test@test.com"), any(PasswordDTO.class)))
                .thenThrow(new BadRequestException("Contraseña inválida"));

        mockMvc.perform(put("/usuarios/me/password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void desactivateUser_DeberiaDevolver200() throws Exception {
        UsuarioDTO dtoSalida = new UsuarioDTO();
        dtoSalida.setActivo(false);

        when(usuarioService.desactivateUser(1)).thenReturn(dtoSalida);

        mockMvc.perform(put("/usuarios/desactivar/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void anhadirRol_DeberiaDevolver200() throws Exception {
        UsuarioDTO dtoSalida = new UsuarioDTO();
        dtoSalida.setIdUsuario(1);

        when(usuarioService.anhadirRol(1, 2)).thenReturn(dtoSalida);

        mockMvc.perform(put("/usuarios/1/roles")
                .with(csrf())
                .param("idRol", "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeRol_DeberiaDevolver200() throws Exception {
        UsuarioRolDTO dtoSalida = new UsuarioRolDTO();
        dtoSalida.setIdUsuario(1);
        dtoSalida.setIdRol(2);

        when(usuarioService.removeRol(1, 2)).thenReturn(dtoSalida);

        mockMvc.perform(put("/usuarios/1/roles/2")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
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