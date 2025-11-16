package org.example.hotelbookingservice.services;

import org.example.hotelbookingservice.dto.request.LoginRequest;
import org.example.hotelbookingservice.dto.request.RegistrationRequest;
import org.example.hotelbookingservice.dto.request.Response;
import org.example.hotelbookingservice.dto.response.UserDTO;
import org.example.hotelbookingservice.entity.User;

public interface IUserService {

    Response registerUser(RegistrationRequest registrationRequest);
    Response loginUser(LoginRequest loginRequest);
    Response getAllUsers();
    Response getOwnAccountDetails();
    User getCurrentLoggedInUser();
    Response updateOwnAccount(UserDTO userDTO);
    Response deleteOwnAccount();
    Response getMyBookingHistory();
}
