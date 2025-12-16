package org.cms.carrental.controller;

import lombok.RequiredArgsConstructor;
import org.cms.carrental.dto.ApiResponse;
import org.cms.carrental.service.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * External API Controller - Döviz Kuru İşlemleri
 */
@RestController
@RequestMapping("/currency")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CurrencyController {

    private final CurrencyService currencyService;

    /**
     * Herhangi iki para birimi arasında çeviri yap
     * Örnek: GET /api/currency/convert?amount=100&from=USD&to=TRY
     * Örnek: GET /api/currency/convert?amount=1000&from=TRY&to=USD
     */
    @GetMapping("/convert")
    public ResponseEntity<ApiResponse<Map<String, Object>>> convertCurrency(
            @RequestParam Double amount,
            @RequestParam(defaultValue = "USD") String from,
            @RequestParam(defaultValue = "TRY") String to) {

        Double convertedAmount = currencyService.convertCurrency(amount, from, to);
        Double rate = currencyService.getExchangeRate(from, to);

        Map<String, Object> result = Map.of(
            "originalAmount", amount,
            "originalCurrency", from,
            "convertedAmount", convertedAmount,
            "targetCurrency", to,
            "exchangeRate", rate
        );

        return ResponseEntity.ok(ApiResponse.success("Price converted successfully", result));
    }

    /**
     * Güncel döviz kurlarını getir
     * Örnek: GET /api/currency/rates?base=USD
     */
    @GetMapping("/rates")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getExchangeRates(
            @RequestParam(defaultValue = "USD") String base) {

        Map<String, Object> rates = currencyService.getExchangeRates(base);
        return ResponseEntity.ok(ApiResponse.success("Exchange rates retrieved", rates));
    }

    /**
     * İki para birimi arasındaki kuru getir
     * Örnek: GET /api/currency/rate?from=USD&to=TRY
     */
    @GetMapping("/rate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getExchangeRate(
            @RequestParam(defaultValue = "USD") String from,
            @RequestParam(defaultValue = "TRY") String to) {

        Double rate = currencyService.getExchangeRate(from, to);

        Map<String, Object> result = Map.of(
            "from", from,
            "to", to,
            "rate", rate
        );

        return ResponseEntity.ok(ApiResponse.success("Exchange rate retrieved", result));
    }
}
