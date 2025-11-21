package org.example.hotelbookingservice.dto.request.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.hotelbookingservice.enums.BookingStatus;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingCreateRequest {

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be in the future or today")
    private LocalDate checkinDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkoutDate;

    @Min(value = 1, message = "Must have at least 1 adult")
    private Integer adultAmount;

    @Min(value = 0, message = "Children amount cannot be negative")
    private Integer childrenAmount;

    @NotNull(message = "Room ID is required")
    private Integer roomId;

    private String specialRequire;

    private BookingStatus status;
}
