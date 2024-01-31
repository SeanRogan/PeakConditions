package com.capstone.D424.service;

import com.capstone.D424.entities.User;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {
    ResponseEntity<User> getUserByEmail(String email);
    ResponseEntity<User> saveUser(User user);
    Optional<User> getUser(String name);
}
