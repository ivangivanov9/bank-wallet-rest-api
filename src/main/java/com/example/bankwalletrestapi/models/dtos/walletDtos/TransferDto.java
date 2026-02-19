package com.example.bankwalletrestapi.models.dtos.walletDtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferDto {

    @NotNull(message = "Target user ID is required")
    private Long targetUserId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", inclusive = false, message = "Amount must be greater than zero")
    private BigDecimal amount;
}