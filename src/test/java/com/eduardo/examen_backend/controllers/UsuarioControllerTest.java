package com.eduardo.examen_backend.controllers; // CORRECCIÓN 1

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eduardo.examen_backend.dto.UsuarioDTO;
import com.eduardo.examen_backend.services.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    void findAll_CuandoHayUsuarios_DeberiaDevolver200YLista() throws Exception {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setIdUsuario(1);
        usuarioDTO.setNombreUsuario("Eduardo");

        when(usuarioService.findAll()).thenReturn(Arrays.asList(usuarioDTO));

        mockMvc.perform(get("/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Esperamos HTTP 200
                .andExpect(jsonPath("$[0].nombreUsuario").value("Eduardo")); // Inspeccionamos el JSON
    }

    @Test
    void findAll_CuandoNoHayUsuarios_DeberiaDevolver204() throws Exception {
        when(usuarioService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Esperamos HTTP 204
    }

    @Test
    void save_DeberiaDevolver201YUsuarioCreado() throws Exception {
        UsuarioDTO dtoEntrada = new UsuarioDTO();
        dtoEntrada.setNombreUsuario("Laura");

        UsuarioDTO dtoSalida = new UsuarioDTO();
        dtoSalida.setNombreUsuario("Laura");

        when(usuarioService.save(any(UsuarioDTO.class))).thenReturn(dtoSalida);

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isCreated()) // Esperamos HTTP 201
                .andExpect(jsonPath("$.nombreUsuario").value("Laura"));
    }

    @Test
    void deleteById_CuandoExiste_DeberiaDevolver204() throws Exception {
        when(usuarioService.deleteById(1)).thenReturn(true);

        mockMvc.perform(delete("/usuarios/1"))
                .andExpect(status().isNoContent()); // Esperamos HTTP 204
    }

    @Test
    void deleteById_CuandoNoExiste_DeberiaDevolver404() throws Exception {
        when(usuarioService.deleteById(99)).thenReturn(false);

        mockMvc.perform(delete("/usuarios/99"))
                .andExpect(status().isNotFound()); // Esperamos HTTP 404
    }

    @Test
    void cambiarContrasenha_DeberiaRecibirParametrosYDevolver200() throws Exception {
        Integer idUsuario = 5;
        String nueva = "1234";
        String vieja = "0000";

        UsuarioDTO dtoSalida = new UsuarioDTO();
        dtoSalida.setIdUsuario(idUsuario);

        when(usuarioService.changeContrasenha(idUsuario, nueva, vieja)).thenReturn(dtoSalida);

        mockMvc.perform(put("/usuarios/{idUsuario}/contrasenha", idUsuario)
                .param("contrasenhaNueva", nueva)
                .param("contrasenhaVieja", vieja)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}