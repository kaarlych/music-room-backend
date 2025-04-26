package com.example.musicroom.controller;

import com.example.musicroom.dto.LoginRequest;
import com.example.musicroom.dto.LoginResponse;
import com.example.musicroom.security.JwtService;
import com.example.musicroom.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final RoomService roomService;
    private final JwtService jwtService;

    public AuthController(RoomService roomService, JwtService jwtService) {
        this.roomService = roomService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if (roomService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword())) {
            UserDetails userDetails = new User(loginRequest.getUsername(), loginRequest.getPassword(), new ArrayList<>());
            String token = jwtService.generateJwtToken(userDetails);

            return ResponseEntity.ok(new LoginResponse(token));
        }

        return ResponseEntity.status(401).body("Invalid username or password");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // JWT is stateless, so we don't need to do anything specific for logout
        return ResponseEntity.ok("Logged out successfully");
    }
}