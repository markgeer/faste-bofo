package com.idgs15.faste_back.config;

import com.idgs15.faste_back.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final JwtFilter jwtFilter;
    
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                
                // ✅ USUARIOS NORMALES: Solo lectura
                .requestMatchers(HttpMethod.GET, "/artistas/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/retos/**").hasAnyRole("USER", "ADMIN")
                
                // ✅ USUARIOS NORMALES: Ver reto del día y subir retos cumplidos
                .requestMatchers("/retos/diario").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/retos-cumplidos/**").hasAnyRole("USER", "ADMIN")
                
                // ❌ SOLO ADMIN: Crear, editar y eliminar artistas/retos
                .requestMatchers(HttpMethod.POST, "/artistas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/artistas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/artistas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/retos").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/retos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/retos/**").hasRole("ADMIN")
                
                // ✅ SOLO ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173", "https://faste-bofo-iota.vercel.app"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
    
    private UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173", "https://faste-bofo-iota.vercel.app"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}