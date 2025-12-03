package org.cms.carrental.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cms.carrental.dto.ApiResponse;
import org.cms.carrental.dto.CarDto;
import org.cms.carrental.service.CarService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CarController {

    private final CarService carService;

    @PostMapping
    public ResponseEntity<ApiResponse<CarDto>> createCar(@Valid @RequestBody CarDto carDto) {
        CarDto created = carService.createCar(carDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Car created successfully", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CarDto>> getCarById(@PathVariable Long id) {
        CarDto car = carService.getCarById(id);
        return ResponseEntity.ok(ApiResponse.success(car));
    }

    @GetMapping("/plate/{plate}")
    public ResponseEntity<ApiResponse<CarDto>> getCarByPlate(@PathVariable String plate) {
        CarDto car = carService.getCarByPlate(plate);
        return ResponseEntity.ok(ApiResponse.success(car));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CarDto>>> getAllCars() {
        List<CarDto> cars = carService.getAllCars();
        return ResponseEntity.ok(ApiResponse.success(cars));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<CarDto>>> getCarsByCategory(@PathVariable Long categoryId) {
        List<CarDto> cars = carService.getCarsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(cars));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<CarDto>>> getCarsByStatus(@PathVariable String status) {
        List<CarDto> cars = carService.getCarsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(cars));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<CarDto>>> getAvailableCars(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<CarDto> cars = carService.getAvailableCarsBetweenDates(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(cars));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CarDto>> updateCar(
            @PathVariable Long id,
            @Valid @RequestBody CarDto carDto) {
        CarDto updated = carService.updateCar(id, carDto);
        return ResponseEntity.ok(ApiResponse.success("Car updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.ok(ApiResponse.success("Car deleted successfully", null));
    }
}

