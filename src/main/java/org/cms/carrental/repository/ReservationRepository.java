package org.cms.carrental.repository;

import org.cms.carrental.entity.Reservation;
import org.cms.carrental.entity.User;
import org.cms.carrental.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUser(User user);

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByCar(Car car);

    List<Reservation> findByCarId(Long carId);

    List<Reservation> findByStatus(Reservation.ReservationStatus status);

    List<Reservation> findByUserIdAndStatus(Long userId, Reservation.ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.car.id = :carId " +
           "AND r.status IN ('PENDING', 'CONFIRMED') " +
           "AND r.startDate <= :endDate " +
           "AND r.endDate >= :startDate")
    List<Reservation> findConflictingReservations(
        @Param("carId") Long carId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId " +
           "ORDER BY r.createdAt DESC")
    List<Reservation> findRecentReservationsByUserId(@Param("userId") Long userId);
}
