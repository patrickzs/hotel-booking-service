package org.example.hotelbookingservice.dto.response;

import jakarta.validation.constraints.*;
import lombok.Value;

@Value
public class HotelResponse {
    Integer id;
    @NotBlank(message = "Hotel name is required")
    @Size(max = 255, message = "Hotel name must be less than 255 characters")
    String name;

    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must be less than 255 characters")
    String location;

    @NotBlank(message = "Description is required")
    String description;

    @NotNull(message = "Star rating is required")
    @Min(value = 1, message = "Star rating must be at least 1")
    @Max(value = 5, message = "Star rating must be at most 5")
    Integer starRating;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must be less than 255 characters")
    String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\d{10,12}$", message = "Phone number must be between 10 and 12 digits")
    String phone;

    @NotNull(message = "Active status is required")
    Boolean isActive;

    @NotBlank(message = "Contact name is required")
    @Size(max = 255, message = "Contact name must be less than 255 characters")
    String contactName;

    @NotBlank(message = "Contact phone is required")
    @Pattern(regexp = "^\\d{10,12}$", message = "Contact phone must be between 10 and 12 digits")
    String contactPhone;
}
