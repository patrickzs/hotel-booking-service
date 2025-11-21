package org.example.hotelbookingservice.mapper;

import org.example.hotelbookingservice.dto.request.room.RoomCreateRequest;
import org.example.hotelbookingservice.dto.response.RoomResponse;
import org.example.hotelbookingservice.entity.Image;
import org.example.hotelbookingservice.entity.Room;
import org.example.hotelbookingservice.entity.Roomamenity;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    //Response
    @Mapping(target = "roomPhotoUrl", source = "images", qualifiedByName = "mapCoverImage")
    @Mapping(target = "roomImages", source = "images", qualifiedByName = "mapImages")
    @Mapping(target = "amenities", source = "roomAmenities", qualifiedByName = "mapAmenities")
    RoomResponse toRoomResponse(Room room);


    //Request
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "roomAmenities", ignore = true)
    @Mapping(target = "bookingrooms", ignore = true)
    Room toRoom(RoomCreateRequest roomCreateRequest);

    //Update Entity from Request
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "roomAmenities", ignore = true)
    @Mapping(target = "bookingrooms", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateRoomFromRequest(RoomCreateRequest request, @MappingTarget Room room);



    List<RoomResponse> toRoomResponseList(List<Room> rooms);



    // Logic to get avatar (first image)
    @Named("mapCoverImage")
    default String mapCoverImage(Set<Image> images) {
        if (images == null || images.isEmpty()) return null;
        return images.iterator().next().getPath();
    }

    // Logic to get list of image paths
    @Named("mapImages")
    default List<String> mapImages(Set<Image> images) {
        if (images == null) return null;
        return images.stream().map(Image::getPath).collect(Collectors.toList());
    }

    // Logic to get amenity name
    @Named("mapAmenities")
    default List<String> mapAmenities(Set<Roomamenity> roomAmenities) {
        if (roomAmenities == null || roomAmenities.isEmpty()) return null;
        return roomAmenities.stream()
                .map(ra -> ra.getAmenity().getName())
                .collect(Collectors.toList());
    }


}
