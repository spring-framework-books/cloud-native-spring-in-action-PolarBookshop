package com.polarbookshop.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/*
 * class to configure a WebClient bean with the base URL provided by ClientProperties
 */
@Configuration
public class ClientConfig {

    @Bean
    WebClient webClient(ClientProperties clientProperties, WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(clientProperties.catalogServiceUri().toString())
                .build();
    }

}