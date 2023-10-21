package com.peters.bookservice.controller;

import com.peters.bookservice.dto.BookRequest;
import com.peters.bookservice.dto.CustomRequestResponse;
import com.peters.bookservice.service.IBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
public class BookController {
    private final IBookService bookService;

    @PostMapping("/add")
    public ResponseEntity<CustomRequestResponse> addBook(@RequestBody BookRequest request){
        return bookService.addBook(request);
    }
    @GetMapping("/all")
    public ResponseEntity<CustomRequestResponse> getAllBooks(@RequestParam(defaultValue = "0") int page){
        return bookService.getAllBooks(page);
    }

    @GetMapping("/get-by-title")
    public ResponseEntity<CustomRequestResponse> getBooksByTitle(@RequestParam("title") String bookTitle){
        return bookService.getBooksByTitle(bookTitle);
    }

    @GetMapping("/get-by-author")
    public ResponseEntity<CustomRequestResponse> getBooksByAuthor(@RequestParam("author") String author){
        return bookService.getBooksByAuthor(author);
    }

    @GetMapping("/get-by-published_date")
    public ResponseEntity<CustomRequestResponse> getBooksByPublishedDate(@RequestParam("date") LocalDate date){
        return bookService.getBooksByPublishedDate(date);
    }
}
