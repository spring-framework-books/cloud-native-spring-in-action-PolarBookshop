package com.polarbookshop.catalogservice.web;

import java.util.HashMap;
import java.util.Map;

import com.polarbookshop.catalogservice.domain.Book;
import com.polarbookshop.catalogservice.domain.BookAlreadyExistsException;
import com.polarbookshop.catalogservice.domain.BookNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
 * Spring lets you handle error messages in different ways. When building an
 * API, it’s
 * good to consider which types of errors it can throw, since they are just as
 * important as
 * the domain data. When it’s a REST API, you want to ensure that the HTTP
 * response
 * uses a status code that best fits the purpose and includes a meaningful
 * message to
 * help the client identify the problem.
 * When the validation constraints we have just defined are not met, a
 * MethodArgument-
 * NotValidException is thrown. What if we try to fetch a book that doesn’t
 * exist? The
 * business logic we previously implemented throws dedicated exceptions
 * (BookAlready-
 * ExistsException and BookNotFoundException). All those exceptions should be
 * han-
 * dled in the REST API context to return the error codes defined in the
 * original
 * specification.
 * To handle errors for a REST API, we can use the standard Java exceptions and
 * rely
 * on a @RestControllerAdvice class to define what to do when a given exception
 * is
 * thrown. It’s a centralized approach that allows us to decouple the exception
 * handling
 * from the code throwing the exception. In the com.polarbookshop.catalogservice
 * .web package, create a BookControllerAdvice class as follows.
 */
// Marks the class as a centralized exception handler
@RestControllerAdvice
public class BookControllerAdvice {
    /*
     * The mapping provided in the @RestControllerAdvice class makes it possible to
     * obtain an HTTP response with status 422 (unprocessable entity) when we try to
     * create
     * a book that already exists in the catalog, a response with status 404 (not
     * found) when
     * we try to read a book that doesn’t exist, and a response with status 400 (bad
     * request)
     * when one or more fields in a Book object are invalid. Each response will
     * contain a mean-
     * ingful message that we defined as part of the validation constraint or custom
     * exception.
     */
    // Defines the exception for which the handler must be executed
    @ExceptionHandler(BookNotFoundException.class)
    // Defines the status code for the HTTP response created when the exception is
    // thrown
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String bookNotFoundHandler(BookNotFoundException ex) {
        // The message that will be included in the HTTP response body
        return ex.getMessage();
    }

    @ExceptionHandler(BookAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String bookAlreadyExistsHandler(BookAlreadyExistsException ex) {
        return ex.getMessage();
    }

    // Handles the exception thrown when the Book validation fails
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Collects meaningful error messages about which Book fields were invalid
        // instead of returning an empty message
        var errors = new HashMap<String, String>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}