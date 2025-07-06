package com.polarbookshop.edgeservice.user;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    /*
     * For Spring Web MVC and WebFlux controllers, besides using ReactiveSecurity-
     * ContextHolder directly, we can use the annotations @CurrentSecurityContext
     * and
     * 
     * @AuthenticationPrincipal to inject the SecurityContext and the principal (in
     * this
     * case, OidcUser) respectively.
     * 
     */
    // Injects an OidcUser object containing info about the currently authenticated
    // user
    @GetMapping("user")
    public Mono<User> getUser(@AuthenticationPrincipal OidcUser oidcUser) {
        log.info("Fetching information about the currently authenticated user");
        var user = new User(
                oidcUser.getPreferredUsername(),
                oidcUser.getGivenName(),
                oidcUser.getFamilyName(),
                // Gets the “roles” claim and extracts it as a list of strings
                 oidcUser.getClaimAsStringList("roles"));
        return Mono.just(user);
    }

    @GetMapping("user1")
    public Mono<User> getUser() {
        // Gets SecurityContext for the currently authenticated user from
        // ReactiveSecurityContextHolder
        return ReactiveSecurityContextHolder.getContext()
                // Gets Authentication from SecurityContext
                .map(SecurityContext::getAuthentication)
                // Gets the principal from cAuthentication. For OIDC, it’s of type OidcUser.
                .map(authentication -> (OidcUser) authentication.getPrincipal())
                // Builds a User object using data from OidcUser (extracted from the ID Token)
                .map(oidcUser -> new User(
                        oidcUser.getPreferredUsername(),
                        oidcUser.getGivenName(),
                        oidcUser.getFamilyName(),
                        oidcUser.getClaimAsStringList("roles")));
    }

    @GetMapping("user2")
    public Mono<User> getUser2(ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .cast(OAuth2AuthenticationToken.class)
                .map(authentication -> (OidcUser) authentication.getPrincipal())
                .map(oidcUser -> new User(
                        oidcUser.getPreferredUsername(),
                        oidcUser.getGivenName(),
                        oidcUser.getFamilyName(),
                        oidcUser.getClaimAsStringList("roles")));
    }
}