package org.cms.carrental.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * External API Integration - Döviz Kuru Servisi
 * API: https://api.exchangerate-api.com (Ücretsiz, kayıt gerektirmez)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {

    private final RestTemplate restTemplate;

    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/";

    /**
     * Belirtilen para biriminden TL'ye çevir
     */
    public Double convertToTRY(Double amount, String fromCurrency) {
        try {
            log.info("Converting {} {} to TRY", amount, fromCurrency);

            String url = API_URL + fromCurrency;

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("rates")) {
                @SuppressWarnings("unchecked")
                Map<String, Double> rates = (Map<String, Double>) response.get("rates");
                Double tryRate = rates.get("TRY");

                if (tryRate != null) {
                    Double result = amount * tryRate;
                    log.info("Conversion successful: {} {} = {} TRY", amount, fromCurrency, result);
                    return result;
                }
            }

            log.warn("Could not get TRY rate, returning original amount");
            return amount;

        } catch (Exception e) {
            log.error("Currency API error: {}", e.getMessage());
            return amount; // Hata durumunda orijinal tutarı döndür
        }
    }

    /**
     * USD'den TL'ye çevir (En sık kullanılan)
     */
    public Double convertUsdToTry(Double amountUsd) {
        return convertToTRY(amountUsd, "USD");
    }

    /**
     * EUR'dan TL'ye çevir
     */
    public Double convertEurToTry(Double amountEur) {
        return convertToTRY(amountEur, "EUR");
    }

    /**
     * TL'den USD'ye çevir
     */
    public Double convertTryToUsd(Double amountTry) {
        return convertCurrency(amountTry, "TRY", "USD");
    }

    /**
     * Genel para birimi çevirici - Herhangi iki para birimi arasında çeviri yapar
     */
    public Double convertCurrency(Double amount, String from, String to) {
        try {
            log.info("Converting {} {} to {}", amount, from, to);

            String url = API_URL + from;

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("rates")) {
                @SuppressWarnings("unchecked")
                Map<String, Double> rates = (Map<String, Double>) response.get("rates");
                Double rate = rates.get(to);

                if (rate != null) {
                    Double result = amount * rate;
                    log.info("Conversion successful: {} {} = {} {}", amount, from, result, to);
                    return result;
                }
            }

            log.warn("Could not get {} rate, returning original amount", to);
            return amount;

        } catch (Exception e) {
            log.error("Currency API error: {}", e.getMessage());
            return amount;
        }
    }

    /**
     * Güncel döviz kurlarını getir
     */
    public Map<String, Object> getExchangeRates(String baseCurrency) {
        try {
            log.info("Fetching exchange rates for base currency: {}", baseCurrency);

            String url = API_URL + baseCurrency;

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            return response;

        } catch (Exception e) {
            log.error("Error fetching exchange rates: {}", e.getMessage());
            return Map.of("error", "Exchange rates unavailable", "base", baseCurrency);
        }
    }

    /**
     * İki para birimi arasındaki kuru getir
     */
    public Double getExchangeRate(String from, String to) {
        try {
            String url = API_URL + from;

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("rates")) {
                @SuppressWarnings("unchecked")
                Map<String, Double> rates = (Map<String, Double>) response.get("rates");
                return rates.getOrDefault(to, 1.0);
            }

            return 1.0;

        } catch (Exception e) {
            log.error("Error getting exchange rate {}/{}: {}", from, to, e.getMessage());
            return 1.0;
        }
    }
}
