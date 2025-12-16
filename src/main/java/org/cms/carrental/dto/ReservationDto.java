package org.cms.carrental.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    private Long id;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private Double totalPrice;

    private String status;

    private String notes;

    // userId opsiyonel - gönderilmezse mevcut kullanıcı otomatik atanır
    private Long userId;

    @NotNull(message = "Car ID is required")
    private Long carId;

    private String userName;
    private String carBrand;
    private String carModel;
    private String carPlate;
}

