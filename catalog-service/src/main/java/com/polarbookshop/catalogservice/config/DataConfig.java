package com.polarbookshop.catalogservice.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
/*
 * With Spring Data JDBC, you can enable auditing for all the persistent
 * entities
 * using the @EnableJdbcAuditing annotation on a configuration class.
 */
@EnableJdbcAuditing
public class DataConfig {
    // define an AuditorAware bean that should return the principal cthe currently
    // authenticated user.
    // Returns the currently authenticated user for auditing purposes
    @Bean
    AuditorAware<String> auditorAware() {
        return () -> Optional
                // Extracts the SecurityContext object for the currently authenticated user from
                // SecurityContextHolder
                .ofNullable(SecurityContextHolder.getContext())
                // Extracts the Authentication object for the currently authenticated user from
                // SecurityContext
                .map(SecurityContext::getAuthentication)
                // Handles the case where a user is not authenticated, but is manipulating data.
                // Since we protected all the endpoints, this case should never happen, butwe’ll
                // include it for completeness.
                .filter(Authentication::isAuthenticated)
                // Extracts the username for the currently authenticated user from the
                // Authentication object
                .map(Authentication::getName);
    }
}