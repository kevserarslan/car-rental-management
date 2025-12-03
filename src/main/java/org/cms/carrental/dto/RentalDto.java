package org.cms.carrental.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalDto {
    private Long id;

    @NotNull(message = "Pickup date is required")
    private LocalDateTime pickupDate;

    private LocalDateTime returnDate;

    private LocalDateTime actualReturnDate;

    private Integer initialMileage;

    private Integer finalMileage;

    private Double additionalCharges;

    private String status;

    private String notes;

    @NotNull(message = "Reservation ID is required")
    private Long reservationId;
}

