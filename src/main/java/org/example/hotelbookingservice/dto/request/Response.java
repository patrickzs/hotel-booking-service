package org.example.hotelbookingservice.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.hotelbookingservice.dto.response.BookingDTO;
import org.example.hotelbookingservice.dto.response.RoomDTO;
import org.example.hotelbookingservice.dto.response.UserDTO;
import org.example.hotelbookingservice.enums.UserRole;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Response {
    //generic
    private int status;
    String message;

    //for login
    private String token;
    private UserRole role;
    private Boolean isActive;
    private String expirationTime;

    //user data output
    private UserDTO user;
    private List<UserDTO> users;

    //Booking data output
    private BookingDTO booking;
    private List<BookingDTO> bookings;

    //Room data output
    private RoomDTO room;
    private List<RoomDTO> rooms;

}