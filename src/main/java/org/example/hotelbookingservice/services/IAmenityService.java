package org.example.hotelbookingservice.services;

import org.example.hotelbookingservice.dto.request.amenity.AmenityRequest;
import org.example.hotelbookingservice.dto.response.AmenityResponse;
import org.example.hotelbookingservice.entity.Amenity;

import java.util.List;

public interface IAmenityService {

    List<AmenityResponse> getAllAmenities();
    AmenityResponse getAmenityById(Integer id);
    AmenityResponse createAmenity(AmenityRequest request);
    AmenityResponse updateAmenity(Integer id, AmenityRequest request);
    void deleteAmenity(Integer id);
}
