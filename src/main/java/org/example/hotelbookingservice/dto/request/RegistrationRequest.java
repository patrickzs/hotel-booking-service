package org.example.hotelbookingservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.hotelbookingservice.enums.UserRole;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

    @NotBlank(message = "Fullname is required")
    private String fullname;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "LastName is required")
    private String phoneNumber;

    private UserRole role; //optional

    @NotBlank(message = "Password is required")
    private String password;

    private LocalDate dob;

}
