package org.internetstore.scootersrentapplication.repository;

import org.internetstore.scootersrentapplication.entity.Reservation;
import org.internetstore.scootersrentapplication.entity.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    Optional<Reservation> findByScooterIdAndStatus(Integer scooterId, ReservationStatus status);
}