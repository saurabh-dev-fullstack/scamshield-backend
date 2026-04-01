package com.scamshield.backend.config;

import com.scamshield.backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // ✅ CORS + CSRF
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                // ✅ JWT = STATELESS
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        /* ================= PUBLIC APIs ================= */

                        // Static uploads
                        .requestMatchers("/uploads/**").permitAll()

                        // Scam APIs
                        .requestMatchers(
                                "/api/scams",
                                "/api/scams/*",          // 🔥 THIS FIXES /api/scams/{id}
                                "/api/scams/*/proofs",
                                "/api/scams/feed",
                                "/api/scams/search",
                                "/api/scams/category/**",
                                "/api/scams/state",
                                "/api/scams/city",
                                "/api/scams/trending"
                        ).permitAll()


                        // 📘 PUBLIC ARTICLES (VERY IMPORTANT FIX)
                        .requestMatchers(
                                "/api/articles/category/**",
                                "/api/articles/*"              // 🔥 ALLOW SLUG PAGE
                        ).permitAll()

                        /* ================= AUTH ================= */
                        .requestMatchers("/api/auth/**").permitAll()

                        /* ================= ADMIN ================= */
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Admin article creation
                        .requestMatchers("/api/admin/articles/**").hasRole("ADMIN")

                        /* ================= EVERYTHING ELSE ================= */
                        .anyRequest().authenticated()
                );

        // ✅ JWT FILTER
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /* ===================== CORS (LOCALHOST ONLY) ===================== */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of(
                "http://localhost:3000"
        ));
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
