package com.peters.bookservice.service;

import com.peters.bookservice.dto.BookRequest;
import com.peters.bookservice.dto.CustomRequestResponse;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface IBookService {

    ResponseEntity<CustomRequestResponse> getAllBooks(int page);

    ResponseEntity<CustomRequestResponse> getBooksByTitle(String bookTitle);

    ResponseEntity<CustomRequestResponse> getBooksByAuthor(String author);

    ResponseEntity<CustomRequestResponse> getBooksByPublishedDate(LocalDate date);

    ResponseEntity<CustomRequestResponse> addBook(BookRequest request);

    ResponseEntity<CustomRequestResponse> editBook(Long bookId, BookRequest request);

    ResponseEntity<CustomRequestResponse> deleteBook(Long bookId);

    ResponseEntity<CustomRequestResponse> getBookById(Long bookId);
}
