package com.polarbookshop.edgeservice.config;

import reactor.core.publisher.Mono;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * The RequestRateLimiter filter relies on a KeyResolver bean to determine which
bucket to use for each request. By default, it uses the currently authenticated user in
Spring Security. Until we add security to Edge Service, we’ll define a custom Key-
Resolver bean and make it return a constant value (for example, anonymous) so that
all requests will be mapped to the same bucket.
 */
@Configuration
public class RateLimiterConfig {
    @Bean
    public KeyResolver keyResolver() {
        return exchange -> Mono.just("anonymous");
    }
}
