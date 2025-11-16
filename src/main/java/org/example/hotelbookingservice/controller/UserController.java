package org.example.hotelbookingservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.hotelbookingservice.dto.request.Response;
import org.example.hotelbookingservice.dto.response.UserDTO;
import org.example.hotelbookingservice.services.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private  final IUserService IUserService;

    @GetMapping("/all")
    public ResponseEntity<Response> getAllUser(){
        return ResponseEntity.ok(IUserService.getAllUsers());
    }

    @PutMapping("/update")
    public ResponseEntity<Response> updateOwnAccount(@RequestBody UserDTO userDTO){
        return ResponseEntity.ok(IUserService.updateOwnAccount(userDTO));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response> deleteOwnAccount(){
        return ResponseEntity.ok(IUserService.deleteOwnAccount());
    }

    @GetMapping("/account")
    public ResponseEntity<Response> getOwnAccountDetails(){
        return ResponseEntity.ok(IUserService.getOwnAccountDetails());
    }

    @GetMapping("/bookings")
    public ResponseEntity<Response> getMyBookingHistory(){
        return ResponseEntity.ok(IUserService.getMyBookingHistory());
    }



}
