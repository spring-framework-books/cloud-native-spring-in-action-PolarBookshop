package com.polarbookshop.orderservice.order.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

// order is a reserved word in SQL
@Table("orders")
public record Order(

        @Id Long id,

        String bookIsbn,
        String bookName,
        Double bookPrice,
        Integer quantity,
        /*
         * Orders can go through different phases. If the requested book is available in
         * the catalog, then the order is accepted. If not, it’s rejected.
         * Once the order is accepted, it can be dispatched
         */
        OrderStatus status,

        @CreatedDate Instant createdDate,

        @LastModifiedDate Instant lastModifiedDate,

        @CreatedBy String createdBy,

        @LastModifiedBy String lastModifiedBy,

        @Version int version) {

    public static Order of(String bookIsbn, String bookName, Double bookPrice, Integer quantity, OrderStatus status) {
        return new Order(null, bookIsbn, bookName, bookPrice, quantity, status, null, null, null, null, 0);
    }

}