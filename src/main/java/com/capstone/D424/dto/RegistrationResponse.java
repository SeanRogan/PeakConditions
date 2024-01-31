package com.capstone.D424.dto;


import com.capstone.D424.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@RequiredArgsConstructor
public class RegistrationResponse {

    AuthenticationResponse authenticationResponse;
    boolean success;
}
