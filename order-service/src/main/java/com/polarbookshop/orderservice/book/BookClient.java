package com.polarbookshop.orderservice.book;

import java.time.Duration;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class BookClient {

    private static final String BOOKS_ROOT_API = "/books/";
    private final WebClient webClient;

    public BookClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Book> getBookByIsbn(String isbn) {
        /*
         * Currently, Catalog Ser-
         * vice exposes a /books/{bookIsbn} endpoint that returns all the available
         * information
         * about a book. In a real scenario, you might expose a different endpoint that
         * returns an
         * object containing only the required information (a DTO).
         */
        return webClient
                .get()
                .uri(BOOKS_ROOT_API + isbn)
                .retrieve()
                .bodyToMono(Book.class)
                /*
                 * Instead of throwing an exception when the timeout expires, you have the
                 * chance to pro-
                 * vide a fallback behavior. Considering that Order Service can’t accept an
                 * order if the
                 * book’s availability is not verified, you might consider returning an empty
                 * result so that
                 * the order will be rejected. You can define a reactive empty result using
                 * Mono.empty().
                 */
                /*
                 * In a real production scenario, you might want to externalize the time out
                 * configuration by adding a new field to the ClientProperties.
                 * In that way, you can change its value depending on the environment without
                 * having to rebuild the application. It’s also essential to monitor any timeout
                 * and tune its value if necessary.
                 */
                .timeout(Duration.ofSeconds(3), Mono.empty())
                // Returns an empty object when a 404 response is received
                .onErrorResume(WebClientResponseException.NotFound.class, exception -> Mono.empty())
                /*
                 * we want the timeout to apply to each retry attempt, so we’ll use
                 * the retryWhen() operator after timeout() whic means that the timeout is
                 * applied to each retry attempt. The time limiter is applied first. If the
                 * timeout expires, the retryWhen()
                 * operator kicks in and tries the request again.
                 */
                /*
                 * Exponential backoff is used
                 * as the retry strategy. Three
                 * attempts are allowed with
                 * a 100 ms initial backoff
                 */
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
                // If any error happens after the 3 retry attempts, catch the exception and return an empty object.
                .onErrorResume(Exception.class, exception -> Mono.empty());
    }

}