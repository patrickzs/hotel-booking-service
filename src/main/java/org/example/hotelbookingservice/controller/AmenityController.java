package org.example.hotelbookingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.hotelbookingservice.dto.common.ApiResponse;
import org.example.hotelbookingservice.dto.request.amenity.AmenityRequest;
import org.example.hotelbookingservice.dto.response.AmenityResponse;
import org.example.hotelbookingservice.services.IAmenityService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/amenities")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Amenity Management", description = "Quản lý danh mục tiện ích (Wifi, Bể bơi, Gym, Spa...)")
public class AmenityController {
    IAmenityService amenityService;

    @Operation(summary = "Lấy danh sách tất cả tiện ích", description = "API Public. Trả về danh sách toàn bộ tiện ích có trong hệ thống.")
    @GetMapping("/all")
    public ApiResponse<List<AmenityResponse>> getAllAmenities() {
        return ApiResponse.<List<AmenityResponse>>builder()
                .status(200)
                .message("Success")
                .data(amenityService.getAllAmenities())
                .build();
    }

    @Operation(summary = "Xem chi tiết tiện ích")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tiện ích không tồn tại", content = @Content)
    })
    @GetMapping("/{id}")
    public ApiResponse<AmenityResponse> getAmenityById(@PathVariable Integer id) {
        return ApiResponse.<AmenityResponse>builder()
                .status(200)
                .message("Success")
                .data(amenityService.getAmenityById(id))
                .build();
    }

    @Operation(summary = "Tạo tiện ích mới (ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Tên tiện ích đã tồn tại", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền Admin", content = @Content)
    })
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<AmenityResponse> createAmenity(@RequestBody @Valid AmenityRequest request) {
        return ApiResponse.<AmenityResponse>builder()
                .status(201)
                .message("Amenity created successfully")
                .data(amenityService.createAmenity(request))
                .build();
    }

    @Operation(summary = "Cập nhật tiện ích (ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Tên trùng lặp", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tiện ích", content = @Content)
    })
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<AmenityResponse> updateAmenity(
            @PathVariable Integer id,
            @RequestBody @Valid AmenityRequest request) {
        return ApiResponse.<AmenityResponse>builder()
                .status(200)
                .message("Amenity updated successfully")
                .data(amenityService.updateAmenity(id, request))
                .build();
    }

    @Operation(summary = "Xóa tiện ích (ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Xóa thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tiện ích", content = @Content)
    })
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<Void> deleteAmenity(@PathVariable Integer id) {
        amenityService.deleteAmenity(id);
        return ApiResponse.<Void>builder()
                .status(200)
                .message("Amenity deleted successfully")
                .build();
    }

}
