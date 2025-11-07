package org.example.hotelbookingservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for {@link org.example.hotelbookingservice.entity.User}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO implements Serializable {
    Integer id;
    @NotNull
    @Size(max = 255)
    String fullname;
    @NotNull
    @Size(max = 255)
    String email;
    @NotNull
    String password;
    @Size(max = 255)
    String phone;
    @NotNull
    LocalDate dob;
    @NotNull
    Boolean activate;
    LocalDateTime createdAt;


}