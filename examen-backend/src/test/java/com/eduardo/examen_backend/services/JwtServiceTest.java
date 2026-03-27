package com.eduardo.examen_backend.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.eduardo.examen_backend.security.JwtService;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

class JwtServiceTest {

    private JwtService jwtService;
    
    private final String SECRET_FALSO = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_FALSO);
    }

    @Test
    void generateToken_DeberiaDevolverUnTokenValido() {
        String correo = "admin@test.com";

        String token = jwtService.generateToken(correo);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(3, token.split("\\.").length); 
    }

    @Test
    void extractUsername_ConTokenValido_DeberiaDevolverElCorreoOriginal() {
        String correo = "usuario@test.com";
        String tokenGenerado = jwtService.generateToken(correo);

        String correoExtraido = jwtService.extractUsername(tokenGenerado);

        assertEquals(correo, correoExtraido);
    }

    @Test
    void extractUsername_ConTokenManipulado_DeberiaLanzarExcepcion() {
        String correo = "admin@test.com";
        String tokenGenerado = jwtService.generateToken(correo);
        
        String tokenRoto = tokenGenerado.substring(0, tokenGenerado.length() - 5) + "xxxxx";

        assertThrows(SignatureException.class, () -> {
            jwtService.extractUsername(tokenRoto);
        });
    }

    @Test
    void extractUsername_ConFormatoInvalido_DeberiaLanzarExcepcion() {
        String tokenBasura = "esto.noes.untokenvalido";

        assertThrows(MalformedJwtException.class, () -> {
            jwtService.extractUsername(tokenBasura);
        });
    }
}