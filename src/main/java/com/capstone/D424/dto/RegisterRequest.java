package com.capstone.D424.dto;

import com.capstone.D424.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
@AllArgsConstructor
@Data
@Builder
@RequiredArgsConstructor
public class RegisterRequest {
    private String email;
    private String username;
    private String password;
    private Role role;
}
