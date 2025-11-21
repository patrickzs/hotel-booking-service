package org.example.hotelbookingservice.dto.response;

import lombok.Builder;
import lombok.Data;
import org.example.hotelbookingservice.enums.UserRole;

@Data
@Builder
public class LoginResponse {
    private String token;
    private UserRole role;
    private String expirationTime;
    private Boolean isActive;
}
