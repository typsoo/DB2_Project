package org.internetstore.scootersrentapplication.entity;

import jakarta.persistence.*;
import org.internetstore.scootersrentapplication.entity.enums.ReservationStatus;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "reservations",
        indexes = {
                @Index(
                        name = "idx_reservations_status_expires_at",
                        columnList = "status, expires_at"
                )
        }
)public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // connection with users table
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // connection with scooters table
    @ManyToOne
    @JoinColumn(name = "scooter_id")
    private Scooter scooter;

    @Column(name = "reserved_at")
    private LocalDateTime reservedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

}