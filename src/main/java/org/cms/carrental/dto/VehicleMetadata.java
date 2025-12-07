package org.cms.carrental.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleMetadata {
    private String make;        // Marka (Toyota, BMW, etc.)
    private String model;       // Model (Corolla, X5, etc.)
    private Integer year;       // Yıl
    private String vehicleType; // Araç tipi (Sedan, SUV, etc.)
    private String fuelType;    // Yakıt tipi (Gasoline, Diesel, Electric)
    private String transmission; // Şanzıman (Automatic, Manual)
    private Integer engineSize;  // Motor hacmi
    private String bodyStyle;    // Kasa tipi (Sedan, Hatchback, etc.)
}

