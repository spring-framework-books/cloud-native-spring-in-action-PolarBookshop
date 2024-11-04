package com.polarbookshop.catalogservice.domain;

import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface BookRepository extends CrudRepository<Book, Long> {
    // The default methods defined by CrudRepository for Book objects are based on
    // their @Id-annotated fields. Since the application needs to access books based
    // on the
    // ISBN, we must explicitly declare those operations.
    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    @Modifying
    @Transactional
    @Query("delete from Book where isbn = :isbn")
    void deleteByIsbn(String isbn);

}