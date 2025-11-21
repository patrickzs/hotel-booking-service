package org.example.hotelbookingservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hotelbookingservice.dto.request.booking.BookingCreateRequest;
import org.example.hotelbookingservice.dto.request.booking.BookingUpdateRequest;
import org.example.hotelbookingservice.dto.response.BookingResponse;
import org.example.hotelbookingservice.entity.Booking;
import org.example.hotelbookingservice.entity.Bookingroom;
import org.example.hotelbookingservice.entity.Room;
import org.example.hotelbookingservice.entity.User;
import org.example.hotelbookingservice.enums.BookingStatus;
import org.example.hotelbookingservice.exception.AppException;
import org.example.hotelbookingservice.exception.ErrorCode;
import org.example.hotelbookingservice.exception.InvalidBookingStateAndDateException;
import org.example.hotelbookingservice.exception.NotFoundException;
import org.example.hotelbookingservice.mapper.BookingMapper;
import org.example.hotelbookingservice.repository.BookingRepository;
import org.example.hotelbookingservice.repository.BookingRoomRepository;
import org.example.hotelbookingservice.repository.RoomRepository;
import org.example.hotelbookingservice.services.BookingCodeGenerator;
import org.example.hotelbookingservice.services.IBookingService;
import org.example.hotelbookingservice.services.IUserService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements IBookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final BookingMapper bookingMapper;
    private final IUserService userService;
    private final BookingCodeGenerator bookingCodeGenerator;
    private final BookingRoomRepository bookingRoomRepository;


    @Override
    public List<BookingResponse> getAllBookings() {
        List<Booking> bookingList =bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        return bookingMapper.toBookingResponseList(bookingList);
    }

    @Override
    public BookingResponse createBooking(BookingCreateRequest bookingRequest) {
        User currentUser = userService.getCurrentLoggedInUser();

        Room room = roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND_ROOM));


        //validation: Ensure the check-in date is not before today
        if (bookingRequest.getCheckinDate().isBefore(LocalDate.now())){
            throw new InvalidBookingStateAndDateException("check in date cannot be before today ");
        }

        //validation: Ensure the check-out date is not before check in date
        if (bookingRequest.getCheckinDate().isBefore(bookingRequest.getCheckinDate())){
            throw new InvalidBookingStateAndDateException("check out date cannot be before check in date ");
        }

        //validation: Ensure the check-in date is not same as check out date
        if (bookingRequest.getCheckinDate().isEqual(bookingRequest.getCheckoutDate())){
            throw new InvalidBookingStateAndDateException("check in date cannot be equal to check out date ");
        }

        //validate room availability
        boolean isAvailable = bookingRepository.isRoomAvailable(Long.valueOf(room.getId()), bookingRequest.getCheckinDate(), bookingRequest.getCheckoutDate());
        if (!isAvailable) {
            throw new InvalidBookingStateAndDateException("Room is not available for the selected date ranges");
        }

        //calculate the total price needed to pay for the stay
        BigDecimal totalPrice = calculateTotalPrice(room, bookingRequest);
        String bookingReference = bookingCodeGenerator.generateBookingReference();

        //create and save the booking entity
        Booking booking = new Booking();
        booking.setUser(currentUser);
        booking.setCheckinDate(bookingRequest.getCheckinDate());
        booking.setCheckoutDate(bookingRequest.getCheckoutDate());
        booking.setAdultAmount(bookingRequest.getAdultAmount());
        booking.setChildrenAmount(bookingRequest.getChildrenAmount());
        booking.setCustomerName(currentUser.getFullName());
        booking.setTotalPrice(totalPrice.floatValue());
        booking.setBookingReference(bookingReference);
        booking.setStatus(BookingStatus.BOOKED);
        booking.setCreateAt(LocalDate.now());
        booking.setSpecialRequire(bookingRequest.getSpecialRequire());

        //save booking for hibernate generate ID
        Booking savedBooking = bookingRepository.save(booking);

        //Create and save bookingroom
        Bookingroom bookingRoom = new Bookingroom();
        bookingRoom.setBooking(savedBooking);
        bookingRoom.setRoom(room);
        bookingRoomRepository.save(bookingRoom);

        return bookingMapper.toBookingResponse(savedBooking);
    }

    @Override
    public BookingResponse findBookingByReferenceNo(String bookingReference) {
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(()-> new NotFoundException("Booking with reference No: " + bookingReference + "Not found"));

        return bookingMapper.toBookingResponse(booking);
    }

    @Override
    public BookingResponse updateBooking(Integer bookingId, BookingUpdateRequest bookingRequest) {
        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(()-> new NotFoundException("Booking Not Found"));

        if (bookingRequest.getStatus() != null) {
            existingBooking.setStatus(bookingRequest.getStatus());
        }
        if (bookingRequest.getCancelReason() != null) {
            existingBooking.setCancelReason(bookingRequest.getCancelReason());
        }

        Booking savedBooking = bookingRepository.save(existingBooking);
        return bookingMapper.toBookingResponse(savedBooking);
    }

    @Override
    public void cancelBooking(Integer bookingId) {

        //1. Find booking id
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking Not Found"));

        //2.get user login
        User currentUser = userService.getCurrentLoggedInUser();

        // 3. SECURITY CHECK
        //Allow cancellation if ADMIN OR owner (User ID matches)
        boolean isAdmin = currentUser.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRole().getName().equals("ADMIN"));

        if (!isAdmin && !booking.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 4. VALIDATION:
        // Completed or canceled orders cannot be canceled.
        if (booking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new InvalidBookingStateAndDateException("Cannot cancel a booking that has already been completed.");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingStateAndDateException("Booking is already cancelled.");
        }

        // 5. Execute cancellation
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    private BigDecimal calculateTotalPrice(Room room, BookingCreateRequest bookingCreateRequest){
        BigDecimal pricePerNight = room.getPrice();
        long days = ChronoUnit.DAYS.between(bookingCreateRequest.getCheckinDate(), bookingCreateRequest.getCheckoutDate());
        return pricePerNight.multiply(BigDecimal.valueOf(days));
    }
}
