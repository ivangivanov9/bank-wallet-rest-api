package com.example.bankwalletrestapi.utils;

import com.example.bankwalletrestapi.models.dtos.userDtos.UserResponseDto;
import com.example.bankwalletrestapi.models.entities.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DtoMapper {

    public UserResponseDto toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .walletBalance(user.getWallet() != null ? user.getWallet().getBalance() : BigDecimal.ZERO)
                .build();
    }

}