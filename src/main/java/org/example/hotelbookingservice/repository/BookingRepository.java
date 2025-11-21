package org.example.hotelbookingservice.repository;

import org.example.hotelbookingservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserId(Long userId); // Fetch all bookings for a specific user


    Optional<Booking> findByBookingReference(String bookingReference);


    @Query("""
               SELECT CASE WHEN COUNT(b) = 0 THEN true ELSE false END
                FROM Booking b
                JOIN b.bookingrooms br
                WHERE br.room.id = :roomId
                  AND :checkInDate <= b.checkoutDate
                  AND :checkOutDate >= b.checkinDate
                  AND b.status IN ('BOOKED', 'CHECKED_IN')
            """)
    boolean isRoomAvailable(@Param("roomId") Long roomId,
                            @Param("checkInDate") LocalDate checkinDate,
                            @Param("checkOutDate") LocalDate checkoutDate);
}