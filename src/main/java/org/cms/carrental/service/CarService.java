package org.cms.carrental.service;

import lombok.RequiredArgsConstructor;
import org.cms.carrental.dto.CarDto;
import org.cms.carrental.entity.Car;
import org.cms.carrental.entity.Category;
import org.cms.carrental.repository.CarRepository;
import org.cms.carrental.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public CarDto createCar(CarDto carDto) {
        Category category = categoryRepository.findById(carDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + carDto.getCategoryId()));

        Car car = new Car();
        car.setBrand(carDto.getBrand());
        car.setModel(carDto.getModel());
        car.setYear(carDto.getYear());
        car.setPlate(carDto.getPlate());
        car.setDescription(carDto.getDescription());
        car.setDailyPrice(carDto.getDailyPrice());
        car.setStatus(Car.CarStatus.AVAILABLE);
        car.setImageUrl(carDto.getImageUrl());
        car.setFuelType(carDto.getFuelType());
        car.setTransmissionType(carDto.getTransmissionType());
        car.setSeatCount(carDto.getSeatCount());
        car.setCategory(category);

        Car savedCar = carRepository.save(car);
        return convertToDto(savedCar);
    }

    public CarDto getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + id));
        return convertToDto(car);
    }

    public CarDto getCarByPlate(String plate) {
        Car car = carRepository.findByPlate(plate)
                .orElseThrow(() -> new RuntimeException("Car not found with plate: " + plate));
        return convertToDto(car);
    }

    public List<CarDto> getAllCars() {
        return carRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<CarDto> getCarsByCategory(Long categoryId) {
        return carRepository.findByCategoryId(categoryId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<CarDto> getCarsByStatus(String status) {
        Car.CarStatus carStatus = Car.CarStatus.valueOf(status);
        return carRepository.findByStatus(carStatus).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<CarDto> getAvailableCarsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return carRepository.findAvailableCarsBetweenDates(startDate, endDate).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CarDto updateCar(Long id, CarDto carDto) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + id));

        if (carDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(carDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + carDto.getCategoryId()));
            car.setCategory(category);
        }

        car.setBrand(carDto.getBrand());
        car.setModel(carDto.getModel());
        car.setYear(carDto.getYear());
        car.setPlate(carDto.getPlate());
        car.setDescription(carDto.getDescription());
        car.setDailyPrice(carDto.getDailyPrice());
        car.setImageUrl(carDto.getImageUrl());
        car.setFuelType(carDto.getFuelType());
        car.setTransmissionType(carDto.getTransmissionType());
        car.setSeatCount(carDto.getSeatCount());

        if (carDto.getStatus() != null) {
            car.setStatus(Car.CarStatus.valueOf(carDto.getStatus()));
        }

        Car updatedCar = carRepository.save(car);
        return convertToDto(updatedCar);
    }

    @Transactional
    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new RuntimeException("Car not found with id: " + id);
        }
        carRepository.deleteById(id);
    }

    private CarDto convertToDto(Car car) {
        CarDto dto = new CarDto();
        dto.setId(car.getId());
        dto.setBrand(car.getBrand());
        dto.setModel(car.getModel());
        dto.setYear(car.getYear());
        dto.setPlate(car.getPlate());
        dto.setDescription(car.getDescription());
        dto.setDailyPrice(car.getDailyPrice());
        dto.setStatus(car.getStatus().name());
        dto.setImageUrl(car.getImageUrl());
        dto.setFuelType(car.getFuelType());
        dto.setTransmissionType(car.getTransmissionType());
        dto.setSeatCount(car.getSeatCount());
        dto.setCategoryId(car.getCategory().getId());
        dto.setCategoryName(car.getCategory().getName());
        return dto;
    }
}

