package org.example.hotelbookingservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hotelbookingservice.dto.request.Response;
import org.example.hotelbookingservice.dto.response.RoomDTO;
import org.example.hotelbookingservice.enums.RoomType;
import org.example.hotelbookingservice.services.IRoomService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements IRoomService {
    @Override
    public Response addRoom(RoomDTO roomDTO, MultipartFile imageFile) {
        return null;
    }

    @Override
    public Response updateRoom(RoomDTO roomDTO, MultipartFile imageFile) {
        return null;
    }

    @Override
    public Response getAllRooms() {
        return null;
    }

    @Override
    public Response getRoomById(Long id) {
        return null;
    }

    @Override
    public Response deleteRoom(Long id) {
        return null;
    }

    @Override
    public Response getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, RoomType roomType) {
        return null;
    }

    @Override
    public List<RoomType> getAllRoomTypes() {
        return List.of();
    }

    @Override
    public Response searchRoom(String input) {
        return null;
    }
}
