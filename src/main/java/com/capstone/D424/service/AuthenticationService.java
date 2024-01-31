package com.capstone.D424.service;

import com.capstone.D424.dto.AuthenticationRequest;
import com.capstone.D424.dto.AuthenticationResponse;
import com.capstone.D424.dto.RegisterRequest;
import com.capstone.D424.dto.RegistrationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    RegistrationResponse register(RegisterRequest request);
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

}
