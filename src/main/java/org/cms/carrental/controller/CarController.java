package org.cms.carrental.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cms.carrental.dto.ApiResponse;
import org.cms.carrental.dto.CarDto;
import org.cms.carrental.service.CarService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CarController {

    private final CarService carService;

    /**
     * Yeni araç oluştur - Sadece ADMIN
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CarDto>> createCar(@Valid @RequestBody CarDto carDto) {
        CarDto created = carService.createCar(carDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Car created successfully", created));
    }

    /**
     * Araç detayı getir - Herkes erişebilir
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CarDto>> getCarById(@PathVariable Long id) {
        CarDto car = carService.getCarById(id);
        return ResponseEntity.ok(ApiResponse.success(car));
    }

    /**
     * Plakaya göre araç getir - Herkes erişebilir
     */
    @GetMapping("/plate/{plate}")
    public ResponseEntity<ApiResponse<CarDto>> getCarByPlate(@PathVariable String plate) {
        CarDto car = carService.getCarByPlate(plate);
        return ResponseEntity.ok(ApiResponse.success(car));
    }

    /**
     * Tüm araçları listele - Herkes erişebilir
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CarDto>>> getAllCars() {
        List<CarDto> cars = carService.getAllCars();
        return ResponseEntity.ok(ApiResponse.success(cars));
    }

    /**
     * Kategoriye göre araçları listele - Herkes erişebilir
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<CarDto>>> getCarsByCategory(@PathVariable Long categoryId) {
        List<CarDto> cars = carService.getCarsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(cars));
    }

    /**
     * Duruma göre araçları listele - Herkes erişebilir
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<CarDto>>> getCarsByStatus(@PathVariable String status) {
        List<CarDto> cars = carService.getCarsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(cars));
    }

    /**
     * Belirli tarihler arasında müsait araçları listele - Herkes erişebilir
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<CarDto>>> getAvailableCars(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<CarDto> cars = carService.getAvailableCarsBetweenDates(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(cars));
    }

    /**
     * Araç güncelle - Sadece ADMIN
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CarDto>> updateCar(
            @PathVariable Long id,
            @Valid @RequestBody CarDto carDto) {
        CarDto updated = carService.updateCar(id, carDto);
        return ResponseEntity.ok(ApiResponse.success("Car updated successfully", updated));
    }

    /**
     * Araç sil - Sadece ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.ok(ApiResponse.success("Car deleted successfully", null));
    }
}

