package com.polarbookshop.orderservice.order.domain;

import com.polarbookshop.orderservice.config.DataConfig;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
//Identifies a test class that focuses on R2DBC components
@DataR2dbcTest
// Imports R2DBC configuration needed to enable auditing
@Import(DataConfig.class)
// Activates automatic startup and cleanup of test containers
@Testcontainers
class OrderRepositoryR2dbcTests {

    // Identifies a PostgreSQL container for testing
    @Container
    static PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14.12"));

    @Autowired
    private OrderRepository orderRepository;

    // Overwrites R2DBC and Flyway configuration to point to the test PostgreSQL instance
    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", OrderRepositoryR2dbcTests::r2dbcUrl);
        registry.add("spring.r2dbc.username", postgresql::getUsername);
        registry.add("spring.r2dbc.password", postgresql::getPassword);
        registry.add("spring.flyway.url", postgresql::getJdbcUrl);
    }

    // Builds an R2DBC connection string, because Testcontainers doesn’t provide one out of the box as it does for JDBC
    private static String r2dbcUrl() {
        return String.format("r2dbc:postgresql://%s:%s/%s", postgresql.getHost(),
                postgresql.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT), postgresql.getDatabaseName());
    }

    @Test
    void findOrderByIdWhenNotExisting() {
        StepVerifier.create(orderRepository.findById(394L))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void createRejectedOrder() {
        var rejectedOrder = OrderService.buildRejectedOrder("1234567890", 3);
        StepVerifier.create(orderRepository.save(rejectedOrder))
                .expectNextMatches(order -> order.status().equals(OrderStatus.REJECTED))
                .verifyComplete();
    }

}