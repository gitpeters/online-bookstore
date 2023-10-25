package com.peters.userservice.service;


import com.peters.userservice.controller.proxy.OrderFeignProxy;
import com.peters.userservice.dto.*;
import com.peters.userservice.entity.UserAddress;
import com.peters.userservice.entity.User;
import com.peters.userservice.entity.UserRole;
import com.peters.userservice.repository.IUserAddressRepository;
import com.peters.userservice.repository.IUserRepository;
import com.peters.userservice.repository.RoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService{

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IUserAddressRepository addressRepository;
    private final IRoleService roleService;
    private final HttpServletRequest servletRequest;
    private final RoleRepository roleRepository;
    private final OrderFeignProxy orderFeignProxy;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    @Override
    public ResponseEntity<CustomResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDto> userResponseList = users.stream()
                .map(user -> mapToUserResponse(user)).collect(Collectors.toList());

        CustomResponse successResponse = CustomResponse.builder()
                .status(HttpStatus.OK.name())
                .message("Successful")
                .data(userResponseList.isEmpty() ? null : userResponseList)
                .build();

        return ResponseEntity.ok(successResponse);
    }

    private UserResponseDto mapToUserResponse(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .role(new HashSet<>(user.getRoles()))
                .build();
    }

    @Override
    public ResponseEntity<CustomResponse> registerUser(UserRequestDto request) {
        Optional<User> userOpt = userRepository.findUserByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            if (request == null) {
                return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "Request body is required"));
            }
            if (request.getFirstName() == null) {
                return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "firstName is required"));
            }
            if (request.getLastName() == null) {
                return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "lastName is required"));
            }
            if (request.getEmail() == null) {
                return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "email is required"));
            }
            if (!validateEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "provide correct email format"));
            }
            if (request.getPassword() == null) {
                return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "password is required"));
            }

            Optional<UserRole> role = roleRepository.findByName("ROLE_USER");
            UserRole adminRole = null;
            if(!role.isPresent()){
                UserRole newAdminUser= UserRole.builder()
                        .name("ROLE_USER")
                        .build();
                adminRole= roleRepository.save(newAdminUser);
            }else{
                adminRole = role.get();
            }
            User newUser = User.builder()
                    .email(request.getEmail())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .roles(Collections.singleton(adminRole))
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();
            userRepository.save(newUser);
            return ResponseEntity.ok(new CustomResponse(HttpStatus.OK, "Successfully registered user"));
        }
        return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "User already exists"));
    }

    @Override
    public ResponseEntity<CustomResponse> findUserByEmail(String email) {
        Optional<User> userOpt = userRepository.findUserByEmail(email);
        if(!userOpt.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "No user with email address found"));
        }
        User user = userOpt.get();
        UserResponseDto response = UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(new HashSet<>(user.getRoles()))
                .build();
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), response, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> fetchUserById(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if(userOpt.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "No user found"));
        }
        User user = userOpt.get();
        UserResponseDto response = UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(new HashSet<>(user.getRoles()))
                .build();
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), response, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> changePassword(ChangePasswordDTO request) {
        if(request.getEmail() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "email is required"));
        }
        if(!validateEmail(request.getEmail())){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "provide correct email format"));
        }
        if(request.getOldPassword() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "oldPassword is required"));
        }
        if(request.getNewPassword() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "newPassword is required"));
        }
        Optional<User> userOpt = userRepository.findUserByEmail(request.getEmail());

        if(userOpt.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "No user is associated with this email"));
        }

        User user = userOpt.get();
        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "Old password is not correct. Try again"));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK, "Password has been successfully changed"));
    }

    @Override
    public ResponseEntity<CustomResponse> updateProfile(Long userId, UserRequestDto request) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "No user found " +
                    "for " +
                    "this " +
                    "id: "+userId));
        }
        User user = userOptional.get();
        BeanUtils.copyProperties(request, user, getNullPropertyNames(request));
        User updatedUser = userRepository.save(user);
        if(updatedUser!=null){
            return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), updatedUser, "Successfully updated user" +
                    " profile"));
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<CustomResponse> deleteProfile(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "No user found " +
                    "for " +
                    "this " +
                    "id: "+userId));
        }
        User user = userOptional.get();
        user.setAccountDeleted(true);
        userRepository.save(user);
        return  ResponseEntity.ok(new CustomResponse(HttpStatus.OK,"Successfully deleted user" +
                " profile"));
    }

    @Override
    public ResponseEntity<?> checkout(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No user found");
        }
        User user = userOptional.get();
        return orderFeignProxy.checkout(userId, user.getEmail());
    }

    @Override
    public ResponseEntity<CustomResponse> addAddress(Long userId, UserAddressRequest request) {
        Optional<User> userOpt = userRepository.findById(userId);
        if(!userOpt.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "User not found"));
        }

        if(request.getPhoneNumber()==null || request.getPhoneNumber().isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "Phone number is required"));
        }
        if(request.getStreet()==null || request.getStreet().isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "Street is required"));
        }
        if(request.getCity()==null || request.getCity().isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "City is required"));
        }
        if(request.getState()==null || request.getState().isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "State is required"));
        }
        if(request.getCountry()==null || request.getCountry().isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST, "Country is required"));
        }

        User user = userOpt.get();

        UserAddress address = UserAddress.builder()
                .city(request.getCity())
                .street(request.getStreet())
                .phoneNumber(request.getPhoneNumber())
                .user(user)
                .state(request.getState())
                .country(request.getCountry())
                .build();
        addressRepository.save(address);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK, "Successful"));
    }

    public static boolean validateEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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

    public static int theToken() {
        Random random = new Random();
        int min = 100000; // Minimum 6-digit number
        int max = 999999; // Maximum 6-digit number
        return random.nextInt(max - min + 1) + min;
    }


}
