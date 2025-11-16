package org.example.hotelbookingservice.services;

import org.example.hotelbookingservice.dto.request.Response;
import org.example.hotelbookingservice.dto.response.BookingDTO;

public interface IBookingService {

    Response getAllBookings();
    Response createBooking(BookingDTO bookingDTO);
    Response findBookingByReferenceNo(String  bookingReference);
    Response updateBooking(BookingDTO bookingDTO);
}
