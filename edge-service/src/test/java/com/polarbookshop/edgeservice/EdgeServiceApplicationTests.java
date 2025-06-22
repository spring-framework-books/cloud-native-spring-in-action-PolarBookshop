package com.polarbookshop.edgeservice;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/*
 * verify that the Spring context loads correctly when Redis is
used for storing web session–related data
 */
// Loads a full Spring web application context and a web environment listening on a random port
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// Activates automatic startup and cleanup of test containers
@Testcontainers
class EdgeServiceApplicationTests {

	private static final int REDIS_PORT = 6379;
	// Defines a Redis container for testing
	@Container
	static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2"))
			.withExposedPorts(REDIS_PORT);

	// Overwrites the Redis configuration to point to the test Redis instance
	@DynamicPropertySource
	static void redisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.redis.host", () -> redis.getHost());
		registry.add("spring.data.redis.port", () -> redis.getMappedPort(REDIS_PORT));
	}

	// An empty test used to verify that the application context is loaded correctly
	// and that a connection with Redis has been established successfully
	@Test
	void verifyThatSpringContextLoads() {
	}

}