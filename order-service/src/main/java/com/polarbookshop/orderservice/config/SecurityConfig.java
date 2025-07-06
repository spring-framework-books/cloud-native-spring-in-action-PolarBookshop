package com.polarbookshop.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;

@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
                // All requests require authentication
                .authorizeExchange(exchange -> exchange
                        // Allows unauthenticated access to any Spring Boot Actuator endpoint
                        .pathMatchers("/actuator/**").permitAll()
                        .anyExchange().authenticated())
                // Enables OAuth2 Resource Server support using the default configuration based
                // on JWT (JWT authentication)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                // Each request must include an Access Token, so there’s no need to keep a
                // session cache alive between requests. We want it to be stateless.
                .requestCache(requestCacheSpec -> requestCacheSpec.requestCache(NoOpServerRequestCache.getInstance()))
                // Since the authentication strategy is stateless and doesn’t involve a
                // browser-based client, we can safely disable the CSRF protection.
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

}