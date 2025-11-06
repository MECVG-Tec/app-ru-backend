package com.ru.facil.ru_facil.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;


@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Em dev estamos sem CSRF por simplicidade (mantém como estava)
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                // Rotas públicas (dev e docs)
                .requestMatchers(
                    "/health",
                    "/h2-console/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/auth/**",
                    "/register"
                        ).permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/menu/**").permitAll()
    .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/v1/menu/**").permitAll()
    .anyRequest().authenticated()
            )

            // Necessário para o console do H2 abrir em <iframe>
            .headers(h -> h.frameOptions(f -> f.disable()))

            // Mantém o formLogin existente
            .formLogin(form -> form
                .loginProcessingUrl("/auth/login")   // POST
                .defaultSuccessUrl("/clientes", true)
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout")
                .permitAll()
            )

            // HTTP Basic opcional (útil em dev para testar rápido se precisar)
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
