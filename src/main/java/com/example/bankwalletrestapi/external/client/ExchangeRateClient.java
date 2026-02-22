package com.example.bankwalletrestapi.external.client;

import com.example.bankwalletrestapi.external.helpers.CurrencyConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeRateClient {

    private final ExchangeRateApi api;
    private final CurrencyConverter converter;

    public BigDecimal convertToEur(String fromCurrency, BigDecimal amount) {
        if (isEuro(fromCurrency)) {
            return amount;
        }

        Double rate = api.fetchRateFor(fromCurrency);
        BigDecimal converted = converter.toEur(amount, rate);

        log.info("Converted {} {} to {} EUR", amount, fromCurrency, converted);
        return converted;
    }

    private boolean isEuro(String currency) {
        return "EUR".equalsIgnoreCase(currency);
    }
}