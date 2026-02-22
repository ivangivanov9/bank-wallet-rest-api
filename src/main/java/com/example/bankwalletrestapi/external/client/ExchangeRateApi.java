package com.example.bankwalletrestapi.external.client;

import com.example.bankwalletrestapi.external.dto.LatestRatesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ExchangeRateApi {

    private final String apiKey;
    private final String baseUrl;
    private final RestTemplate restTemplate;

    public ExchangeRateApi(
            @Value("${apilayer.api-key}") String apiKey,
            @Value("${apilayer.base-url}") String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    public Double fetchRateFor(String currency) {
        String url = buildUrl(currency);
        LatestRatesResponse response = restTemplate.getForObject(url, LatestRatesResponse.class);
        return extractRate(response, currency);
    }

    private String buildUrl(String currency) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl + "latest")
                .queryParam("access_key", apiKey)
                .queryParam("base", "EUR")
                .queryParam("symbols", currency.toUpperCase())
                .toUriString();
    }

    private Double extractRate(LatestRatesResponse response, String currency) {
        if (response == null || !response.isSuccess()) {
            throw new RuntimeException("Failed to fetch exchange rates");
        }

        Double rate = response.getRates().get(currency.toUpperCase());
        if (rate == null) {
            throw new RuntimeException("No rate found for currency: " + currency);
        }

        return rate;
    }
}