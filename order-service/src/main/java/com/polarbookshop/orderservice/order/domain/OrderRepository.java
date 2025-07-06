package com.polarbookshop.orderservice.order.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
    // Custom method to query only the orders created by the given user
    Flux<Order> findAllByCreatedBy(String userId);
}