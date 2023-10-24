package com.peters.userservice.controller;


import com.peters.userservice.controller.proxy.BookFeignProxy;
import com.peters.userservice.controller.proxy.OrderFeignProxy;
import com.peters.userservice.dto.ChangePasswordDTO;
import com.peters.userservice.dto.CustomResponse;
import com.peters.userservice.dto.UserRequestDto;
import com.peters.userservice.service.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth/user")
@Tag(name = "Authenticated User")
@RequiredArgsConstructor
public class CustomerController {
    private final IUserService userService;
    private final BookFeignProxy bookFeignProxy;
    private final OrderFeignProxy orderFeignProxy;

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
    public ResponseEntity<CustomResponse> getAllBooks(@RequestParam(defaultValue = "0") int page){
        return bookFeignProxy.getAllBooks(page);
    }

    @GetMapping("/get-books-by-author")
    public ResponseEntity<CustomResponse> getBooksByAuthor(@RequestParam("author") String authorName){
        return bookFeignProxy.getBooksByAuthor(authorName);
    }

    @GetMapping("/get-books-by-title")
    public ResponseEntity<CustomResponse> getBooksByTitle(@RequestParam("title") String title){
        return bookFeignProxy.getBooksByTitle(title);
    }

    @GetMapping("/get-books-by-date")
    public ResponseEntity<CustomResponse> getBooksByPublishedDate(@RequestParam("published_date") String date){
        return bookFeignProxy.getBooksByPublishedDate(date);
    }

    @GetMapping("/add-to-cart")
    public ResponseEntity<CustomResponse> addBookToCart(@RequestParam("bookId") Long bookId, @RequestParam("userId") Long userId, @RequestParam("quantity") int quantity){
        return orderFeignProxy.addBookToCart(bookId, userId, quantity);
    }

}
