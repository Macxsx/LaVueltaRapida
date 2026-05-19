package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired private JwtAuthFilter jwtAuthFilter;
    @Autowired private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(h -> h.frameOptions(fo -> fo.sameOrigin()))
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth

                // ── Público ─────────────────────────────────────────────────
                .requestMatchers(HttpMethod.GET, "/auth/me").authenticated()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/h2/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/comidas/**", "/categorias/**", "/adicionales/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/clientes").permitAll()

                // ── Perfil propio ────────────────────────────────────────────
                .requestMatchers(HttpMethod.GET, "/clientes/me").hasAnyRole("ADMIN", "CLIENTE")
                .requestMatchers(HttpMethod.GET, "/operadores/me").hasAnyRole("ADMIN", "OPERADOR")
                .requestMatchers(HttpMethod.GET, "/administradores/me").hasRole("ADMIN")

                // ── Solo ADMIN ───────────────────────────────────────────────
                .requestMatchers("/administradores/**", "/operadores/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/comidas/**", "/categorias/**", "/adicionales/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/comidas/**", "/categorias/**", "/adicionales/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/comidas/**", "/categorias/**", "/adicionales/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/comidas/**", "/categorias/**", "/adicionales/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/domiciliarios", "/domiciliarios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/domiciliarios", "/domiciliarios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/clientes", "/clientes/**").hasRole("ADMIN")

                // ── ADMIN + OPERADOR ─────────────────────────────────────────
                .requestMatchers("/domiciliarios/**").hasAnyRole("ADMIN", "OPERADOR")
                .requestMatchers(HttpMethod.PATCH, "/pedido/**").hasAnyRole("ADMIN", "OPERADOR", "CLIENTE")
                .requestMatchers(HttpMethod.GET, "/clientes", "/clientes/**").hasAnyRole("ADMIN", "OPERADOR")

                // ── CLIENTE (su propio perfil y operaciones) ─────────────────
                .requestMatchers(HttpMethod.PUT, "/clientes/**").hasAnyRole("ADMIN", "CLIENTE")
                .requestMatchers("/carrito/**", "/mercadopago/**").hasAnyRole("ADMIN", "CLIENTE")

                // ── Cualquier autenticado ────────────────────────────────────
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
