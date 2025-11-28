package org.cms.carrental.repository;

import org.cms.carrental.entity.Car;
import org.cms.carrental.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByPlate(String plate);

    List<Car> findByCategory(Category category);

    List<Car> findByStatus(Car.CarStatus status);

    List<Car> findByCategoryId(Long categoryId);

    @Query("SELECT c FROM Car c WHERE c.status = 'AVAILABLE' " +
           "AND c.id NOT IN (" +
           "SELECT r.car.id FROM Reservation r " +
           "WHERE r.status IN ('PENDING', 'CONFIRMED') " +
           "AND r.startDate <= :endDate " +
           "AND r.endDate >= :startDate)")
    List<Car> findAvailableCarsBetweenDates(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
