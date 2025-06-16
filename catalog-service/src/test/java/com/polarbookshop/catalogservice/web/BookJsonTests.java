package com.polarbookshop.catalogservice.web;

import java.time.Instant;

import com.polarbookshop.catalogservice.domain.Book;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

// Identifies a test class that focuses on JSON serialization
@JsonTest
class BookJsonTests {

        // Utility class to assert JSON serialization and deserialization
        @Autowired
        private JacksonTester<Book> json;

        @Test
        void testSerialize() throws Exception {
                var now = Instant.now();
                var book = new Book(394L, "1234567890", "Title", "Author", 9.90, "Polarsophia", now, now, 21);
                // Verifying the parsing from Java to JSON, using the JsonPath format to
                // navigate the JSON object
                var jsonContent = json.write(book);
                assertThat(jsonContent).extractingJsonPathNumberValue("@.id")
                                .isEqualTo(book.id().intValue());
                assertThat(jsonContent).extractingJsonPathStringValue("@.isbn")
                                .isEqualTo(book.isbn());
                assertThat(jsonContent).extractingJsonPathStringValue("@.title")
                                .isEqualTo(book.title());
                assertThat(jsonContent).extractingJsonPathStringValue("@.author")
                                .isEqualTo(book.author());
                assertThat(jsonContent).extractingJsonPathNumberValue("@.price")
                                .isEqualTo(book.price());
                assertThat(jsonContent).extractingJsonPathStringValue("@.publisher")
                                .isEqualTo(book.publisher());
                assertThat(jsonContent).extractingJsonPathStringValue("@.createdDate")
                                .isEqualTo(book.createdDate().toString());
                assertThat(jsonContent).extractingJsonPathStringValue("@.lastModifiedDate")
                                .isEqualTo(book.lastModifiedDate().toString());
                assertThat(jsonContent).extractingJsonPathNumberValue("@.version")
                                .isEqualTo(book.version());
        }

        @Test
        void testDeserialize() throws Exception {
                var instant = Instant.parse("2021-09-07T22:50:37.135029Z");
                // Defines a JSON object using the Java text block feature
                var content = """
                                {
                                    "id": 394,
                                    "isbn": "1234567890",
                                    "title": "Title",
                                    "author": "Author",
                                    "price": 9.90,
                                    "publisher": "Polarsophia",
                                    "createdDate": "2021-09-07T22:50:37.135029Z",
                                    "lastModifiedDate": "2021-09-07T22:50:37.135029Z",
                                    "version": 21
                                }
                                """;
                // Verifies the parsing from JSON to Java
                assertThat(json.parse(content))
                                .usingRecursiveComparison()
                                .isEqualTo(new Book(394L, "1234567890", "Title", "Author", 9.90, "Polarsophia", instant,
                                                instant, 21));
        }

}