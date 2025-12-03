package org.cms.carrental.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cms.carrental.dto.ApiResponse;
import org.cms.carrental.dto.RentalDto;
import org.cms.carrental.service.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RentalController {

    private final RentalService rentalService;

    @PostMapping
    public ResponseEntity<ApiResponse<RentalDto>> createRental(@Valid @RequestBody RentalDto rentalDto) {
        RentalDto created = rentalService.createRental(rentalDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Rental created successfully", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RentalDto>> getRentalById(@PathVariable Long id) {
        RentalDto rental = rentalService.getRentalById(id);
        return ResponseEntity.ok(ApiResponse.success(rental));
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<ApiResponse<RentalDto>> getRentalByReservationId(@PathVariable Long reservationId) {
        RentalDto rental = rentalService.getRentalByReservationId(reservationId);
        return ResponseEntity.ok(ApiResponse.success(rental));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RentalDto>>> getAllRentals() {
        List<RentalDto> rentals = rentalService.getAllRentals();
        return ResponseEntity.ok(ApiResponse.success(rentals));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<RentalDto>>> getRentalsByUserId(@PathVariable Long userId) {
        List<RentalDto> rentals = rentalService.getRentalsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(rentals));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<RentalDto>>> getRentalsByStatus(@PathVariable String status) {
        List<RentalDto> rentals = rentalService.getRentalsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(rentals));
    }

    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<RentalDto>>> getOverdueRentals() {
        List<RentalDto> rentals = rentalService.getOverdueRentals();
        return ResponseEntity.ok(ApiResponse.success(rentals));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<ApiResponse<RentalDto>> returnCar(
            @PathVariable Long id,
            @Valid @RequestBody RentalDto rentalDto) {
        RentalDto returned = rentalService.returnCar(id, rentalDto);
        return ResponseEntity.ok(ApiResponse.success("Car returned successfully", returned));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRental(@PathVariable Long id) {
        rentalService.deleteRental(id);
        return ResponseEntity.ok(ApiResponse.success("Rental deleted successfully", null));
    }
}

