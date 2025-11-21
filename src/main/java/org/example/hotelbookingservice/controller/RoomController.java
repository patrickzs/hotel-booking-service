package org.example.hotelbookingservice.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.hotelbookingservice.dto.common.ApiResponse;
import org.example.hotelbookingservice.dto.request.room.RoomCreateRequest;
import org.example.hotelbookingservice.dto.response.RoomResponse;
import org.example.hotelbookingservice.enums.RoomType;
import org.example.hotelbookingservice.services.IRoomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomController {
    IRoomService roomService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    ApiResponse<RoomResponse> addRoom(
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @ModelAttribute @Valid RoomCreateRequest roomCreateRequest
            // @ModelAttribute spring map field from from-data to DTO
            // @Valid error checking annotations in RoomDTO
    ) {
        return ApiResponse.<RoomResponse>builder()
                .status(201)
                .message("Room successfully added")
                .data(roomService.addRoom(roomCreateRequest, photo))
                .build();
    }

    @PutMapping("/update/{roomId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    ApiResponse<RoomResponse> updateRoom(
            @PathVariable Integer roomId,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @ModelAttribute RoomCreateRequest roomCreateRequest
    ) {
        roomCreateRequest.setId(roomId);
        return ApiResponse.<RoomResponse>builder()
                .status(200)
                .message("Room updated successfully")
                .data(roomService.updateRoom(roomCreateRequest, photo))
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<RoomResponse>> getAllRooms() {
        return ApiResponse.<List<RoomResponse>>builder()
                .status(200)
                .message("Success")
                .data(roomService.getAllRooms())
                .build();
    }


    @GetMapping("/{roomId}")
    ApiResponse<RoomResponse> getRoomById(@PathVariable Integer roomId) {
        return ApiResponse.<RoomResponse>builder()
                .status(200)
                .message("Success")
                .data(roomService.getRoomById(roomId))
                .build();
    }


    @DeleteMapping("/delete/{roomId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    ApiResponse<Void> deleteRoom(@PathVariable Integer roomId) {
        roomService.deleteRoom(roomId);
        return ApiResponse.<Void>builder()
                .status(200)
                .message("Room Deleted Successfully")
                .build();
    }


    @GetMapping("/all-available-rooms")
    ApiResponse<List<RoomResponse>> getAvailableRooms(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam(required = false) String roomType
    ) {
        if (checkInDate == null || checkOutDate == null) {
            return ApiResponse.<List<RoomResponse>>builder()
                    .status(400)
                    .message("Check-in and Check-out dates are required")
                    .build();
        }

        RoomType type = null;
        if (roomType != null && !roomType.isEmpty()) {
            try {
                type = RoomType.valueOf(roomType.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ApiResponse.<List<RoomResponse>>builder()
                        .status(400)
                        .message("Invalid room type")
                        .build();
            }
        }

        return ApiResponse.<List<RoomResponse>>builder()
                .status(200)
                .message("Success")
                .data(roomService.getAvailableRooms(checkInDate, checkOutDate, type))
                .build();
    }

    @GetMapping("/types")
    ApiResponse<List<RoomType>> getRoomTypes() {
        return ApiResponse.<List<RoomType>>builder()
                .status(200)
                .message("Success")
                .data(roomService.getAllRoomTypes())
                .build();
    }


    @GetMapping("/search")
    ApiResponse<List<RoomResponse>> searchRoom(@RequestParam String input) {
        return ApiResponse.<List<RoomResponse>>builder()
                .status(200)
                .message("Success")
                .data(roomService.searchRoom(input))
                .build();
    }



}


