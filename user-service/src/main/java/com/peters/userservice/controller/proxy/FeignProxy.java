package com.peters.userservice.controller.proxy;

import com.peters.userservice.dto.BookRequest;
import com.peters.userservice.dto.CustomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "book-service", url = "http://localhost:8080/api/v1/book")
public interface FeignProxy {
    @PostMapping("/add")
    ResponseEntity<CustomResponse> addBook(@RequestBody BookRequest request);

    @GetMapping("/all")
    ResponseEntity<CustomResponse> getAllBooks(@RequestParam(defaultValue = "0") int page);

    @GetMapping("/get-by-title")
    ResponseEntity<CustomResponse> getBooksByTitle(@RequestParam("title") String bookTitle);

    @GetMapping("/get-by-author")
    ResponseEntity<CustomResponse> getBooksByAuthor(@RequestParam("author") String author);

    @GetMapping("/get-by-published_date")
    ResponseEntity<CustomResponse> getBooksByPublishedDate(@RequestParam("date") LocalDate date);
}
