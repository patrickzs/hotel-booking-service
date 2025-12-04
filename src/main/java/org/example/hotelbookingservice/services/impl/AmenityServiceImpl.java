package org.example.hotelbookingservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.hotelbookingservice.dto.request.amenity.AmenityRequest;
import org.example.hotelbookingservice.dto.response.AmenityResponse;
import org.example.hotelbookingservice.entity.Amenity;
import org.example.hotelbookingservice.exception.AppException;
import org.example.hotelbookingservice.exception.ErrorCode;
import org.example.hotelbookingservice.mapper.AmenityMapper;
import org.example.hotelbookingservice.repository.AmenityRepository;
import org.example.hotelbookingservice.services.IAmenityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AmenityServiceImpl implements IAmenityService {
    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;

    @Override
    public List<AmenityResponse> getAllAmenities() {
        return amenityMapper.toAmenityResponseList(amenityRepository.findAll());
    }

    @Override
    public AmenityResponse getAmenityById(Integer id) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_EXCEPTION));
        return amenityMapper.toAmenityResponse(amenity);
    }

    @Override
    public AmenityResponse createAmenity(AmenityRequest request) {
        if (amenityRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.AMENITY_EXISTED);
        }

        Amenity amenity = amenityMapper.toAmenity(request);
        Amenity savedAmenity = amenityRepository.save(amenity);

        return amenityMapper.toAmenityResponse(savedAmenity);
    }

    @Override
    public AmenityResponse updateAmenity(Integer id, AmenityRequest request) {
       Amenity existingAmenity = amenityRepository.findById(id)
               .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_EXCEPTION));

        // Check for duplicate names if the user changes their name
        // (except if the name is the same as the old name)
        if (!existingAmenity.getName().equalsIgnoreCase(request.getName())
                && amenityRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        amenityMapper.updateAmenityFromRequest(request, existingAmenity);

        return amenityMapper.toAmenityResponse(amenityRepository.save(existingAmenity));
    }

    @Override
    public void deleteAmenity(Integer id) {
        if (!amenityRepository.existsById(id)) {
            throw new AppException(ErrorCode.NOT_FOUND_EXCEPTION);
        }

        amenityRepository.deleteById(id);
    }
}
