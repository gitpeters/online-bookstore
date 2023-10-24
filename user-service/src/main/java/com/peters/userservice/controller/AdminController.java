package com.peters.userservice.controller;


import com.peters.userservice.controller.proxy.BookFeignProxy;
import com.peters.userservice.dto.BookRequest;
import com.peters.userservice.dto.CustomResponse;
import com.peters.userservice.dto.UserResponseDto;
import com.peters.userservice.dto.UserRoleRequestDto;
import com.peters.userservice.entity.UserRole;
import com.peters.userservice.service.IRoleService;
import com.peters.userservice.service.IUserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "admin")
public class AdminController {
    private final IRoleService roleService;
    private final IUserService userService;
    private final BookFeignProxy bookFeignProxy;

    @Operation(
            summary = "fetch all users",
            description = "This endpoint fetches all users from database",
            responses = {
                    @ApiResponse(responseCode = "200",
                    description = "Success",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))
                    ),

                    @ApiResponse(responseCode = "204",
                            description = "NO content",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))
                    ),
                    @ApiResponse(responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))
                    )
            }
    )
    @GetMapping("/users")
    public ResponseEntity<CustomResponse> getUser(){
        return userService.getAllUsers();
    }

    @Hidden
    @GetMapping("/all-roles")
    public ResponseEntity<List<UserRole>> getAllRoles(){
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @Operation(
            summary = "Create roles",
            description = "This endpoint allow authenticated admin to crate roles for users",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRole.class))
                    ),
                    @ApiResponse(responseCode = "204",
                            description = "NO content",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))
                    ),
                    @ApiResponse(responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomResponse.class))
                    )
            }
    )
    @PostMapping("/create-role")
    public ResponseEntity<CustomResponse> createRole(@RequestBody UserRoleRequestDto request){
        return roleService.createRole(request);
    }

    @PostMapping("/remove-all-users-from-role/{id}")
    public ResponseEntity<CustomResponse> removeUserAllUsersFromRole(@PathVariable("id") Long roleId){
        return roleService.removeAllUserFromRole(roleId);
    }

    @PostMapping("/remove-user-from-role")
    public ResponseEntity<CustomResponse> removeSingleUserFromRole(@RequestParam(name = "userId") Long userId, @RequestParam(name = "roleId")Long roleId){
        return roleService.removeUserFromRole(userId, roleId);
    }

    @PostMapping("/assign-user-to-role")
    public ResponseEntity<CustomResponse> assignUserToRole(@RequestParam(name = "userId") Long userId, @RequestParam(name = "roleId")Long roleId){
        return roleService.assignUserToRole(userId, roleId);
    }

    @DeleteMapping("/delete-role/{id}")
    public ResponseEntity<CustomResponse> deleteRole (@PathVariable("id") Long roleId){
        return roleService.deleteRole(roleId);
    }

    @PostMapping("/add-book")
    public ResponseEntity<CustomResponse> addNewBook(@RequestBody BookRequest request){
        return bookFeignProxy.addBook(request);
    }

    @PutMapping("edit-book/{bookId}")
    public ResponseEntity<CustomResponse> editBook(@PathVariable("bookId") Long bookId, @RequestBody BookRequest request){
        return bookFeignProxy.editBook(bookId, request);
    }

    @DeleteMapping("delete-book/{bookId}")
    public ResponseEntity<CustomResponse> deleteBook(@PathVariable("bookId") Long bookId){
        return bookFeignProxy.deleteBook(bookId);
    }

}
