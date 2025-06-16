package com.polarbookshop.orderservice.order.domain;

import com.polarbookshop.orderservice.book.Book;
import com.polarbookshop.orderservice.book.BookClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final BookClient bookClient;
    private final OrderRepository orderRepository;

    public OrderService(BookClient bookClient, OrderRepository orderRepository) {
        this.bookClient = bookClient;
        this.orderRepository = orderRepository;
    }

    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Mono<Order> submitOrder(String isbn, int quantity) {
        /*
         * autowire a BookClient instance and
         * use the underlying WebClient to start a reactive stream to process the book
         * information and create an order. The map() operator lets you map a Book to an
         * accepted Order. If BookClient returns an empty result, you can define a
         * rejected Order
         * with the defaultIfEmpty() operator. Finally, the stream is ended by calling
         * OrderRepository to save the order (either as accepted or rejected).
         * 
         */
        // Calls the Catalog Service to check the book’s availability
        return bookClient.getBookByIsbn(isbn)
                // If the book is available, it accepts the order
                .map(book -> buildAcceptedOrder(book, quantity))
                // If the book is not available, it rejects the order
                .defaultIfEmpty(buildRejectedOrder(isbn, quantity))
                // Saves the order (either as accepted or rejected)
                .flatMap(orderRepository::save);
    }

    public static Order buildAcceptedOrder(Book book, int quantity) {
        /*
         * When an order is accepted, we specify ISBN,
         * book name (title + author), quantity, and
         * status. Spring Data takes care of adding the
         * identifier, version, and audit metadata.
         */
        return Order.of(book.isbn(), book.title() + " - " + book.author(),
                book.price(), quantity, OrderStatus.ACCEPTED);
    }

    public static Order buildRejectedOrder(String bookIsbn, int quantity) {
        return Order.of(bookIsbn, null, null, quantity, OrderStatus.REJECTED);
    }

}