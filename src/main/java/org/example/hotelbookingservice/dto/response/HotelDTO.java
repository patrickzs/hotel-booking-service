package org.example.hotelbookingservice.dto.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link org.example.hotelbookingservice.entity.Hotel}
 */
@Value
public class HotelDTO implements Serializable {
    Integer id;
    @NotNull
    @Size(max = 255)
    String name;
    @NotNull
    @Size(max = 255)
    String location;
    @NotNull
    @Size(max = 255)
    String description;
    @NotNull
    Integer starRating;
    @NotNull
    @Size(max = 255)
    String email;
    @NotNull
    @Size(max = 255)
    String phone;
    @NotNull
    Boolean isActive;
    @NotNull
    @Size(max = 255)
    String contactName;
    @NotNull
    @Size(max = 255)
    String contactPhone;
}