package org.example.hotelbookingservice.mapper;

import org.example.hotelbookingservice.dto.request.booking.BookingCreateRequest;
import org.example.hotelbookingservice.dto.response.BookingResponse;
import org.example.hotelbookingservice.dto.response.RoomResponse;
import org.example.hotelbookingservice.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class BookingMapper {

    @Autowired
    protected RoomMapper roomMapper;

    // 1. Entity -> Response (Output)
    @Mapping(target = "customerEmail", source = "user.email")
    @Mapping(target = "customerPhone", source = "user.phone")
    @Mapping(target = "room", expression = "java(mapRoom(booking))")
    public abstract BookingResponse toBookingResponse(Booking booking);

    // 2. Request -> Entity (Input)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "bookingReference", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "bookingrooms", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "customerName", ignore = true)
    @Mapping(target = "refund", ignore = true)
    @Mapping(target = "cancelReason", ignore = true)
    public abstract Booking toBooking(BookingCreateRequest request);


    public abstract List<BookingResponse> toBookingResponseList(List<Booking> bookings);

    // BookingRoom (Entity) to RoomResponse
    protected RoomResponse mapRoom(Booking booking) {
        if (booking.getBookingrooms() != null && !booking.getBookingrooms().isEmpty()) {
            return roomMapper.toRoomResponse(booking.getBookingrooms().iterator().next().getRoom());
        }
        return null;
    }

}
