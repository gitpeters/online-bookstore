package com.peters.userservice.controller;


import com.peters.userservice.controller.proxy.BookFeignProxy;
import com.peters.userservice.controller.proxy.OrderFeignProxy;
import com.peters.userservice.dto.ChangePasswordDTO;
import com.peters.userservice.dto.CustomResponse;
import com.peters.userservice.dto.UserRequestDto;
import com.peters.userservice.exception.ErrorDetails;
import com.peters.userservice.exception.FeignErrorHandling;
import com.peters.userservice.exception.FeignResourceNotFoundException;
import com.peters.userservice.exception.UserNotFoundException;
import com.peters.userservice.service.IUserService;
import feign.FeignException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/v1/auth/user")
@Tag(name = "Authenticated User")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {
    private final IUserService userService;
    private final BookFeignProxy bookFeignProxy;
    private final OrderFeignProxy orderFeignProxy;
    private final FeignErrorHandling feignErrorHandling;

    @GetMapping("/{userId}/profile")
    public ResponseEntity<CustomResponse> userProfile(@PathVariable("userId") Long userId){
        return userService.fetchUserById(userId);
    }

    @PostMapping("/change-password")
    public ResponseEntity<CustomResponse> changePassword(@RequestBody ChangePasswordDTO request){
        return userService.changePassword(request);
    }

    @PatchMapping("/{userId}/profile")
    public ResponseEntity<CustomResponse> updateProfile(@PathVariable("userId") Long userId, UserRequestDto request){
        return userService.updateProfile(userId, request);
    }

    @DeleteMapping("/{userId}/profile")
    public ResponseEntity<CustomResponse> deleteProfile(@PathVariable("userId") Long userId){
        return userService.deleteProfile(userId);
    }

    @GetMapping("/get-all-books")
    public ResponseEntity<?> getAllBooks(@RequestParam(defaultValue = "0") int page){
        try{
            ResponseEntity<CustomResponse> allBooks = bookFeignProxy.getAllBooks(page);
            log.info("Successfully made a request call to book-service {} ", allBooks);
            if(allBooks==null || allBooks.getBody()==null){
                throw new FeignResourceNotFoundException("No Resource found");
            }
            return allBooks;
        }catch (FeignException.NotFound e){
            return feignErrorHandling.handleFeignNotFound(e);
        }
    }

    @GetMapping("/get-books-by-author")
    public ResponseEntity<?> getBooksByAuthor(@RequestParam("author") String authorName){
        try {
            ResponseEntity<CustomResponse> booksByAuthor = bookFeignProxy.getBooksByAuthor(authorName);
            log.info("Successfully made a request call to book-service {} ", booksByAuthor);
            if(booksByAuthor==null || booksByAuthor.getBody()==null){
                throw new FeignResourceNotFoundException("No Resource found");
            }
            return booksByAuthor;
        }catch (FeignException.NotFound e){
            return feignErrorHandling.handleFeignNotFound(e);
        }catch (FeignException e){
            return feignErrorHandling.handleFeignError(e);
        }
    }

    @GetMapping("/get-books-by-title")
    public ResponseEntity<CustomResponse> getBooksByTitle(@RequestParam("title") String title){
       try{
           ResponseEntity<CustomResponse> booksByTitle = bookFeignProxy.getBooksByTitle(title);
           log.info("Successfully made a request call to book-service {} ", booksByTitle);
           if(booksByTitle==null || booksByTitle.getBody()==null){
               throw new FeignResourceNotFoundException("No Resource found");
           }
           return booksByTitle;
       }catch (FeignException.NotFound e){
           return (ResponseEntity<CustomResponse>) feignErrorHandling.handleFeignNotFound(e);
       }
    }

    @GetMapping("/get-books-by-date")
    public ResponseEntity<?> getBooksByPublishedDate(@RequestParam("published_date") String date){
        try{
            ResponseEntity<CustomResponse> booksByPublishedDate = bookFeignProxy.getBooksByPublishedDate(date);
            log.info("Successfully made a request call to book-service {} ", booksByPublishedDate);
            if(booksByPublishedDate==null || booksByPublishedDate.getBody().getData()==null){
                throw new FeignResourceNotFoundException("No Resource found");
            }
            return booksByPublishedDate;
        }catch (FeignException.NotFound e){
            return feignErrorHandling.handleFeignNotFound(e);
        }
    }

    @GetMapping("/add-to-cart")
    public ResponseEntity<?> addBookToCart(@RequestParam("bookId") Long bookId, @RequestParam("userId") Long userId, @RequestParam("quantity") int quantity){
        try {
            ResponseEntity<CustomResponse> addBookToCartResponse = orderFeignProxy.addBookToCart(bookId, userId, quantity);
            log.info("Successfully made a request call to order-service {} ", addBookToCartResponse);
            if(addBookToCartResponse ==null || addBookToCartResponse.getBody().getData()==null){
                throw new FeignResourceNotFoundException("No Resource found");
            }
            return addBookToCartResponse;
        }catch (FeignException.NotFound e){
          return feignErrorHandling.handleFeignNotFound(e);
        }catch (FeignException e){
           return feignErrorHandling.handleFeignError(e);
        }
    }

    @GetMapping("/{userId}/carts")
    public ResponseEntity<?> getAllCarts(@PathVariable("userId") Long userId){
        try{
            ResponseEntity<CustomResponse> cartsByUser = orderFeignProxy.getAllCarts(userId);
            log.info("Successfully made a request call to order-service {} ", cartsByUser);
            if (cartsByUser == null || cartsByUser.getBody() == null) {
                throw new FeignResourceNotFoundException("No Resource found");
            }
            return cartsByUser;
        }catch (FeignException.NotFound e){
            return feignErrorHandling.handleFeignNotFound(e);
        }
    }

    @GetMapping("/cart/{cartId}")
    public ResponseEntity<?> getCart(@PathVariable("cartId") String cartId){
        try{
            ResponseEntity<CustomResponse> cartById = orderFeignProxy.getCart(cartId);
            log.info("Successfully made a request call to order-service {} ", cartById);
            if (cartById == null || cartById.getBody() == null) {
                throw new FeignResourceNotFoundException("No Resource found");
            }
            return cartById;
        }catch (FeignException.NotFound e){
            return feignErrorHandling.handleFeignNotFound(e);
        }
    }

    @PutMapping("/edit-cart/{cartId}")
    public ResponseEntity<?> editCart(@PathVariable("cartId") String cartId, @RequestParam("quantity") int quantity){
        try{
            ResponseEntity<CustomResponse> editCartQuantity = orderFeignProxy.editCart(cartId, quantity);
            log.info("Successfully made a request call to order-service {} ", editCartQuantity);
            if (editCartQuantity == null || editCartQuantity.getBody() == null) {
                throw new FeignResourceNotFoundException("No Resource found");
            }
            return editCartQuantity;
        }catch (FeignException.NotFound e){
            return feignErrorHandling.handleFeignNotFound(e);
        }
    }

    @DeleteMapping("/delete-cart/{cartId}")
    public ResponseEntity<?> deleteCart(@PathVariable("cartId") String cartId){
        try{
            ResponseEntity<CustomResponse> deletedCart = orderFeignProxy.deleteCart(cartId);
            log.info("Successfully made a request call to order-service {} ", deletedCart);
            if (deletedCart == null || deletedCart.getBody() == null) {
                throw new FeignResourceNotFoundException("No Resource found");
            }
            return deletedCart;
        }catch (FeignException.NotFound e){
            return feignErrorHandling.handleFeignNotFound(e);
        }
    }

    @DeleteMapping("/{userId}/clear-cart")
    public ResponseEntity<?> clearCart(@PathVariable("userId") Long userId){
        try{
            ResponseEntity<?> deletedCarts = orderFeignProxy.deleteAllCarts(userId);
            log.info("Successfully made a request call to order-service {} ", deletedCarts);
            if (deletedCarts == null || deletedCarts.getBody() == null) {
                throw new FeignResourceNotFoundException("No Resource found");
            }
            return deletedCarts;
        }catch (FeignException.NotFound e){
            return feignErrorHandling.handleFeignNotFound(e);
        }
    }

    @GetMapping("/{userId}/checkout")
    ResponseEntity<?> checkout(@PathVariable("userId") Long userId){

        return userService.checkout(userId);
    }

    @GetMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@RequestParam("payment_reference_id") String paymentReferenceId){
        try{
            ResponseEntity<?> confirmPayments = orderFeignProxy.confirmPayment(paymentReferenceId);
            log.info("Successfully made a request call to order-service {} ", confirmPayments);
            if (confirmPayments == null || confirmPayments.getBody() == null) {
                throw new FeignResourceNotFoundException("No Resource found");
            }
            return confirmPayments;
        }catch (FeignException.NotFound e){
            return feignErrorHandling.handleFeignNotFound(e);
        }catch (FeignException.InternalServerError e){
            return feignErrorHandling.handleFeignError(e);
        }
    }

}
