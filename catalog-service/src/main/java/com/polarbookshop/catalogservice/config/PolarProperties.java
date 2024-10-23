package com.polarbookshop.catalogservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// a PolarProperties class annotated with @ConfigurationProperties to mark it as a holder of configuration data.
//Marks the class as a source for configuration properties starting with the prefix “polar”
@ConfigurationProperties(prefix = "polar")
public class PolarProperties {

    /**
     * A message to welcome users.
     */
    // Field for the custom polar.greeting (prefix + field name) property, parsed as String
    private String greeting;

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

}