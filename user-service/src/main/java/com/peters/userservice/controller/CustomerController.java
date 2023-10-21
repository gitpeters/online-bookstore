package com.peters.userservice.controller;


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

}
