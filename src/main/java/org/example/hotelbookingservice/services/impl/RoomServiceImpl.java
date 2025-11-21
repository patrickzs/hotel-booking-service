package org.example.hotelbookingservice.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.example.hotelbookingservice.dto.request.room.RoomCreateRequest;
import org.example.hotelbookingservice.dto.response.RoomResponse;
import org.example.hotelbookingservice.mapper.RoomMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.hotelbookingservice.entity.Hotel;
import org.example.hotelbookingservice.entity.Image;
import org.example.hotelbookingservice.entity.Room;
import org.example.hotelbookingservice.entity.User;
import org.example.hotelbookingservice.enums.RoomType;
import org.example.hotelbookingservice.exception.AppException;
import org.example.hotelbookingservice.exception.ErrorCode;
import org.example.hotelbookingservice.exception.InvalidBookingStateAndDateException;
import org.example.hotelbookingservice.repository.ImageRepository;
import org.example.hotelbookingservice.repository.RoomRepository;
import org.example.hotelbookingservice.services.IRoomService;
import org.example.hotelbookingservice.services.IUserService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements IRoomService {
    private final RoomRepository roomRepository;
    private final ImageRepository imageRepository;
    private final RoomMapper roomMapper;
    private final IUserService userService;


    private final Cloudinary cloudinary;

    @Override
    public RoomResponse addRoom(RoomCreateRequest roomCreateRequest, MultipartFile imageFile) {

        //Get current user (admin)
        User currentUser = userService.getCurrentLoggedInUser();

        //Check admin has hotel
        if (currentUser.getHotels() == null || currentUser.getHotels().isEmpty()) {
            throw new AppException(ErrorCode.NOT_FOUND_EXCEPTION);
        }
        //Get Admin's first hotel to assign to room
        Hotel currentHotel = currentUser.getHotels().iterator().next();

        if (roomRepository.existsByRoomNumber(roomCreateRequest.getRoomNumber())) {
            throw new AppException(ErrorCode.ROOM_ALREADY_EXISTS);
        }

        //Map DTO to Entity
        Room roomToSave = roomMapper.toRoom(roomCreateRequest);

        //Assign Hotel to room
        roomToSave.setHotel(currentHotel);


        Room savedRoom = roomRepository.save(roomToSave);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = saveImageToCloud(imageFile);
            Image image = new Image();
            image.setPath(imageUrl);
            image.setRoom(savedRoom);
            image.setHotel(savedRoom.getHotel());
            imageRepository.save(image);
        }
        return roomMapper.toRoomResponse(savedRoom);
    }

    @Override
    public RoomResponse updateRoom(RoomCreateRequest roomCreateRequest, MultipartFile imageFile) {
        Room existingRoom = roomRepository.findById(roomCreateRequest.getId())
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND_ROOM));

        if (imageFile != null && !imageFile.isEmpty()){
            String imagePath = saveImageToCloud(imageFile);

            Image image = new Image();
            image.setPath(imagePath);
            image.setRoom(existingRoom);
            image.setHotel(existingRoom.getHotel());

            imageRepository.save(image);
        }

        roomMapper.updateRoomFromRequest(roomCreateRequest, existingRoom);

        Room savedRoom = roomRepository.save(existingRoom);

        return roomMapper.toRoomResponse(existingRoom);
    }

    @Override
    public List<RoomResponse> getAllRooms() {
        List<Room> roomList = roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        return roomMapper.toRoomResponseList(roomList);
    }




    @Override
    public RoomResponse getRoomById(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND_ROOM));

        return roomMapper.toRoomResponse(room);
    }




    @Override
    public void deleteRoom(Integer id) {
        if (!roomRepository.existsById(id)){
            throw new AppException(ErrorCode.NOT_FOUND_ROOM);
        }
        roomRepository.deleteById(id);
    }

    @Override
    public List<RoomResponse> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, RoomType roomType) {
        //validation: Ensure the check-in date is not before today
        if (checkInDate.isBefore(LocalDate.now())){
            throw new InvalidBookingStateAndDateException("check in date cannot be before today ");
        }

        //validation: Ensure the check-out date is not before check in date
        if (checkOutDate.isBefore(checkInDate)){
            throw new InvalidBookingStateAndDateException("check out date cannot be before check in date ");
        }

        //validation: Ensure the check-in date is not same as check out date
        if (checkInDate.isEqual(checkOutDate)){
            throw new InvalidBookingStateAndDateException("check in date cannot be equal to check out date ");
        }

        //Get the list of available rooms from DB
        List<Room> roomList = roomRepository.findAvailableRooms(checkInDate, checkOutDate, roomType);

        return roomMapper.toRoomResponseList(roomList);
    }

    @Override
    public List<RoomType> getAllRoomTypes() {
        return Arrays.asList(RoomType.values());
    }

    @Override
    public List<RoomResponse> searchRoom(String input) {
        List<Room> roomList = roomRepository.searchRooms(input);

        return roomMapper.toRoomResponseList(roomList);
    }

    //upload image to cloud
    private String saveImageToCloud(MultipartFile imageFile) {
        try {
            // Upload file to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.emptyMap());
            // Get URL
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            log.error("Error uploading file to Cloudinary", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }
}
