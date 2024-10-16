package com.polarbookshop.catalogservice.domain;

/*
 * BookNotFoundException is a runtime exception thrown when we try to fetch a book
 * that is not in the catalog.
 */
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String isbn) {
        super("The book with ISBN " + isbn + " was not found.");
    }

}