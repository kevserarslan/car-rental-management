package org.cms.carrental.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cms.carrental.dto.ApiResponse;
import org.cms.carrental.dto.ReservationDto;
import org.cms.carrental.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationDto>> createReservation(@Valid @RequestBody ReservationDto reservationDto) {
        ReservationDto created = reservationService.createReservation(reservationDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reservation created successfully", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservationDto>> getReservationById(@PathVariable Long id) {
        ReservationDto reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(ApiResponse.success(reservation));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getAllReservations() {
        List<ReservationDto> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(ApiResponse.success(reservations));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getReservationsByUserId(@PathVariable Long userId) {
        List<ReservationDto> reservations = reservationService.getReservationsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(reservations));
    }

    @GetMapping("/car/{carId}")
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getReservationsByCarId(@PathVariable Long carId) {
        List<ReservationDto> reservations = reservationService.getReservationsByCarId(carId);
        return ResponseEntity.ok(ApiResponse.success(reservations));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getReservationsByStatus(@PathVariable String status) {
        List<ReservationDto> reservations = reservationService.getReservationsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(reservations));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<ReservationDto>> confirmReservation(@PathVariable Long id) {
        ReservationDto confirmed = reservationService.confirmReservation(id);
        return ResponseEntity.ok(ApiResponse.success("Reservation confirmed successfully", confirmed));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ReservationDto>> cancelReservation(@PathVariable Long id) {
        ReservationDto cancelled = reservationService.cancelReservation(id);
        return ResponseEntity.ok(ApiResponse.success("Reservation cancelled successfully", cancelled));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.ok(ApiResponse.success("Reservation deleted successfully", null));
    }
}

