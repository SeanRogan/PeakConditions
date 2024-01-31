package com.capstone.D424.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
@AllArgsConstructor
@Builder
@Data
@RequiredArgsConstructor
public class UserRequest {
    String email;
}
