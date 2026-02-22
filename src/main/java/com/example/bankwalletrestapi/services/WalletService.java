package com.example.bankwalletrestapi.services;

import com.example.bankwalletrestapi.external.client.ExchangeRateClient;
import com.example.bankwalletrestapi.models.dtos.userDtos.UserResponseDto;
import com.example.bankwalletrestapi.models.dtos.walletDtos.MoneyOperationDto;
import com.example.bankwalletrestapi.models.dtos.walletDtos.TransferDto;
import com.example.bankwalletrestapi.models.entities.User;
import com.example.bankwalletrestapi.models.entities.Wallet;
import com.example.bankwalletrestapi.repositories.UserRepository;
import com.example.bankwalletrestapi.repositories.WalletRepository;
import com.example.bankwalletrestapi.utils.DtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final DtoMapper dtoMapper;
    private final ExchangeRateClient exchangeRateClient;

    public UserResponseDto deposit(Long userId, MoneyOperationDto dto) {
        log.info("Processing deposit for user ID: {}, amount: {} EUR", userId, dto.getAmount());

        Wallet wallet = walletRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user ID: " + userId));

        BigDecimal amountInEur = exchangeRateClient.convertToEur(dto.getCurrency(), dto.getAmount());
        log.info("Converted {} {} to {} EUR", dto.getAmount(), dto.getCurrency(), amountInEur);

        wallet.deposit(amountInEur);

        log.info("Deposit successful. New balance: {} EUR", wallet.getBalance());

        User user = wallet.getUser();
        return dtoMapper.toUserResponse(user);
    }

    public UserResponseDto withdraw(Long userId, MoneyOperationDto dto) {
        log.info("Processing withdrawal for user ID: {}, amount: {} EUR", userId, dto.getAmount());

        Wallet wallet = walletRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user ID: " + userId));

        if (wallet.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds. Current balance: " +
                    wallet.getBalance() + " EUR, Requested: " + dto.getAmount() + " EUR");
        }

        wallet.withdraw(dto.getAmount());

        log.info("Withdrawal successful. New balance: {} EUR", wallet.getBalance());

        User user = wallet.getUser();
        return dtoMapper.toUserResponse(user);
    }

    public UserResponseDto transfer(Long sourceUserId, TransferDto dto) {
        log.info("Processing transfer from user ID: {} to user ID: {}, amount: {} EUR",
                sourceUserId, dto.getTargetUserId(), dto.getAmount());

        if (sourceUserId.equals(dto.getTargetUserId())) {
            throw new RuntimeException("Cannot transfer money to the same user");
        }

        if (!userRepository.existsById(sourceUserId)) {
            throw new RuntimeException("Source user not found with ID: " + sourceUserId);
        }
        if (!userRepository.existsById(dto.getTargetUserId())) {
            throw new RuntimeException("Target user not found with ID: " + dto.getTargetUserId());
        }

        Wallet sourceWallet;
        Wallet targetWallet;

        if (sourceUserId < dto.getTargetUserId()) {
            sourceWallet = walletRepository.findByUserIdWithLock(sourceUserId)
                    .orElseThrow(() -> new RuntimeException("Source wallet not found"));
            targetWallet = walletRepository.findByUserIdWithLock(dto.getTargetUserId())
                    .orElseThrow(() -> new RuntimeException("Target wallet not found"));
        } else {
            targetWallet = walletRepository.findByUserIdWithLock(dto.getTargetUserId())
                    .orElseThrow(() -> new RuntimeException("Target wallet not found"));
            sourceWallet = walletRepository.findByUserIdWithLock(sourceUserId)
                    .orElseThrow(() -> new RuntimeException("Source wallet not found"));
        }

        if (sourceWallet.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds for transfer. Source balance: " +
                    sourceWallet.getBalance() + " EUR");
        }

        sourceWallet.withdraw(dto.getAmount());
        targetWallet.deposit(dto.getAmount());

        log.info("Transfer successful. Source new balance: {} EUR, Target new balance: {} EUR",
                sourceWallet.getBalance(), targetWallet.getBalance());

        User sourceUser = sourceWallet.getUser();
        return dtoMapper.toUserResponse(sourceUser);
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long userId) {
        log.info("Getting balance for user ID: {}", userId);

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user ID: " + userId));

        return wallet.getBalance();
    }
}