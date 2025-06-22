package com.polarbookshop.dispatcherservice;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DispatchingFunctions {

	private static final Logger log = LoggerFactory.getLogger(DispatchingFunctions.class);

	@Bean
	public Function<OrderAcceptedMessage, Long> pack() {
		return orderAcceptedMessage -> {
			log.info("The order with id {} is packed.", orderAcceptedMessage.orderId());
			return orderAcceptedMessage.orderId();
		};
	}

	/*
	 * Spring Cloud Function supports both imperative and reactive code, so you’re
	 * free to
	 * implement functions using reactive APIs like Mono and Flux. You can also mix
	 * and
	 * match. For the sake of the example, let’s implement the label function using
	 * Project
	 * Reactor.
	 */
	/*
	 * The input of the function will be the identifier of an order that has been
	 * packed, represented as a Long object. The output of the function will be the
	 * identifier of
	 * the order that has been labeled, resulting in the dispatching process being
	 * complete.
	 * 
	 */
	@Bean
	public Function<Flux<Long>, Flux<OrderDispatchedMessage>> label() {
		return orderFlux -> orderFlux.map(orderId -> {
			log.info("The order with id {} is labeled.", orderId);
			return new OrderDispatchedMessage(orderId);
		});
	}

}