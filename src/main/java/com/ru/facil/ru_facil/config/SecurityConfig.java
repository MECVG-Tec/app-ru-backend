package com.ru.facil.ru_facil.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("1234"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Dev: sem CSRF e com CORS básico
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())

            .authorizeHttpRequests(auth -> auth
                // Público (docs/health/H2)
                .requestMatchers(
                    "/health",
                    "/h2-console/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/auth/**",
                    "/register"
                ).permitAll()

                // Pré-flight CORS
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Cardápio
                .requestMatchers(HttpMethod.GET,    "/api/v1/menu/**").permitAll()
                .requestMatchers(HttpMethod.PUT,    "/api/v1/menu/**").authenticated()
                .requestMatchers(HttpMethod.POST,   "/api/v1/menu/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/menu/**").authenticated()

                // Demais rotas: login
                .anyRequest().authenticated()
            )

            // H2 em iframe
            .headers(h -> h.frameOptions(f -> f.disable()))

            // Login padrão + Basic (útil p/ Swagger e scripts)
            .formLogin(form -> form
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/clientes", true)
                .permitAll()
            )
            .httpBasic(Customizer.withDefaults())

            .logout(logout -> logout
                .logoutUrl("/auth/logout")
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
