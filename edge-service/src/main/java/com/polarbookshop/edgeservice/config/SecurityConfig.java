package com.polarbookshop.edgeservice.config;

import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.security.web.server.csrf.XorServerCsrfTokenRequestAttributeHandler;
import org.springframework.web.server.WebFilter;

import com.nimbusds.oauth2.sdk.token.Tokens;

@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
            ReactiveClientRegistrationRepository clientRegistrationRepository) {
        return http
                .authorizeExchange(exchange -> exchange
                        // Allows unauthenticated access to any Spring Boot Actuator endpoint
                        .pathMatchers("/actuator/**").permitAll()
                        // Allows unauthenticated access to the SPA static resources
                        .pathMatchers("/", "/*.css", "/*.js", "/favicon.ico").permitAll()
                        // Allows unauthenticated read access to the books in the catalog
                        .pathMatchers(HttpMethod.GET, "/books/**").permitAll()
                        // Any other request requires user authentication.
                        .anyExchange().authenticated())
                // When an exception is thrown because a user is not authenticated, it replies
                // with an HTTP 401 response.
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)))
                .oauth2Login(Customizer.withDefaults())
                // Defines a custom handler for the scenario where a logout operation is
                // completed successfully
                .logout(logout -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository)))
                // Uses a cookie-based strategy for exchanging CSRF tokens with the Angular
                // frontend
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new XorServerCsrfTokenRequestAttributeHandler()::handle))
                .build();
    }

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(
            ReactiveClientRegistrationRepository clientRegistrationRepository) {
        // After logging out from the OIDC Provider, Keycloak will redirect the user to
        // the application base URL computed dynamically from Spring (locally, it’s
        // http:/ /localhost:9000).
        var oidcLogoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
        return oidcLogoutSuccessHandler;
    }

    /*
     * A filter with the only purpose of subscribing
     * to the CsrfToken reactive stream and
     * ensuring its value is extracted correctly
     */
    @Bean
    WebFilter csrfWebFilter() {
        // Required because of
        // https://github.com/spring-projects/spring-security/issues/5766
        return (exchange, chain) -> {
            exchange.getResponse().beforeCommit(() -> Mono.defer(() -> {
                Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());
                return csrfToken != null ? csrfToken.then() : Mono.empty();
            }));
            return chain.filter(exchange);
        };
    }

    /*
     * By default, Spring Security stores the Access Tokens for the currently
     * authenticated
     * users in memory. When you have multiple instances of Edge Service running
     * (which is
     * always true in a cloud production environment to ensure high availability),
     * you will
     * encounter issues due to the statefulness of the application. Cloud native
     * applications
     * should be stateless. Let’s fix that.
     * Spring Security stores Access Tokens in an OAuth2AuthorizedClient object that
     * is
     * accessible through a ServerOAuth2AuthorizedClientRepository bean. The default
     * implementation for that repository adopts an in-memory strategy for
     * persistence.
     * That’s what makes Edge Service a stateful application. How can we keep it
     * stateless
     * and scalable?
     * A simple way to do that is to store OAuth2AuthorizedClient objects in the web
     * ses-
     * sion rather than in memory so that Spring Session will pick them up
     * automatically and
     * save them in Redis, just like it does with ID Tokens. Fortunately, the
     * framework already
     * provides an implementation of the ServerOAuth2AuthorizedClientRepository
     * inter-
     * face to save data in the web session: WebSessionServerOAuth2AuthorizedClient-
     * Repository.
     */
    // Defines a repository to store Access Tokens in the web session
    @Bean
    ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }
}