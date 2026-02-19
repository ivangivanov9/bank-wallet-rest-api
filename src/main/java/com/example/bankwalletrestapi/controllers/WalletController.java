package com.example.bankwalletrestapi.controllers;

import com.example.bankwalletrestapi.models.dtos.userDtos.UserResponseDto;
import com.example.bankwalletrestapi.models.dtos.walletDtos.MoneyOperationDto;
import com.example.bankwalletrestapi.services.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
