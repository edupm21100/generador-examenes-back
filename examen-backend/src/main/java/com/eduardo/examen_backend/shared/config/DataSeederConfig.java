package com.eduardo.examen_backend.shared.config;

import com.eduardo.examen_backend.roles.Rol;
import com.eduardo.examen_backend.roles.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeederConfig {

    @Bean
    CommandLineRunner initDatabase(RolRepository rolRepository) {
        return args -> {
            if (rolRepository.count() == 0) {
                rolRepository.save(Rol.builder().nombreRol("ADMIN").activo(true).build());
                rolRepository.save(Rol.builder().nombreRol("PROFESOR").activo(true).build());
                rolRepository.save(Rol.builder().nombreRol("ALUMNO").activo(true).build());
            }
        };
    }
}