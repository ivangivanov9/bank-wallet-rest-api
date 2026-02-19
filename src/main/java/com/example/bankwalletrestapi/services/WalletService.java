package com.example.bankwalletrestapi.services;

import com.example.bankwalletrestapi.models.dtos.walletDtos.MoneyOperationDto;
import com.example.bankwalletrestapi.models.dtos.walletDtos.TransferDto;
import com.example.bankwalletrestapi.models.dtos.userDtos.UserResponseDto;
import com.example.bankwalletrestapi.repositories.UserRepository;
import com.example.bankwalletrestapi.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public UserResponseDto deposit(Long userId, MoneyOperationDto dto) {
        // TODO
        return null;
    }

    public UserResponseDto withdraw(Long userId, MoneyOperationDto dto) {
        // TODO
        return null;
    }

    public UserResponseDto transfer(Long sourceUserId, TransferDto dto) {
        // TODO
        return null;
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long userId) {
        // TODO
        return null;
    }
}