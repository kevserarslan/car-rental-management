package org.cms.carrental.service;

import lombok.RequiredArgsConstructor;
import org.cms.carrental.dto.ReservationDto;
import org.cms.carrental.entity.Car;
import org.cms.carrental.entity.Reservation;
import org.cms.carrental.entity.User;
import org.cms.carrental.repository.CarRepository;
import org.cms.carrental.repository.ReservationRepository;
import org.cms.carrental.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    /**
     * Mevcut oturum açmış kullanıcıyı döndürür
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    @Transactional
    public ReservationDto createReservation(ReservationDto reservationDto) {
        // Mevcut kullanıcıyı al (eğer userId belirtilmemişse)
        User user;
        if (reservationDto.getUserId() != null) {
            user = userRepository.findById(reservationDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + reservationDto.getUserId()));
        } else {
            user = getCurrentUser();
        }

        Car car = carRepository.findById(reservationDto.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + reservationDto.getCarId()));

        // Check if car is available
        if (car.getStatus() != Car.CarStatus.AVAILABLE) {
            throw new RuntimeException("Car is not available");
        }

        // Check for conflicting reservations
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                car.getId(),
                reservationDto.getStartDate(),
                reservationDto.getEndDate()
        );

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Car is already reserved for the selected dates");
        }

        // Calculate total price
        long days = ChronoUnit.DAYS.between(reservationDto.getStartDate(), reservationDto.getEndDate());
        if (days <= 0) {
            throw new RuntimeException("End date must be after start date");
        }
        double totalPrice = car.getDailyPrice() * days;

        Reservation reservation = new Reservation();
        reservation.setStartDate(reservationDto.getStartDate());
        reservation.setEndDate(reservationDto.getEndDate());
        reservation.setTotalPrice(totalPrice);
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setNotes(reservationDto.getNotes());
        reservation.setUser(user);
        reservation.setCar(car);

        Reservation savedReservation = reservationRepository.save(reservation);
        return convertToDto(savedReservation);
    }

    @Transactional(readOnly = true)
    public ReservationDto getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));
        return convertToDto(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Mevcut oturum açmış kullanıcının rezervasyonlarını döndürür
     */
    @Transactional(readOnly = true)
    public List<ReservationDto> getMyReservations() {
        User currentUser = getCurrentUser();
        return reservationRepository.findByUserId(currentUser.getId()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByCarId(Long carId) {
        return reservationRepository.findByCarId(carId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByStatus(String status) {
        Reservation.ReservationStatus reservationStatus = Reservation.ReservationStatus.valueOf(status);
        return reservationRepository.findByStatus(reservationStatus).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationDto confirmReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));

        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        Reservation updatedReservation = reservationRepository.save(reservation);
        return convertToDto(updatedReservation);
    }

    @Transactional
    public ReservationDto cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));

        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        Reservation updatedReservation = reservationRepository.save(reservation);
        return convertToDto(updatedReservation);
    }

    @Transactional
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new RuntimeException("Reservation not found with id: " + id);
        }
        reservationRepository.deleteById(id);
    }

    private ReservationDto convertToDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        dto.setId(reservation.getId());
        dto.setStartDate(reservation.getStartDate());
        dto.setEndDate(reservation.getEndDate());
        dto.setTotalPrice(reservation.getTotalPrice());
        dto.setStatus(reservation.getStatus().name());
        dto.setNotes(reservation.getNotes());
        dto.setUserId(reservation.getUser().getId());
        dto.setCarId(reservation.getCar().getId());
        dto.setUserName(reservation.getUser().getName());
        dto.setCarBrand(reservation.getCar().getBrand());
        dto.setCarModel(reservation.getCar().getModel());
        dto.setCarPlate(reservation.getCar().getPlate());
        return dto;
    }
}

