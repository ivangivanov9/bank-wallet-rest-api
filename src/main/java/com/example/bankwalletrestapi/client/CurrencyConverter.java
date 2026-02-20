package com.example.bankwalletrestapi.client;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CurrencyConverter {

    private static final int DECIMALS = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public BigDecimal toEur(BigDecimal amount, Double exchangeRate) {
        return amount.divide(BigDecimal.valueOf(exchangeRate), DECIMALS, ROUNDING_MODE);
    }
}