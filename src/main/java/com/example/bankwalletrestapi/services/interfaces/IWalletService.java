package com.example.bankwalletrestapi.services.interfaces;

import com.example.bankwalletrestapi.models.dtos.userDtos.UserResponseDto;
import com.example.bankwalletrestapi.models.dtos.walletDtos.MoneyOperationDto;
import com.example.bankwalletrestapi.models.dtos.walletDtos.TransferDto;
import java.math.BigDecimal;

public interface IWalletService {

    UserResponseDto deposit(Long userId, MoneyOperationDto dto);

    UserResponseDto withdraw(Long userId, MoneyOperationDto dto);

    UserResponseDto transfer(Long sourceUserId, TransferDto dto);

    BigDecimal getBalance(Long userId);
}