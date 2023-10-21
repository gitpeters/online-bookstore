package com.peters.userservice.controller;


import com.peters.userservice.dto.CustomResponse;
import com.peters.userservice.dto.LoginRequestDto;
import com.peters.userservice.dto.UserAddressRequest;
import com.peters.userservice.dto.UserRequestDto;
import com.peters.userservice.service.IUserService;
import com.peters.userservice.service.UserAuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
@Tag(name = "user")
public class UserController {
    private final IUserService userService;
    private final UserAuthenticationService authenticationService;


    // user related endpoints
    @PostMapping
    public ResponseEntity<CustomResponse> register(@RequestBody UserRequestDto requestDto){
        return userService.registerUser(requestDto);
    }


    @PostMapping("/authenticate")
    public ResponseEntity<CustomResponse> authenticateUser(@RequestBody @Validated LoginRequestDto loginRequest) throws Exception {
        return authenticationService.createAuthenticationTokenAndAuthenticateUser(loginRequest);
    }

    @PostMapping("/{userId}/add-address")
    public ResponseEntity<CustomResponse> addAddress(@PathVariable Long userId, UserAddressRequest request){
        return userService.addAddress(userId, request);
    }

}
