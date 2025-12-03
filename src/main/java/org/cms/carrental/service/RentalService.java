package org.cms.carrental.service;

import lombok.RequiredArgsConstructor;
import org.cms.carrental.dto.RentalDto;
import org.cms.carrental.entity.Car;
import org.cms.carrental.entity.Rental;
import org.cms.carrental.entity.Reservation;
import org.cms.carrental.repository.CarRepository;
import org.cms.carrental.repository.RentalRepository;
import org.cms.carrental.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;

    @Transactional
    public RentalDto createRental(RentalDto rentalDto) {
        Reservation reservation = reservationRepository.findById(rentalDto.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + rentalDto.getReservationId()));

        // Check if reservation is confirmed
        if (reservation.getStatus() != Reservation.ReservationStatus.CONFIRMED) {
            throw new RuntimeException("Reservation must be confirmed before creating rental");
        }

        // Check if rental already exists for this reservation
        if (rentalRepository.findByReservationId(reservation.getId()).isPresent()) {
            throw new RuntimeException("Rental already exists for this reservation");
        }

        // Update car status to RENTED
        Car car = reservation.getCar();
        car.setStatus(Car.CarStatus.RENTED);
        carRepository.save(car);

        Rental rental = new Rental();
        rental.setPickupDate(rentalDto.getPickupDate() != null ? rentalDto.getPickupDate() : LocalDateTime.now());
        rental.setReturnDate(reservation.getEndDate().atTime(12, 0));
        rental.setInitialMileage(rentalDto.getInitialMileage());
        rental.setStatus(Rental.RentalStatus.PICKED_UP);
        rental.setNotes(rentalDto.getNotes());
        rental.setReservation(reservation);

        Rental savedRental = rentalRepository.save(rental);
        return convertToDto(savedRental);
    }

    public RentalDto getRentalById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found with id: " + id));
        return convertToDto(rental);
    }

    public RentalDto getRentalByReservationId(Long reservationId) {
        Rental rental = rentalRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new RuntimeException("Rental not found for reservation id: " + reservationId));
        return convertToDto(rental);
    }

    public List<RentalDto> getAllRentals() {
        return rentalRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<RentalDto> getRentalsByUserId(Long userId) {
        return rentalRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<RentalDto> getRentalsByStatus(String status) {
        Rental.RentalStatus rentalStatus = Rental.RentalStatus.valueOf(status);
        return rentalRepository.findByStatus(rentalStatus).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<RentalDto> getOverdueRentals() {
        return rentalRepository.findOverdueRentals().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RentalDto returnCar(Long id, RentalDto rentalDto) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found with id: " + id));

        if (rental.getStatus() == Rental.RentalStatus.RETURNED) {
            throw new RuntimeException("Car has already been returned");
        }

        rental.setActualReturnDate(LocalDateTime.now());
        rental.setFinalMileage(rentalDto.getFinalMileage());
        rental.setAdditionalCharges(rentalDto.getAdditionalCharges() != null ? rentalDto.getAdditionalCharges() : 0.0);
        rental.setStatus(Rental.RentalStatus.RETURNED);

        // Update car status back to AVAILABLE
        Car car = rental.getReservation().getCar();
        car.setStatus(Car.CarStatus.AVAILABLE);
        carRepository.save(car);

        // Update reservation status to COMPLETED
        Reservation reservation = rental.getReservation();
        reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);

        Rental updatedRental = rentalRepository.save(rental);
        return convertToDto(updatedRental);
    }

    @Transactional
    public void deleteRental(Long id) {
        if (!rentalRepository.existsById(id)) {
            throw new RuntimeException("Rental not found with id: " + id);
        }
        rentalRepository.deleteById(id);
    }

    private RentalDto convertToDto(Rental rental) {
        RentalDto dto = new RentalDto();
        dto.setId(rental.getId());
        dto.setPickupDate(rental.getPickupDate());
        dto.setReturnDate(rental.getReturnDate());
        dto.setActualReturnDate(rental.getActualReturnDate());
        dto.setInitialMileage(rental.getInitialMileage());
        dto.setFinalMileage(rental.getFinalMileage());
        dto.setAdditionalCharges(rental.getAdditionalCharges());
        dto.setStatus(rental.getStatus().name());
        dto.setNotes(rental.getNotes());
        dto.setReservationId(rental.getReservation().getId());
        return dto;
    }
}

