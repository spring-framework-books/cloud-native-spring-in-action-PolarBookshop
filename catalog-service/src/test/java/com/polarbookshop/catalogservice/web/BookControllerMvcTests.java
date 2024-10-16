package com.polarbookshop.catalogservice.web;

import com.polarbookshop.catalogservice.domain.BookNotFoundException;
import com.polarbookshop.catalogservice.domain.BookService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Identifies a test class that focuses on Spring MVC components, explicitly targeting BookController
@WebMvcTest(BookController.class)
class BookControllerMvcTests {

    // Utility class to test the web layer in a mock environment
    @Autowired
    private MockMvc mockMvc;
    // Adds a mock of BookService to the Spring application context
    @MockBean
    private BookService bookService;

    @Test
    void whenGetBookNotExistingThenShouldReturn404() throws Exception {
        String isbn = "73737313940";

        // Defines the expected behavior for the BookService mock bean
        given(bookService.viewBookDetails(isbn))
                .willThrow(BookNotFoundException.class);

        // MockMvc is used to perform an HTTP GET request and verify the result.
        // and Expects the response to have a “404 Not Found” status
        mockMvc.perform(get("/books/" + isbn))
                .andExpect(status().isNotFound());
    }

}