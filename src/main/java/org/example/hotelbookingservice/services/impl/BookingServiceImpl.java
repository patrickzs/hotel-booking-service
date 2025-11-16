package org.example.hotelbookingservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hotelbookingservice.dto.request.Response;
import org.example.hotelbookingservice.dto.response.BookingDTO;
import org.example.hotelbookingservice.repository.BookingRepository;
import org.example.hotelbookingservice.repository.RoomRepository;
import org.example.hotelbookingservice.services.IBookingService;
import org.example.hotelbookingservice.services.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements IBookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final IUserService userService;



    @Override
    public Response getAllBookings() {
        return null;
    }

    @Override
    public Response createBooking(BookingDTO bookingDTO) {
        return null;
    }

    @Override
    public Response findBookingByReferenceNo(String bookingReference) {
        return null;
    }

    @Override
    public Response updateBooking(BookingDTO bookingDTO) {
        return null;
    }
}
