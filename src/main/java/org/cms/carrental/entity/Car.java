package org.cms.carrental.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cars")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Brand is required")
    @Column(nullable = false)
    private String brand;

    @NotBlank(message = "Model is required")
    @Column(nullable = false)
    private String model;

    @NotNull(message = "Year is required")
    @Column(nullable = false)
    private Integer year;

    @NotBlank(message = "Plate number is required")
    @Column(nullable = false, unique = true)
    private String plate;

    @Column(length = 500)
    private String description;

    @Positive(message = "Daily price must be positive")
    @Column(name = "daily_price", nullable = false)
    private Double dailyPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarStatus status = CarStatus.AVAILABLE;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "fuel_type")
    private String fuelType;

    @Column(name = "transmission_type")
    private String transmissionType;

    @Column(name = "seat_count")
    private Integer seatCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "car")
    private List<Reservation> reservations = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum CarStatus {
        AVAILABLE, RENTED, MAINTENANCE, UNAVAILABLE
    }
}

