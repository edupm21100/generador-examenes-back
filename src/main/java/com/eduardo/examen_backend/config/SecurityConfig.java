package com.eduardo.examen_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Bean
    SecurityFilterChain filterChain(HttpSecurity hhHttpSecurity){
        hhHttpSecurity.csrf(
            csrf -> csrf.disable()
        ).authorizeHttpRequests(
            auth -> auth.anyRequest().authenticated()
        ).httpBasic(Customizer.withDefaults());
        return hhHttpSecurity.build();
    }

/*     @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    } */


}
