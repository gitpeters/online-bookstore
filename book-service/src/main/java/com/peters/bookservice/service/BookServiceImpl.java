package com.peters.bookservice.service;

import com.peters.bookservice.dto.BookRequest;
import com.peters.bookservice.dto.CustomRequestResponse;
import com.peters.bookservice.dto.ResponseStatus;
import com.peters.bookservice.entity.Book;
import com.peters.bookservice.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.peters.bookservice.dto.ResponseStatus.FAILED;
import static com.peters.bookservice.dto.ResponseStatus.SUCCESSFUL;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements IBookService{
    private final BookRepository bookRepository;

    @Override
    public ResponseEntity<CustomRequestResponse> addBook(BookRequest request) {
        if(request.getAuthor()==null){
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomRequestResponse("Failed", "author name is required"));
        }

        if(request.getPrice()==null){
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomRequestResponse("Failed", "price is required"));
        }

        if(request.getTitle()==null){
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomRequestResponse("Failed", "book title is required"));
        }

        Book book = Book.builder()
                .author(request.getAuthor())
                .price(request.getPrice())
                .isAvailable(true)
                .publishedDate(request.getPublishedDate())
                .build();

        Book savedRecord = bookRepository.save(book);
        if(savedRecord!=null){
            return ResponseEntity.ok(new CustomRequestResponse(HttpStatus.CREATED.name(), savedRecord, "Successful"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CustomRequestResponse("Failed", "Something went wrong. Could not add new book"));
    }

    @Override
    public ResponseEntity<CustomRequestResponse> getAllBooks(int page) {
        int pageSize = 10;

        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Book> bookPage = bookRepository.findAll(pageable);
        if(bookPage.hasContent()){
            List<Book> books = bookPage.getContent();
            long totalElements = bookPage.getTotalElements();
            int totalPages = bookPage.getTotalPages();
            int currentPage = bookPage.getNumber();

            return ResponseEntity.ok(new CustomRequestResponse(SUCCESSFUL.name(), books,
                    "Successfully fetched books, page -> " + currentPage +
                            " of " + totalPages + " (total elements: " + totalElements + ")"));
        } else {
            return ResponseEntity.badRequest().body(new CustomRequestResponse(FAILED.name(),
                    "No books found or something went wrong."));
        }
    }

    @Override
    public ResponseEntity<CustomRequestResponse> getBooksByTitle(String bookTitle) {
        List<Book> books = bookRepository.findByTitle(bookTitle);
        if(!books.isEmpty()){
            return ResponseEntity.ok(new CustomRequestResponse(HttpStatus.FOUND.name(), books, "Successful"));
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new CustomRequestResponse("Failed", "No book found with this title"));
    }

    @Override
    public ResponseEntity<CustomRequestResponse> getBooksByAuthor(String author) {
        List<Book> books = bookRepository.findByAuthor(author);
        if(!books.isEmpty()){
            return ResponseEntity.ok(new CustomRequestResponse(HttpStatus.FOUND.name(), books, "Successful"));
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new CustomRequestResponse("Failed", "No book found with this author's name"));
    }

    @Override
    public ResponseEntity<CustomRequestResponse> getBooksByPublishedDate(LocalDate date) {
        List<Book> books = bookRepository.findByPublishedDate(date);
        if(!books.isEmpty()){
            return ResponseEntity.ok(new CustomRequestResponse(HttpStatus.FOUND.name(), books, "Successful"));
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new CustomRequestResponse("Failed", "No book found with this date"));
    }
}
