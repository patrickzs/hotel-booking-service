package org.example.hotelbookingservice.mapper;

import org.example.hotelbookingservice.dto.request.booking.BookingCreateRequest;
import org.example.hotelbookingservice.dto.response.BookingResponse;
import org.example.hotelbookingservice.dto.response.HotelResponse;

import org.example.hotelbookingservice.dto.response.RoomResponse;
import org.example.hotelbookingservice.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class BookingMapper {

    @Autowired
    protected RoomMapper roomMapper;

    @Autowired
    protected HotelMapper hotelMapper;

    // 1. Entity -> Response (Output)
    @Mapping(target = "customerEmail", source = "user.email")
    @Mapping(target = "customerPhone", source = "user.phone")
    @Mapping(target = "hotel", expression = "java(mapHotel(booking))")
    @Mapping(target = "rooms", expression = "java(mapBookedRooms(booking))")
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


    // 1. Map Hotel nhưng loại bỏ danh sách toàn bộ phòng (set rooms = null)
    protected HotelResponse mapHotel(Booking booking) {
        if (booking.getBookingrooms() != null && !booking.getBookingrooms().isEmpty()) {
            // Lấy Hotel từ phòng đầu tiên trong danh sách booking
            var hotel = booking.getBookingrooms().iterator().next().getRoom().getHotel();

            // Map sang Response
            HotelResponse response = hotelMapper.toHotelResponse(hotel);

            // QUAN TRỌNG: Set list rooms của Hotel thành null để không hiển thị trong JSON
            response.setRooms(null);

            return response;
        }
        return null;
    }

    // 2. Map danh sách các phòng ĐÃ ĐẶT (từ bảng Bookingroom)
    protected List<RoomResponse> mapBookedRooms(Booking booking) {
        if (booking.getBookingrooms() == null || booking.getBookingrooms().isEmpty()) {
            return null;
        }
        // Duyệt qua set Bookingroom -> lấy Room -> dùng roomMapper chuyển sang RoomResponse
        return booking.getBookingrooms().stream()
                .map(br -> roomMapper.toRoomResponse(br.getRoom()))
                .collect(Collectors.toList());
    }

}
