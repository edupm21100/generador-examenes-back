package com.eduardo.examen_backend.controllers;

import com.eduardo.examen_backend.auth.AuthController;
import com.eduardo.examen_backend.auth.LoginDTO;
import com.eduardo.examen_backend.security.JwtService;
import com.eduardo.examen_backend.shared.services.AuditoriaService;
import com.eduardo.examen_backend.usuarios.Usuario;
import com.eduardo.examen_backend.usuarios.UsuarioDTO;
import com.eduardo.examen_backend.usuarios.UsuarioRepository;
import com.eduardo.examen_backend.usuarios.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private AuditoriaService auditoriaService;

    @MockitoBean
    private com.eduardo.examen_backend.auth.CustomUserDetailService customUserDetailsService;

    @Test
    @WithMockUser
    void register_DeberiaDevolver201YUsuarioDTO() throws Exception {
        UsuarioDTO inputDto = new UsuarioDTO();
        inputDto.setCorreoUsuario("nuevo@test.com");
        inputDto.setNombreUsuario("Nuevo");
        inputDto.setContrasenhaUsuario("ValidPassword123!");
        inputDto.setApellidoUsuario("García");

        UsuarioDTO outputDto = new UsuarioDTO();
        outputDto.setIdUsuario(1);
        outputDto.setCorreoUsuario("nuevo@test.com");

        when(usuarioService.save(any(UsuarioDTO.class))).thenReturn(outputDto);
        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andDo(print())
                .andExpect(status().isCreated()) // 201 CREATED
                .andExpect(jsonPath("$.correoUsuario").value("nuevo@test.com"));
    }

    @Test
    void login_ConCredencialesValidas_DeberiaDevolver200YToken() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setCorreoUsuario("admin@test.com");
        loginDTO.setContrasenhaUsuario("MiPassword123");

        Usuario mockUsuario = new Usuario();
        mockUsuario.setCorreoUsuario("admin@test.com");
        mockUsuario.setContrasenhaUsuario("hashed_password_in_db");
        mockUsuario.setActivo(true);
        when(usuarioRepository.findByCorreoUsuario(anyString()))
                .thenReturn(Optional.of(mockUsuario));

        when(passwordEncoder.matches(any(CharSequence.class), anyString()))
                .thenReturn(true);

        when(jwtService.generateToken(anyString()))
                .thenReturn("un.token.falso.muy.largo");

        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("un.token.falso.muy.largo"));
    }

    @Test
    @WithMockUser
    void login_ConCorreoInexistente_DeberiaDevolver401() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setCorreoUsuario("fantasma@test.com");
        loginDTO.setContrasenhaUsuario("1234");

        when(usuarioRepository.findByCorreoUsuario("fantasma@test.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void login_ConContrasenaIncorrecta_DeberiaDevolver401() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setCorreoUsuario("admin@test.com");
        loginDTO.setContrasenhaUsuario("PasswordEquivocada");

        Usuario mockUsuario = new Usuario();
        mockUsuario.setCorreoUsuario("admin@test.com");
        mockUsuario.setContrasenhaUsuario("hashed_password_in_db");

        when(usuarioRepository.findByCorreoUsuario("admin@test.com")).thenReturn(Optional.of(mockUsuario));
        when(passwordEncoder.matches("PasswordEquivocada", "hashed_password_in_db")).thenReturn(false);

        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized());
    }
}