package com.polarbookshop.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

// Indicates a class as a source of Spring configuration
@Configuration
// Enables R2DBC auditing for persistent entities
@EnableR2dbcAuditing
public class DataConfig {
    /*
     * DEFINING AN AUDITOR TO CAPTURE WHO CREATED OR UPDATED AN R2DBC DATA ENTITY
     * Even in this case, we need to tell Spring Data where to get the information
     * about the
     * currently authenticated user. Since it’s a reactive application, this time
     * we’ll get the
     * SecurityContext object for the principal from the
     * ReactiveSecurityContextHolder.
     * 
     */
    // Returns the currently authenticated user for auditing purposes
    @Bean
    ReactiveAuditorAware<String> auditorAware() {
        // Extracts the SecurityContext object for the currently authenticated user from
        // ReactiveSecurityContextHolder
        return () -> ReactiveSecurityContextHolder.getContext()
                // Extracts the Authentication object for the currently authenticated user from
                // SecurityContext
                .map(SecurityContext::getAuthentication)
                // Handles the case where a user is not authenticated, but it is manipulating
                // data. Since we protected all the endpoints, this case should never happen,
                // but we’ll include it for completeness.
                .filter(Authentication::isAuthenticated)
                // Extracts the username of the currently authenticated user from the
                // Authentication object
                .map(Authentication::getName);
    }
}