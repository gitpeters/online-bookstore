package com.peters.userservice.service;


import com.peters.userservice.dto.ChangePasswordDTO;
import com.peters.userservice.dto.CustomResponse;
import com.peters.userservice.dto.UserAddressRequest;
import com.peters.userservice.dto.UserRequestDto;
import org.springframework.http.ResponseEntity;

public interface IUserService {

    ResponseEntity<CustomResponse> getAllUsers();
    ResponseEntity<CustomResponse> registerUser(UserRequestDto request);

    ResponseEntity<CustomResponse> findUserByEmail(String email);

    ResponseEntity<CustomResponse> fetchUserById(Long userId);


    ResponseEntity<CustomResponse> addAddress(Long userId, UserAddressRequest request);


    ResponseEntity<CustomResponse> changePassword(ChangePasswordDTO request);

    ResponseEntity<CustomResponse> updateProfile(Long userId, UserRequestDto request);

    ResponseEntity<CustomResponse> deleteProfile(Long userId);

    ResponseEntity<?> checkout(Long userId);

    ;
}
