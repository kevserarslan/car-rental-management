package org.cms.carrental.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cms.carrental.dto.VehicleMetadata;
import org.cms.carrental.exception.VehicleApiException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleApiService {

    private final RestTemplate restTemplate;

    // NHTSA Vehicle API (Ücretsiz, kayıt gerektirmez)
    private static final String API_BASE_URL = "https://vpic.nhtsa.dot.gov/api/vehicles";

    /**
     * Marka, model ve yıla göre araç bilgilerini getirir
     */
    public VehicleMetadata getVehicleMetadata(String make, String model, Integer year) {
        try {
            log.info("Fetching vehicle metadata for: {} {} {}", make, model, year);

            String url = String.format("%s/GetModelsForMakeYear/make/%s/modelyear/%d?format=json",
                    API_BASE_URL, make, year);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("Results")) {
                Object resultsObj = response.get("Results");
                if (resultsObj instanceof List<?>) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> results = (List<Map<String, Object>>) resultsObj;

                    // Model eşleşmesi ara
                    for (Map<String, Object> result : results) {
                        String modelName = (String) result.get("Model_Name");
                        if (modelName != null && modelName.toLowerCase().contains(model.toLowerCase())) {
                            return VehicleMetadata.builder()
                                    .make(make)
                                    .model(modelName)
                                    .year(year)
                                    .vehicleType((String) result.get("VehicleTypeName"))
                                    .build();
                        }
                    }

                    // Eşleşme bulunamazsa ilk sonucu döndür
                    if (!results.isEmpty()) {
                        Map<String, Object> firstResult = results.getFirst();
                        return VehicleMetadata.builder()
                                .make(make)
                                .model((String) firstResult.get("Model_Name"))
                                .year(year)
                                .vehicleType((String) firstResult.get("VehicleTypeName"))
                                .build();
                    }
                }
            }

            log.warn("No vehicle data found for: {} {} {}", make, model, year);
            return createDefaultMetadata(make, model, year);

        } catch (Exception e) {
            log.error("Error fetching vehicle metadata: {}", e.getMessage());
            throw new VehicleApiException("Failed to fetch vehicle data from external API", e);
        }
    }

    /**
     * Belirli bir markaya ait tüm modelleri getirir
     */
    public List<String> getModelsForMake(String make) {
        try {
            log.info("Fetching models for make: {}", make);

            String url = String.format("%s/GetModelsForMake/%s?format=json", API_BASE_URL, make);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("Results")) {
                Object resultsObj = response.get("Results");
                if (resultsObj instanceof List<?>) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> results = (List<Map<String, Object>>) resultsObj;
                    return results.stream()
                            .map(r -> (String) r.get("Model_Name"))
                            .distinct()
                            .sorted()
                            .toList();
                }
            }

            return List.of();

        } catch (Exception e) {
            log.error("Error fetching models for make {}: {}", make, e.getMessage());
            throw new VehicleApiException("Failed to fetch models from external API", e);
        }
    }

    /**
     * Tüm araç markalarını getirir
     */
    public List<String> getAllMakes() {
        try {
            log.info("Fetching all vehicle makes");

            String url = String.format("%s/GetAllMakes?format=json", API_BASE_URL);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("Results")) {
                Object resultsObj = response.get("Results");
                if (resultsObj instanceof List<?>) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> results = (List<Map<String, Object>>) resultsObj;
                    return results.stream()
                            .map(r -> (String) r.get("Make_Name"))
                            .limit(50) // İlk 50 marka
                            .sorted()
                            .toList();
                }
            }

            return List.of();

        } catch (Exception e) {
            log.error("Error fetching all makes: {}", e.getMessage());
            throw new VehicleApiException("Failed to fetch makes from external API", e);
        }
    }

    private VehicleMetadata createDefaultMetadata(String make, String model, Integer year) {
        return VehicleMetadata.builder()
                .make(make)
                .model(model)
                .year(year)
                .vehicleType("Unknown")
                .fuelType("Gasoline")
                .transmission("Automatic")
                .build();
    }
}

