package com.polarbookshop.orderservice.order.domain;

import com.polarbookshop.orderservice.book.Book;
import com.polarbookshop.orderservice.book.BookClient;
import com.polarbookshop.orderservice.order.event.OrderAcceptedMessage;
import com.polarbookshop.orderservice.order.event.OrderDispatchedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final BookClient bookClient;
    private final OrderRepository orderRepository;
    private final StreamBridge streamBridge;

    public OrderService(BookClient bookClient, StreamBridge streamBridge, OrderRepository orderRepository) {
        this.bookClient = bookClient;
        this.orderRepository = orderRepository;
        this.streamBridge = streamBridge;
    }

    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /*
     * All that’s left now is calling the method whenever a submitted order is
     * accepted.
     * That’s a critical point and one of the aspects characterizing the saga
     * pattern, a popular
     * alternative to distributed transactions in microservice architectures. To
     * ensure consis-
     * tency in your system, persisting an order in the database and sending a
     * message about
     * it must be done atomically. Either both operations succeed, or they both must
     * fail. A
     * simple yet effective way to ensure atomicity is by wrapping the two
     * operations in a
     * local transaction. To do that, we can rely on the built-in Spring transaction
     * manage-
     * ment functionality.
     */
    @Transactional
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
                .flatMap(orderRepository::save)
                // Publishes an event if the order is accepted
                .doOnNext(this::publishOrderAcceptedEvent);
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

    /*
     * the event source is not a message broker, but a REST endpoint.
     * When a user sends a POST request to Order Service for purchasing a book, we
     * want to
     * publish an event signaling whether the order has been accepted.
     * We can bridge the REST layer with the stream part of the application using a
     * Stream-
     * Bridge object that allows us to send data to a specific destination
     * imperatively. Let’s
     * break this new functionality down. First, we can implement a method that
     * accepts an
     * Order object as input, verifies it’s accepted, builds an OrderAcceptedMessage
     * object,
     * and sends it to a RabbitMQ destination using StreamBridge.
     */
    private void publishOrderAcceptedEvent(Order order) {
        // If the order is not accepted, it does nothing.
        if (!order.status().equals(OrderStatus.ACCEPTED)) {
            return;
        }
        // Builds a message to notify that an order has been accepted
        var orderAcceptedMessage = new OrderAcceptedMessage(order.id());
        log.info("Sending order accepted event with id: {}", order.id());
        // Explicitly sends a message to the acceptOrder-out-0 binding
        /*
         * Since the data source is a REST endpoint, there is no Supplier bean we can
         * register
         * with Spring Cloud Function, and therefore there is no trigger for the
         * framework to
         * create the necessary bindings with RabbitMQ. Yet, in listing 10.23,
         * StreamBridge is
         * used to send data to an acceptOrder-out-0 binding.
         * At startup time, Spring Cloud Stream will notice that StreamBridge wants to
         * publish
         * messages via an acceptOrder-out-0 binding, and it will create one
         * automatically.
         */
        var result = streamBridge.send("acceptOrder-out-0", orderAcceptedMessage);
        log.info("Result of sending data for order with id {}: {}", order.id(), result);
    }

    // update the status in the database for an existing order after it’s
    // dispatched.
    // Accepts a reactive stream of OrderDispatchedMessage objects as input
    public Flux<Order> consumeOrderDispatchedEvent(Flux<OrderDispatchedMessage> flux) {
        return flux
                // For each object emitted to the stream, it reads the related order from the
                // database.
                .flatMap(message -> orderRepository.findById(message.orderId()))
                // Updates the order with the “dispatched” status
                .map(this::buildDispatchedOrder)
                // Saves the updated order in the database
                .flatMap(orderRepository::save);
    }

    private Order buildDispatchedOrder(Order existingOrder) {
        return new Order(
                existingOrder.id(),
                existingOrder.bookIsbn(),
                existingOrder.bookName(),
                existingOrder.bookPrice(),
                existingOrder.quantity(),
                OrderStatus.DISPATCHED,
                existingOrder.createdDate(),
                existingOrder.lastModifiedDate(),
                existingOrder.version());
    }

}