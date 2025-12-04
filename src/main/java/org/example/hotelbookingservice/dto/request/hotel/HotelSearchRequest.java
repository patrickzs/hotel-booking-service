package org.example.hotelbookingservice.dto.request.hotel;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HotelSearchRequest {
    private String location;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer adultAmount;
    private Integer childrenAmount;
    private Integer roomQuantity;
    private Double minPrice;
    private Double maxPrice;
}
