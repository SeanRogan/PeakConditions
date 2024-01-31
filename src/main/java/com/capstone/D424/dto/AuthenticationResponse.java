package com.capstone.D424.dto;

import com.capstone.D424.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@RequiredArgsConstructor
public class AuthenticationResponse {
    private User user;
}
