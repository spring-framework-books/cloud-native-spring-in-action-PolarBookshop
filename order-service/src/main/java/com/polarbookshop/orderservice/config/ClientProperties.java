package com.polarbookshop.orderservice.config;

import java.net.URI;

import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;

/*
 *  define your custom polar.catalog-service-uri property to configure the URI for calling the Catalog Service.
 */
@ConfigurationProperties(prefix = "polar")
public record ClientProperties(

        @NotNull URI catalogServiceUri

) {
}