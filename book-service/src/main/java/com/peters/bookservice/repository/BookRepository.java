package com.peters.bookservice.repository;

import com.peters.bookservice.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitle(String bookTitle);
    List<Book> findByAuthor(String author);
    List<Book> findByPublishedDate(LocalDate date);
}
