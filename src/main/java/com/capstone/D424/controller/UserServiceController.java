package com.capstone.D424.controller;

import com.capstone.D424.dto.RegisterRequest;
import com.capstone.D424.dto.UserRequest;
import com.capstone.D424.entities.User;
import com.capstone.D424.entities.UserProfile;
import com.capstone.D424.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class UserServiceController {

    private final UserServiceImpl userService;
    @GetMapping("/user")
    public ResponseEntity<User> getUserByEmail(@RequestBody UserRequest userRequest) {
        return userService.getUserByEmail(userRequest.getEmail());
    }
    @PostMapping("/user")
    public ResponseEntity<User> saveUser(@RequestBody RegisterRequest registerRequest) {
        new User();
        return userService.saveUser(User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .profile(new UserProfile())
                .build());
    }
}