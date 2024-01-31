package com.capstone.D424.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor

public class AuthenticationRequest {
    private String email;
    private String password;
}
