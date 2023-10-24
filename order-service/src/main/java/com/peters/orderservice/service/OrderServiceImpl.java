package com.peters.orderservice.service;

import com.peters.orderservice.controller.proxy.FeignProxy;
import com.peters.orderservice.dto.BookResponse;
import com.peters.orderservice.dto.CustomResponse;
import com.peters.orderservice.model.Cart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final FeignProxy feignProxy;
    @Override
    public ResponseEntity<CustomResponse> addBookToCart(Long bookId, Long userId, int quantity) {
        ResponseEntity<CustomResponse> response = feignProxy.getBookById(bookId);
        log.info("Make a request call to the book service: {} ", response);

        if (response.getStatusCode().is2xxSuccessful()) {
            CustomResponse customResponse = response.getBody();
            if (customResponse.getData() != null && customResponse.getData() instanceof Map) {
                Map<String, Object> bookData = (Map<String, Object>) customResponse.getData();
                if (bookData.containsKey("title") && bookData.containsKey("author") && bookData.containsKey("price")) {
                    String bookName = (String) bookData.get("title");
                    String authorName = (String) bookData.get("author");
                    BigDecimal bookPrice = new BigDecimal(String.valueOf(bookData.get("price")));

                    Cart cart = Cart.builder()
                            .bookId(bookId)
                            .userId(userId)
                            .bookName(bookName)
                            .authorName(authorName)
                            .bookPrice(bookPrice)
                            .quantity(quantity)
                            .subTotal(bookPrice.multiply(BigDecimal.valueOf(quantity)))
                            .dateAdded(LocalDate.now())
                            .build();

                    return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), cart, "Successfully added book to cart"));
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomResponse(HttpStatus.NOT_FOUND.name(), customResponse, "Could not find book by this id: " + bookId));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CustomResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong"));
    }


}
