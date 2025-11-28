package org.cms.carrental.repository;

import org.cms.carrental.entity.Rental;
import org.cms.carrental.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    Optional<Rental> findByReservation(Reservation reservation);

    Optional<Rental> findByReservationId(Long reservationId);

    List<Rental> findByStatus(Rental.RentalStatus status);

    @Query("SELECT r FROM Rental r WHERE r.reservation.user.id = :userId " +
           "ORDER BY r.createdAt DESC")
    List<Rental> findByUserId(Long userId);

    @Query("SELECT r FROM Rental r WHERE r.status = 'PICKED_UP' " +
           "AND r.returnDate < CURRENT_TIMESTAMP")
    List<Rental> findOverdueRentals();
}
