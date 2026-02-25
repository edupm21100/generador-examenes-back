package com.eduardo.examen_backend.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsuarioConfig {

    @Bean(name = "usuarioModelMapper")
    public ModelMapper ModelMapper() {
        return new ModelMapper();
    }
}
