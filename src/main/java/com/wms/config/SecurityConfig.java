package com.wms.config;

import com.wms.security.CustomAuthenticationSuccessHandler;
import com.wms.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // === AUTH & DOCS ===
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // === THYMELEAF & STATICS (VIEW'LER) ===
                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/products",
                                "/inventory",
                                "/locations",
                                "/orders",
                                "/purchase-orders",
                                "/admin",              // Admin Panel sayfası
                                "/favicon.ico",
                                "/css/**",
                                "/js/**"
                        ).permitAll()

                        // === REST API'LER (JWT + ROLE GEREKİR) ===

                        // PRODUCTS - WORKER sadece görebilir, ADMIN & MANAGER her şeyi yapabilir
                        .requestMatchers(HttpMethod.GET, "/api/products/**")
                        .hasAnyRole("ADMIN", "MANAGER", "WORKER")
                        .requestMatchers(HttpMethod.POST, "/api/products/**")
                        .hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**")
                        .hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        // WAREHOUSES - WORKER görebilir, ADMIN & MANAGER yönetir
                        .requestMatchers(HttpMethod.GET, "/api/warehouses/**")
                        .hasAnyRole("ADMIN", "MANAGER", "WORKER")
                        .requestMatchers(HttpMethod.POST, "/api/warehouses/**")
                        .hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/warehouses/**")
                        .hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/warehouses/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        // LOCATIONS - WORKER görebilir, ADMIN & MANAGER yönetir
                        .requestMatchers(HttpMethod.GET, "/api/locations/**")
                        .hasAnyRole("ADMIN", "MANAGER", "WORKER")
                        .requestMatchers(HttpMethod.POST, "/api/locations/**")
                        .hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/locations/**")
                        .hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/locations/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        // INVENTORY - WORKER görebilir, ADMIN & MANAGER yönetir
                        .requestMatchers(HttpMethod.GET, "/api/inventory/**")
                        .hasAnyRole("ADMIN", "MANAGER", "WORKER")
                        .requestMatchers(HttpMethod.POST, "/api/inventory/**")
                        .hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/inventory/**")
                        .hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/inventory/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        // STOCK - Herkes erişebilir
                        .requestMatchers("/api/stock/**")
                        .hasAnyRole("ADMIN", "MANAGER", "WORKER")

                        // PURCHASE ORDERS - Sadece ADMIN & MANAGER
                        .requestMatchers("/api/purchase-orders/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        // ORDERS - Herkes erişebilir
                        .requestMatchers("/api/orders/**")
                        .hasAnyRole("ADMIN", "MANAGER", "WORKER")

                        // SUPPLIERS - WORKER görebilir, ADMIN & MANAGER yönetir
                        .requestMatchers(HttpMethod.GET, "/api/suppliers/**")
                        .hasAnyRole("ADMIN", "MANAGER", "WORKER")
                        .requestMatchers(HttpMethod.POST, "/api/suppliers/**")
                        .hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/suppliers/**")
                        .hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/suppliers/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        // REPORTS - Sadece ADMIN & MANAGER
                        .requestMatchers("/api/reports/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        // ADMIN PANEL API - Sadece ADMIN
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // Diğer her şey için auth zorunlu
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // geliştirme için
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false); // "*" ile birlikte zorunlu olarak false

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}