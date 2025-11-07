package org.example.hotelbookingservice.dto.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link org.example.hotelbookingservice.entity.Role}
 */
@Value
public class RoleDTO implements Serializable {
    Integer id;
    @NotNull
    @Size(max = 255)
    String name;
}