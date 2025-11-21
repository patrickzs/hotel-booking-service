package org.example.hotelbookingservice.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.hotelbookingservice.dto.common.ApiResponse;
import org.example.hotelbookingservice.dto.request.user.ChangePasswordRequest;

import org.example.hotelbookingservice.dto.request.user.UserUpdateRequest;
import org.example.hotelbookingservice.dto.response.BookingResponse;
import org.example.hotelbookingservice.dto.response.UserResponse;
import org.example.hotelbookingservice.services.IUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    IUserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    ApiResponse<List<UserResponse>> getAllUser() {
        return ApiResponse.<List<UserResponse>>builder()
                .status(200)
                .message("Success")
                .data(userService.getAllUsers())
                .build();
    }

    @PutMapping("/update")
    ApiResponse<UserResponse> updateOwnAccount(@RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .status(200)
                .message("User updated successfully")
                .data(userService.updateOwnAccount(request))
                .build();
    }

    @DeleteMapping("/delete")
    ApiResponse<Void> deleteOwnAccount() {
        userService.deleteOwnAccount();
        return ApiResponse.<Void>builder()
                .status(200)
                .message("User deleted successfully")
                .build();
    }

    @GetMapping("/get-logged-in-profile-info")
    ApiResponse<UserResponse> getOwnAccountDetails() {
        return ApiResponse.<UserResponse>builder()
                .status(200)
                .message("Success")
                .data(userService.getOwnAccountDetails())
                .build();
    }

    @GetMapping("/get-user-bookings")
    ApiResponse<List<BookingResponse>> getMyBookingHistory() {
        return ApiResponse.<List<BookingResponse>>builder()
                .status(200)
                .message("Success")
                .data(userService.getMyBookingHistory())
                .build();
    }

    @PutMapping("/change-password")
    ApiResponse<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request);
        return ApiResponse.<Void>builder()
                .status(200)
                .message("Password changed successfully")
                .build();
    }


}
