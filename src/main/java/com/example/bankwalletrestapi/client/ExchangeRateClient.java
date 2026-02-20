package com.example.bankwalletrestapi.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
public class ExchangeRateClient {

    @Value("${apilayer.api-key}")
    private String apiKey;

    @Value("${apilayer.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public BigDecimal convertToEur(String fromCurrency, BigDecimal amount) {
        if ("EUR".equalsIgnoreCase(fromCurrency)) {
            return amount;
        }

        Double rate = fetchRate(fromCurrency);
        return convert(amount, rate);
    }

    private Double fetchRate(String currency) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "latest")
                .queryParam("access_key", apiKey)
                .queryParam("base", "EUR")
                .queryParam("symbols", currency.toUpperCase())
                .toUriString();

        LatestRatesResponse response = restTemplate.getForObject(url, LatestRatesResponse.class);

        if (response == null || !response.isSuccess()) {
            throw new RuntimeException("Failed to fetch exchange rates");
        }

        Double rate = response.getRates().get(currency.toUpperCase());
        if (rate == null) {
            throw new RuntimeException("No rate for currency: " + currency);
        }

        return rate;
    }

    private BigDecimal convert(BigDecimal amount, Double rate) {
        return amount.divide(BigDecimal.valueOf(rate), 2, RoundingMode.HALF_UP);
    }
}