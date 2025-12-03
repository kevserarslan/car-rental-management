package org.cms.carrental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDto {
    private Long id;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotBlank(message = "Plate number is required")
    private String plate;

    private String description;

    @Positive(message = "Daily price must be positive")
    private Double dailyPrice;

    private String status;

    private String imageUrl;

    private String fuelType;

    private String transmissionType;

    private Integer seatCount;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private String categoryName;
}

