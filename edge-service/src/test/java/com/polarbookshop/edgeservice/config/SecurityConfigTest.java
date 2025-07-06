package com.polarbookshop.edgeservice.config;

import static org.mockito.Mockito.when;

import org.assertj.core.util.URLs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

@WebFluxTest
@Import(SecurityConfig.class)
public class SecurityConfigTest {
    @Autowired
    WebTestClient webClient;

    // A mock bean to skip the interaction with Keycloak when retrieving information
    // about the Client registration
    @MockBean
    ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Test
    void whenLogoutNotAuthenticatedAndNoCsrfTokenThen403() {
        webClient
                .post()
                .uri("/logout")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenLogoutAuthenticatedAndNoCsrfTokenThen403() {
        webClient
                .mutateWith(SecurityMockServerConfigurers.mockOidcLogin())
                .post()
                .uri("/logout")
                .exchange()
                .expectStatus().isForbidden();
    }
    /*
     * check whether the application returns an HTTP 302 response
     * after sending an HTTP POST request to the /logout with the correct OIDC login
     * and
     * CSRF context.
     * 
     */
    @Test
    void whenLogoutAuthenticatedAndWithCsrfTokenThen302() {
        when(clientRegistrationRepository.findByRegistrationId("test"))
                .thenReturn(Mono.just(testClientRegistration()));

        webClient
                // Uses a mock ID Token to authenticate the user
                .mutateWith(SecurityMockServerConfigurers.mockOidcLogin())
                // Enhances the request to provide the required CSRF token
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri("/logout")
                .exchange()
                // The response is a redirect to Keycloak to propagate the logout operation.
                .expectStatus().isFound();
    }
    // A mock ClientRegistration used by Spring Security to get the URLs to contact
    // Keycloak
    private ClientRegistration testClientRegistration() {
        return ClientRegistration.withRegistrationId("test")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientId("test")
                .authorizationUri("https://sso.polarbookshop.com/auth")
                .tokenUri("https://sso.polarbookshop.com/token")
                .redirectUri("https://polarbookshop.com")
                .build();
    }
}
