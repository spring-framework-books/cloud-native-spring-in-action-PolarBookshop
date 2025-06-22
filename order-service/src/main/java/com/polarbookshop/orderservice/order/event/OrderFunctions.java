package com.polarbookshop.orderservice.order.event;

import java.util.function.Consumer;

import com.polarbookshop.orderservice.order.domain.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderFunctions {

    private static final Logger log = LoggerFactory.getLogger(OrderFunctions.class);

    /*
     * The function will be a Consumer responsible
     * for listening to the incoming messages and updating the database entities
     * accordingly.
     * Consumer objects are functions with input but no output. To keep the function
     * clean
     * and readable, we’ll move the processing of OrderDispatchedMessage objects to
     * the
     * OrderService class
     * 
     */
    /*
     * Consumers are triggered by a message arriving in the queue. RabbitMQ provides
     * an
     * at-least-one delivered guarantee, so you need to be aware of possible
     * duplicates. The code we implemented updates the status of the specific order
     * to be DISPATCHED, an
     * operation that can be executed several times with the same result. Since the
     * operation
     * is idempotent, the code is resilient to duplicates. A further optimization
     * would be to
     * check for the status and skip the update operation if it’s already
     * dispatched.
     */
    @Bean
    public Consumer<Flux<OrderDispatchedMessage>> dispatchOrder(OrderService orderService) {
        /*
         * Order Service is a reactive application, so the dispatchOrder function will
         * consume
         * messages as a reactive stream (a Flux of OrderDispatchedMessage). Reactive
         * streams
         * are activated only if there’s a subscriber interested in receiving the data.
         * For that rea-
         * son, it’s critical that we end the reactive stream by subscribing to it, or
         * else no data will
         * ever be processed
         */
        // For each dispatched message, it updates the related order in the database.
        return flux -> orderService.consumeOrderDispatchedEvent(flux)
                // For each order updated in the database, it logs a message.
                .doOnNext(order -> log.info("The order with id {} is dispatched", order.id()))
                // Subscribes to the reactive stream in order to activate it. Without a
                // subscriber, no data flows through the stream.
                .subscribe();
    }

}