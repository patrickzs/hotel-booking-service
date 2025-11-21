package org.example.hotelbookingservice.repository;

import org.example.hotelbookingservice.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {
}
