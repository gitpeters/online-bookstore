package com.peters.bookservice.controller;

import com.peters.bookservice.dto.BookRequest;
import com.peters.bookservice.dto.CustomRequestResponse;
import com.peters.bookservice.service.IBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
@Slf4j
public class BookController {
    private final IBookService bookService;

    @PostMapping("/add")
    public ResponseEntity<CustomRequestResponse> addBook(@RequestBody BookRequest request){
        log.info("add book to db -> {}", request);
        return bookService.addBook(request);
    }
    @GetMapping("/all")
    public ResponseEntity<CustomRequestResponse> getAllBooks(@RequestParam(defaultValue = "0") int page){
        log.info("Fetches all books");
        return bookService.getAllBooks(page);
    }

    @GetMapping("/get-by-title")
    public ResponseEntity<CustomRequestResponse> getBooksByTitle(@RequestParam("title") String bookTitle){
        log.info("Fetches all books by title -> {}", bookTitle);
        return bookService.getBooksByTitle(bookTitle);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<CustomRequestResponse> getBookById(@PathVariable("bookId") Long bookId){
        log.info("Fetch book by id -> {}", bookId);
        return bookService.getBookById(bookId);
    }

    @GetMapping("/get-by-author")
    public ResponseEntity<CustomRequestResponse> getBooksByAuthor(@RequestParam("author") String author){
        log.info("Fetches all books by author -> {}", author);
        return bookService.getBooksByAuthor(author);
    }

    @GetMapping("/get-by-published_date")
    public ResponseEntity<CustomRequestResponse> getBooksByPublishedDate(@RequestParam("date") LocalDate date){
        log.info("Fetches all books by published date -> {}", date);
        return bookService.getBooksByPublishedDate(date);
    }

    @PutMapping("{bookId}/edit")
    public ResponseEntity<CustomRequestResponse> editBook(@PathVariable("bookId") Long bookId, @RequestBody BookRequest request){
        return bookService.editBook(bookId,request);
    }

    @DeleteMapping("{bookId}/delete")
    public ResponseEntity<CustomRequestResponse> deleteBook(@PathVariable("bookId") Long bookId){
        return bookService.deleteBook(bookId);
    }
}
