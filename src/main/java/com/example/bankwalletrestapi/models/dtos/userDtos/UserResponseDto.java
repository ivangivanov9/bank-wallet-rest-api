package com.example.bankwalletrestapi.models.dtos.userDtos;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private BigDecimal walletBalance;
}