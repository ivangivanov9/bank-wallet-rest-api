package com.example.bankwalletrestapi.controllers;

import com.example.bankwalletrestapi.models.dtos.userDtos.UserResponseDto;
import com.example.bankwalletrestapi.models.dtos.walletDtos.MoneyOperationDto;
import com.example.bankwalletrestapi.models.dtos.walletDtos.TransferDto;
import com.example.bankwalletrestapi.services.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/{userId}/deposit")
    public ResponseEntity<UserResponseDto> deposit(
            @PathVariable Long userId,
            @Valid @RequestBody MoneyOperationDto dto) {
        log.info("REST request to deposit {} EUR for user: {}", dto.getAmount(), userId);
        UserResponseDto response = walletService.deposit(userId, dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<UserResponseDto> withdraw(
            @PathVariable Long userId,
            @Valid @RequestBody MoneyOperationDto dto) {
        log.info("REST request to withdraw {} EUR for user: {}", dto.getAmount(), userId);
        UserResponseDto response = walletService.withdraw(userId, dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<UserResponseDto> transfer(
            @RequestParam Long from,
            @Valid @RequestBody TransferDto dto) {
        log.info("REST request to transfer {} EUR from user: {} to user: {}",
                dto.getAmount(), from, dto.getTargetUserId());
        UserResponseDto response = walletService.transfer(from, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long userId) {
        log.info("REST request to get balance for user: {}", userId);
        BigDecimal balance = walletService.getBalance(userId);
        return ResponseEntity.ok(balance);
    }
}
