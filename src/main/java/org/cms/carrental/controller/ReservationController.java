package org.cms.carrental.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cms.carrental.dto.ApiResponse;
import org.cms.carrental.dto.ReservationDto;
import org.cms.carrental.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Yeni rezervasyon oluştur - Giriş yapmış tüm kullanıcılar
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReservationDto>> createReservation(@Valid @RequestBody ReservationDto reservationDto) {
        ReservationDto created = reservationService.createReservation(reservationDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reservation created successfully", created));
    }

    /**
     * Rezervasyon detayı getir - Giriş yapmış kullanıcılar
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReservationDto>> getReservationById(@PathVariable Long id) {
        ReservationDto reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(ApiResponse.success(reservation));
    }

    /**
     * Tüm rezervasyonları listele - Sadece ADMIN
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getAllReservations() {
        List<ReservationDto> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(ApiResponse.success(reservations));
    }

    /**
     * Kullanıcıya göre rezervasyonları listele -
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getReservationsByUserId(@PathVariable Long userId) {
        List<ReservationDto> reservations = reservationService.getReservationsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(reservations));
    }

    /**
     * Giriş yapmış kullanıcının kendi rezervasyonlarını listele
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getMyReservations() {
        List<ReservationDto> reservations = reservationService.getMyReservations();
        return ResponseEntity.ok(ApiResponse.success(reservations));
    }

    /**
     * Araca göre rezervasyonları listele - Sadece ADMIN
     */
    @GetMapping("/car/{carId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getReservationsByCarId(@PathVariable Long carId) {
        List<ReservationDto> reservations = reservationService.getReservationsByCarId(carId);
        return ResponseEntity.ok(ApiResponse.success(reservations));
    }

    /**
     * Duruma göre rezervasyonları listele - Sadece ADMIN
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getReservationsByStatus(@PathVariable String status) {
        List<ReservationDto> reservations = reservationService.getReservationsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(reservations));
    }

    /**
     * Rezervasyonu onayla - Sadece ADMIN
     */
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReservationDto>> confirmReservation(@PathVariable Long id) {
        ReservationDto confirmed = reservationService.confirmReservation(id);
        return ResponseEntity.ok(ApiResponse.success("Reservation confirmed successfully", confirmed));
    }

    /**
     * Rezervasyonu iptal et - İlgili kullanıcı veya ADMIN
     */
    @PutMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReservationDto>> cancelReservation(@PathVariable Long id) {
        ReservationDto cancelled = reservationService.cancelReservation(id);
        return ResponseEntity.ok(ApiResponse.success("Reservation cancelled successfully", cancelled));
    }

    /**
     * Rezervasyon sil - Sadece ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.ok(ApiResponse.success("Reservation deleted successfully", null));
    }
}

