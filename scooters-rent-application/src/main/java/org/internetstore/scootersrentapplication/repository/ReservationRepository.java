package org.internetstore.scootersrentapplication.repository;

import org.internetstore.scootersrentapplication.entity.Reservation;
import org.internetstore.scootersrentapplication.entity.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByStatusAndExpiresAtBefore(ReservationStatus status, LocalDateTime time);
    Optional<Reservation> findByUserIdAndStatus(Integer userId, ReservationStatus status);
    Optional<Reservation> findByScooterIdAndStatus(Integer scooterId, ReservationStatus status);
}