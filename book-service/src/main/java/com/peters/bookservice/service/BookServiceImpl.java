package com.peters.bookservice.service;

import com.peters.bookservice.dto.BookRequest;
import com.peters.bookservice.dto.BookResponse;
import com.peters.bookservice.dto.CustomRequestResponse;
import com.peters.bookservice.dto.ResponseStatus;
import com.peters.bookservice.entity.Book;
import com.peters.bookservice.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

import static com.peters.bookservice.dto.ResponseStatus.FAILED;
import static com.peters.bookservice.dto.ResponseStatus.SUCCESSFUL;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements IBookService{
    private final BookRepository bookRepository;

    @Override
    public ResponseEntity<CustomRequestResponse> addBook(BookRequest request) {
        if (request.getAuthor() == null || "string".equals(request.getAuthor())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomRequestResponse("Failed", "author name is required"));
        }

        if (request.getPrice() == null || request.getPrice() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomRequestResponse("Failed", "price is required"));
        }

        if (request.getTitle() == null || "string".equals(request.getTitle())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomRequestResponse("Failed", "book title is required"));
        }

        Book book = Book.builder()
                .author(request.getAuthor())
                .title(request.getTitle())
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
        if(books.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new CustomRequestResponse("Failed", "No book found with this author's name"));
        }
        return ResponseEntity.ok(new CustomRequestResponse(HttpStatus.FOUND.name(), books, "Successful"));
    }

    @Override
    public ResponseEntity<CustomRequestResponse> getBooksByPublishedDate(LocalDate date) {
        List<Book> books = bookRepository.findByPublishedDate(date);
        if(!books.isEmpty()){
            return ResponseEntity.ok(new CustomRequestResponse(HttpStatus.FOUND.name(), books, "Successful"));
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new CustomRequestResponse("Failed", "No book found with this date"));
    }

    @Override
    public ResponseEntity<CustomRequestResponse> editBook(Long id, BookRequest request) {
        if(id==null || id==0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomRequestResponse(HttpStatus.BAD_REQUEST.name(), "bookId is required"));
        }
        Optional<Book> bookOptional = bookRepository.findById(id);
        if(bookOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomRequestResponse("Failed", "No book found for this id"));
        }
        Book existingBook = bookOptional.get();
        BeanUtils.copyProperties(request, existingBook, getNullPropertyNames(request));
        Book updatedBook = bookRepository.save(existingBook);
        if(updatedBook !=null){
            return ResponseEntity.ok(new CustomRequestResponse(HttpStatus.OK.name(), updatedBook, "Successfully updated book"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CustomRequestResponse("Error", "Something went wrong. Could not update book"));
    }

    @Override
    public ResponseEntity<CustomRequestResponse> getBookById(Long bookId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        if(bookOptional.isEmpty()){
            log.info("No book found for this id-> {}", bookId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomRequestResponse(HttpStatus.NOT_FOUND.name(), "No book found for this id -> "+bookId));
        }
        Book book = bookOptional.get();
        log.info("Fetched book by id", book);
        BookResponse response = BookResponse.builder()
                .author(book.getAuthor())
                .title(book.getTitle())
                .price(book.getPrice())
                .publishedDate(book.getPublishedDate())
                .build();
        return ResponseEntity.ok(new CustomRequestResponse(HttpStatus.OK.name(), response, "Successful"));
    }

    @Override
    public ResponseEntity<CustomRequestResponse> deleteBook(Long bookId) {
        if(bookId==null || bookId==0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomRequestResponse(HttpStatus.BAD_REQUEST.name(), "bookId is required"));
        }
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        if(bookOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomRequestResponse("Failed", "No book found for this id"));
        }
        Book existingBook = bookOptional.get();
        bookRepository.delete(existingBook);
        return ResponseEntity.ok(new CustomRequestResponse(HttpStatus.OK.name(),existingBook,"Successfully deleted book"));
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
    public static<T> ResponseEntity validateRequestFields(T requestDto){
        if(requestDto==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomRequestResponse(HttpStatus.BAD_REQUEST.name(), "request body is required"));
        }

        // Get all fields in the request dto class
        Field[] fields = requestDto.getClass().getDeclaredFields();
        for(Field field: fields){
            try{
                field.setAccessible(true);
                Object fieldValue = field.get(requestDto);
                if((fieldValue !=null && fieldValue.toString().equals("string")) || Objects.equals(fieldValue, "")){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomRequestResponse(HttpStatus.BAD_REQUEST.name(), "Request value cannot be 'string'. Provide a valid input."));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return ResponseEntity.ok().build();
    }
}
