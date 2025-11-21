package org.example.hotelbookingservice.dto.request.booking;

import lombok.Data;
import org.example.hotelbookingservice.enums.BookingStatus;

@Data
public class BookingUpdateRequest {
    private BookingStatus status;
    private String cancelReason;
}
