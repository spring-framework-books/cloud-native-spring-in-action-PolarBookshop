package com.polarbookshop.edgeservice.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.polarbookshop.edgeservice.config.SecurityConfig;

@WebFluxTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    WebTestClient webClient;

    // A mock bean to skip the interaction with Keycloak when retrieving information
    // about the Client registration
    @MockBean
    ReactiveClientRegistrationRepository clientRegistrationRepository;

    /*
     * Since we configured Edge Service to return an HTTP 401 response when a
     * request is
     * unauthenticated, let’s verify that happens when calling the /user endpoint
     * without
     * authenticating first:
     * 
     */
    @Test
    void whenNotAuthenticatedThen401() {
        webClient
                .get()
                .uri("/user")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    /*
     * To test the scenario where a user is authenticated, we can use
     * mockOidcLogin(), a
     * configuration object supplied by SecurityMockServerConfigurers to mock an
     * OIDC
     * login, synthesize an ID Token, and mutate the request context in
     * WebTestClient
     * accordingly.
     * 
     * The /user endpoint reads claims from the ID Token through the OidcUser
     * object,
     * so we need to build an ID Token with username, first name, and last name (the
     * roles
     * are hardcoded in the controller for now).
     */
    @Test
    void whenAuthenticatedThenReturnUser() {
        // The expected authenticated user
        var expectedUser = new User("jon.snow", "Jon", "Snow", List.of("employee", "customer"));

        webClient
                // Defines an authentication context based on OIDC and uses the expected user
                .mutateWith(configureMockOidcLogin(expectedUser))
                .get()
                .uri("/user")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(User.class)
                .value(user -> assertThat(user).isEqualTo(expectedUser));
    }

    private SecurityMockServerConfigurers.OidcLoginMutator configureMockOidcLogin(User expectedUser) {
        // Builds a mock ID Token
        return SecurityMockServerConfigurers.mockOidcLogin().idToken(builder -> {
            builder.claim(StandardClaimNames.PREFERRED_USERNAME, expectedUser.username());
            builder.claim(StandardClaimNames.GIVEN_NAME, expectedUser.firstName());
            builder.claim(StandardClaimNames.FAMILY_NAME, expectedUser.lastName());
            builder.claim("roles", expectedUser.roles());
        });
    }

}
