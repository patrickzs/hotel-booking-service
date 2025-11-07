package org.example.hotelbookingservice.dto.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link org.example.hotelbookingservice.entity.Review}
 */
@Value
public class ReviewDTO implements Serializable {
    Integer id;
    @NotNull
    @Size(max = 255)
    String description;
    @NotNull
    Float point;
    @NotNull
    LocalDate createAt;
}