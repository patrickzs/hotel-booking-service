package org.example.hotelbookingservice.mapper;

import org.example.hotelbookingservice.dto.request.hotel.HotelCreateRequest;
import org.example.hotelbookingservice.dto.request.hotel.HotelUpdateRequest;
import org.example.hotelbookingservice.dto.response.HotelResponse;
import org.example.hotelbookingservice.entity.Hotel;
import org.example.hotelbookingservice.entity.Hotelamenity;
import org.example.hotelbookingservice.entity.Image;
import org.example.hotelbookingservice.entity.Room;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {RoomMapper.class})
public interface HotelMapper {

    @Mapping(target = "images", source = "images", qualifiedByName = "mapHotelImages")
    @Mapping(target = "amenities", source = "hotelAmenities", qualifiedByName = "mapHotelAmenities")
    @Mapping(target = "rooms", source = "rooms")
    HotelResponse toHotelResponse(Hotel hotel);

    List<HotelResponse> toHotelResponseList(List<Hotel> hotels);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "hotelAmenities", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "policies", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    Hotel toHotel(HotelCreateRequest request);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "hotelAmenities", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "policies", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    void updateHotelFromRequest(HotelUpdateRequest request, @MappingTarget Hotel hotel);

    // 1. Lấy danh sách URL ảnh từ Set<Image>
    @Named("mapHotelImages")
    default List<String> mapHotelImages(Set<Image> images) {
        if (images == null || images.isEmpty()) return null;
        return images.stream()
                .map(Image::getPath) // Lấy đường dẫn ảnh
                .collect(Collectors.toList());
    }

    // 2. Lấy danh sách tên tiện ích từ Set<Hotelamenity>
    @Named("mapHotelAmenities")
    default List<String> mapHotelAmenities(Set<Hotelamenity> hotelAmenities) {
        if (hotelAmenities == null || hotelAmenities.isEmpty()) return null;
        return hotelAmenities.stream()
                .map(ha -> ha.getAmenity().getName()) // Truy cập Hotelamenity -> Amenity -> Name
                .collect(Collectors.toList());
    }


}