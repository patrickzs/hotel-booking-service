package org.example.hotelbookingservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hotelbookingservice.dto.request.LoginRequest;
import org.example.hotelbookingservice.dto.request.RegistrationRequest;
import org.example.hotelbookingservice.dto.request.Response;
import org.example.hotelbookingservice.services.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IUserService IUserService;

    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(@RequestBody @Valid RegistrationRequest request){
        return ResponseEntity.ok(IUserService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<Response> loginUser(@RequestBody @Valid LoginRequest request){
        return ResponseEntity.ok(IUserService.loginUser(request));
    }
}
