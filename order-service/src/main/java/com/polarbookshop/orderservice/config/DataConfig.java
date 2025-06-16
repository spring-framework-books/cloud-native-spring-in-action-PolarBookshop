package com.polarbookshop.orderservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

// Indicates a class as a source of Spring configuration
@Configuration
// Enables R2DBC auditing for persistent entities
@EnableR2dbcAuditing
public class DataConfig {
}