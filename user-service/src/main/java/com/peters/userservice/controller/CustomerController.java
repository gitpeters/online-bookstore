package com.peters.userservice.controller;


import com.peters.userservice.controller.proxy.BookFeignProxy;
import com.peters.userservice.controller.proxy.OrderFeignProxy;
import com.peters.userservice.dto.ChangePasswordDTO;
import com.peters.userservice.dto.CustomResponse;
import com.peters.userservice.dto.UserRequestDto;
import com.peters.userservice.exception.ErrorDetails;
import com.peters.userservice.exception.FeignErrorHandling;
import com.peters.userservice.exception.UserNotFoundException;
import com.peters.userservice.service.IUserService;
import feign.FeignException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/v1/auth/user")
@Tag(name = "Authenticated User")
@RequiredArgsConstructor
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
            return bookFeignProxy.getAllBooks(page);
        }catch (FeignException.NotFound e){
            return feignErrorHandling.handleFeignNotFound(e);
        }
    }

    @GetMapping("/get-books-by-author")
    public ResponseEntity<?> getBooksByAuthor(@RequestParam("author") String authorName){
        try {
            ResponseEntity<CustomResponse> booksByAuthor = bookFeignProxy.getBooksByAuthor(authorName);
            if(booksByAuthor.getBody()==null){
                throw new UserNotFoundException("No Resource found");
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
           if(booksByTitle.getBody()==null){
               throw new UserNotFoundException("No Resource found");
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
            if(booksByPublishedDate.getBody().getData()==null){
                throw new UserNotFoundException("No Resource found");
            }
            return booksByPublishedDate;
        }catch (FeignException.NotFound e){
            return feignErrorHandling.handleFeignNotFound(e);
        }
    }

    @GetMapping("/add-to-cart")
    public ResponseEntity<?> addBookToCart(@RequestParam("bookId") Long bookId, @RequestParam("userId") Long userId, @RequestParam("quantity") int quantity){
        try {
            return orderFeignProxy.addBookToCart(bookId, userId, quantity);
        }catch (FeignException.NotFound e){
          return feignErrorHandling.handleFeignNotFound(e);
        }catch (FeignException e){
           return feignErrorHandling.handleFeignError(e);
        }
    }

}
