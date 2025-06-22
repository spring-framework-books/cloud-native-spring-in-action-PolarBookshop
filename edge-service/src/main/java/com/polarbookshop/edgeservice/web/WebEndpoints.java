package com.polarbookshop.edgeservice.web;

import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class WebEndpoints {

	@Bean
	public RouterFunction<ServerResponse> routerFunction() {
       /*  For simplicity, the fallback for GET requests returns an empty string, whereas the fall-
        back for POST requests returns an HTTP 503 error. In a real scenario, you might want
        to adopt different fallback strategies depending on the context, including throwing a
        custom exception to be handled from the client or returning the last value saved in
        the cache for the original request. */
         
		return RouterFunctions.route()
                // Fallback response used to handle the GET endpoint
				.GET("/catalog-fallback", request ->
						ServerResponse.ok().body(Mono.just(""), String.class))
                // Fallback response used to handle the POST endpoint
				.POST("/catalog-fallback", request ->
						ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).build())
				.build();
	}
	
}