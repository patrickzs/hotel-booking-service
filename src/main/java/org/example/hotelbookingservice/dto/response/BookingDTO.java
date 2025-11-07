package org.example.hotelbookingservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.example.hotelbookingservice.enums.BookingStatus;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link org.example.hotelbookingservice.entity.Booking}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingDTO implements Serializable {
    Integer id;


    String column;

    LocalDate checkinDate;

    LocalDate checkoutDate;

    Integer adultAmount;


    String customerName;

    String cancelReason;


    BookingStatus status;

    String specialRequire;

    LocalDate createAt;

    Integer childrenAmount;

    Float totalPrice;

}