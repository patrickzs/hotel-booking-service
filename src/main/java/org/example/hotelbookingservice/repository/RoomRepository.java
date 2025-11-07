package org.example.hotelbookingservice.repository;

import org.example.hotelbookingservice.entity.Room;
import org.example.hotelbookingservice.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    @Query("""
            
            SELECT r FROM Room r
            WHERE
                r.id NOT IN (
                    SELECT br.room.id
                    FROM Booking b
                    JOIN b.bookingrooms br
                    WHERE :checkInDate <= b.checkoutDate
                    AND :checkOutDate >= b.checkinDate
                    AND b.status IN ('BOOKED', 'CHECKED_IN')
                )
                AND (:roomType IS NULL OR r.type = :roomType)
            """)
    List<Room> findAvailableRooms(@Param("checkinDate") LocalDate checkinDate, @Param("checkoutDate") LocalDate checkoutDate, @Param("roomType") RoomType roomType);


    @Query("""
               SELECT r FROM Room r
                 WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :searchParam, '%'))
                    OR LOWER(r.type) LIKE LOWER(CONCAT('%', :searchParam, '%'))
                    OR CAST(r.price AS string) LIKE %:searchParam%
                    OR CAST(r.capacity AS string) LIKE %:searchParam%
                    OR LOWER(r.description) LIKE LOWER(CONCAT('%', :searchParam, '%'))
            """)
    List<Room> searchRooms(@Param("searchParam") String searchParam);


}