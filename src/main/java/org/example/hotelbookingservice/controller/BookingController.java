package org.example.hotelbookingservice.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.hotelbookingservice.dto.common.ApiResponse;
import org.example.hotelbookingservice.dto.request.booking.BookingCreateRequest;
import org.example.hotelbookingservice.dto.request.booking.BookingUpdateRequest;
import org.example.hotelbookingservice.dto.response.BookingResponse;
import org.example.hotelbookingservice.services.IBookingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {
    IBookingService bookingService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    ApiResponse<List<BookingResponse>> getAllBookings() {
        return ApiResponse.<List<BookingResponse>>builder()
                .status(200)
                .message("Success")
                .data(bookingService.getAllBookings())
                .build();
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('CUSTOMER') ")
    ApiResponse<BookingResponse> createBooking(@RequestBody @Valid BookingCreateRequest bookingRequest) {
        return ApiResponse.<BookingResponse>builder()
                .status(201)
                .message("Booking successful")
                .data(bookingService.createBooking(bookingRequest))
                .build();
    }

    @GetMapping("/get-by-confirmation-code/{confirmationCode}")
    ApiResponse<BookingResponse> getBookingByConfirmationCode(@PathVariable String confirmationCode) {
        return ApiResponse.<BookingResponse>builder()
                .status(200)
                .message("Success")
                .data(bookingService.findBookingByReferenceNo(confirmationCode))
                .build();
    }

    @PutMapping("/update/{bookingId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    ApiResponse<BookingResponse> updateBooking(
            @PathVariable Integer bookingId,
            @RequestBody BookingUpdateRequest bookingRequest) {
        return ApiResponse.<BookingResponse>builder()
                .status(200)
                .message("Booking Updated Successfully")
                .data(bookingService.updateBooking(bookingId,bookingRequest))
                .build();
    }

    @DeleteMapping("/cancel/{bookingId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('CUSTOMER')")
    ApiResponse<Void> cancelBooking(@PathVariable Integer bookingId) {
        bookingService.cancelBooking(bookingId);
        return ApiResponse.<Void>builder()
                .status(200)
                .message("Booking cancelled successfully")
                .build();
    }

}
