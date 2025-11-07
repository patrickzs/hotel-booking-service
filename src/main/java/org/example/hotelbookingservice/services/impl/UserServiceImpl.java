package org.example.hotelbookingservice.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hotelbookingservice.dto.request.LoginRequest;
import org.example.hotelbookingservice.dto.request.RegistrationRequest;
import org.example.hotelbookingservice.dto.request.Response;
import org.example.hotelbookingservice.dto.response.BookingDTO;
import org.example.hotelbookingservice.dto.response.UserDTO;
import org.example.hotelbookingservice.entity.*;
import org.example.hotelbookingservice.enums.UserRole;
import org.example.hotelbookingservice.exception.AppException;
import org.example.hotelbookingservice.exception.ErrorCode;
import org.example.hotelbookingservice.repository.BookingRepository;
import org.example.hotelbookingservice.repository.RoleRepository;
import org.example.hotelbookingservice.repository.UserRepository;
import org.example.hotelbookingservice.repository.UserroleRepository;
import org.example.hotelbookingservice.security.JwtUtils;
import org.example.hotelbookingservice.services.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserroleRepository userroleRepository;
    private final JwtUtils jwtUtils;


    @Override
    @Transactional
    public Response registerUser(RegistrationRequest registrationRequest) {
        UserRole roleEnum = UserRole.CUSTOMER;

        if (registrationRequest.getRole() != null) {
            roleEnum = registrationRequest.getRole();
        }

        User userToSave = User.builder()
                .fName(registrationRequest.getFullname())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .phone(registrationRequest.getPhoneNumber())
                .dob(registrationRequest.getDob())
                .activate(Boolean.TRUE)
                .userRoles(new LinkedHashSet<>())
                .build();


        User savedUser = userRepository.save(userToSave);

        Role roleEntity = roleRepository.findByName(roleEnum.name())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_EXCEPTION));


        UserroleId userroleId = new UserroleId();
        userroleId.setUserId(savedUser.getId());
        userroleId.setRoleId(roleEntity.getId());

        Userrole userrole = new Userrole();
        userrole.setId(userroleId);
        userrole.setUser(savedUser);
        userrole.setRole(roleEntity);

        userroleRepository.save(userrole);

        return Response.builder()
                .status(200)
                .message("user created successfully")
                .build();
    }

    @Override
    public Response loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND_EXCEPTION));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD_EXCEPTION);
        }

        String token = jwtUtils.generateToken(user.getEmail());

        if (user.getUserRoles() == null || user.getUserRoles().isEmpty()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        //Take first role from Set<Userrole>
        Role roleEntity = user.getUserRoles().iterator().next().getRole();

        // Switch Role (String) to UserRole (Enum)
        UserRole roleEnum = UserRole.valueOf(roleEntity.getName());

        return Response.builder()
                .status(200)
                .message("user logged in successfully")
                .role(roleEnum)
                .token(token)
                .isActive(user.getActivate())
                .expirationTime("6 months")
                .build();
    }

    @Override
    public Response getAllUsers() {
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        List<UserDTO> userDTOList = modelMapper.map(users, new TypeToken<List<UserDTO>>(){}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .users(userDTOList)
                .build();
    }

    @Override
    public Response getOwnAccountDetails() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND_EXCEPTION));


        log.info("Inside getOwnAccountDetails user email is {}", email);

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return Response.builder()
                .status(200)
                .message("success")
                .user(userDTO)
                .build();
    }

    @Override
    public User getCurrentLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_EXCEPTION));
    }

    @Override
    @Transactional
    public Response updateOwnAccount(UserDTO userDTO) {
        User existingUser = getCurrentLoggedInUser();
        log.info("Inside update user");

        if (userDTO.getEmail() != null) existingUser.setEmail(userDTO.getEmail());
        if (userDTO.getFullname() != null) existingUser.setFName(userDTO.getFullname());
        if (userDTO.getPhone() != null) existingUser.setPhone(userDTO.getPhone());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        userRepository.save(existingUser);

        return Response.builder()
                .status(200)
                .message("user updated successfully")
                .build();
    }

    @Override
    @Transactional
    public Response deleteOwnAccount() {
       User user = getCurrentLoggedInUser();
       userRepository.delete(user);

        return Response.builder()
                .status(200)
                .message("user deleted successfully")
                .build();
    }

    @Override
    public Response getMyBookingHistory() {
        User user = getCurrentLoggedInUser();

        List<Booking> bookingList = bookingRepository.findByUserId(Long.valueOf(user.getId()));


        List<BookingDTO> bookingDTOList = modelMapper.map(bookingList, new TypeToken<List<BookingDTO>>(){}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .bookings(bookingDTOList)
                .build();

    }
}
