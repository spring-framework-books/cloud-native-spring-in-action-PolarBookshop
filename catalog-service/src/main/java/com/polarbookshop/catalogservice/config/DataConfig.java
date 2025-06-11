package com.polarbookshop.catalogservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;

@Configuration
/*
 * With Spring Data JDBC, you can enable auditing for all the persistent
 * entities
 * using the @EnableJdbcAuditing annotation on a configuration class.
 */
@EnableJdbcAuditing
public class DataConfig {
}