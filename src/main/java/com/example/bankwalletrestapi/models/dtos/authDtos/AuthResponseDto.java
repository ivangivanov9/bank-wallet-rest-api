package com.example.bankwalletrestapi.models.dtos.authDtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
    private Long id;
    private String username;
    private String email;
    private String role;
}