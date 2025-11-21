package org.example.hotelbookingservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.hotelbookingservice.enums.BookingStatus;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingResponse {
    private Integer id;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private Integer adultAmount;
    private Integer childrenAmount;
    private Float totalPrice;
    private Float refund;
    private String bookingReference;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String cancelReason;
    private BookingStatus status;
    private String specialRequire;
    private LocalDate createAt;

    private UserResponse user;
    private RoomResponse room;
}
