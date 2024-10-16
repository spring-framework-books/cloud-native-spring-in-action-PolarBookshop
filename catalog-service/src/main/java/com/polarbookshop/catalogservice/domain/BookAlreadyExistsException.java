package com.polarbookshop.catalogservice.domain;

/*
 * BookAlreadyExistsException is a runtime exception thrown when we try to add a
 * book to the catalog that is already there. It prevents duplicate entries in the catalog
 */
public class BookAlreadyExistsException extends RuntimeException {

    public BookAlreadyExistsException(String isbn) {
        super("A book with ISBN " + isbn + " already exists.");
    }

}