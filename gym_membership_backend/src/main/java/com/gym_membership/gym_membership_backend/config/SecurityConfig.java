package com.gym_membership.gym_membership_backend.config;

import com.gym_membership.gym_membership_backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    // --- Password encoder folosit la UserAccount (register / login) ---
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // NU mai definim niciun UserDetailsService aici.
    // Folosim DbUserDetailsService (@Service) ca unic UserDetailsService.

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    // === CHAIN 1: /api/ai/** public, fără JwtAuthFilter ===
    @Bean
    @Order(1)
    public SecurityFilterChain aiChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(new AntPathRequestMatcher("/api/ai/**"))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().permitAll()
                );
        return http.build();
    }

    // === CHAIN 2: restul aplicației, cu JwtAuthFilter ===
    @Bean
    @Order(2)
    public SecurityFilterChain appChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // AI e public
                        .requestMatchers("/api/ai/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ==== CLIENT (read-only) ====
                        .requestMatchers(HttpMethod.GET, "/api/members/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/trainers/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/membership-types/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/custom/**").permitAll()

                        // login & register public
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/reset-password-simple").permitAll()

                        // ✅ membrii logați pot cumpăra abonament
                        .requestMatchers(HttpMethod.POST, "/api/members/assign-membership")
                        .authenticated()

                        // ==== ADMIN / STAFF protejat ====
                        .requestMatchers(HttpMethod.POST,   "/api/members/assign-trainer").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/members/**").authenticated()
                        .requestMatchers(HttpMethod.PUT,    "/api/trainers/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/trainers/**").authenticated()
                        .requestMatchers("/api/membership-types/**").authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // pentru dev, lăsăm orice origin (web, Quasar dev, emulator)
        cfg.setAllowedOriginPatterns(List.of("*"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
