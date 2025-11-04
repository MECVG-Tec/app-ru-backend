package com.ru.facil.ru_facil.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .csrf(csrf -> csrf.disable())


                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(

                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",

                                "/auth/**",
                                "/register"
                        ).permitAll()
                        .anyRequest().authenticated()
                )


                .formLogin(form -> form
                        .loginProcessingUrl("/auth/login") // Endpoint que processará o login (POST)
                        .defaultSuccessUrl("/clientes", true) // URL de redirecionamento após sucesso
                        .permitAll() // Permite que todos acessem a página de login
                )


                .logout(logout -> logout
                        .logoutUrl("/auth/logout") // Endpoint para logout
                        .logoutSuccessUrl("/auth/login?logout")
                        .permitAll()
                );

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


