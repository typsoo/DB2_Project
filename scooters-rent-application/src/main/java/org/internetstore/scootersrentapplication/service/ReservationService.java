package org.internetstore.scootersrentapplication.service;

import lombok.RequiredArgsConstructor;
import org.internetstore.scootersrentapplication.dto.ReservationDto;
import org.internetstore.scootersrentapplication.entity.Reservation;
import org.internetstore.scootersrentapplication.entity.Scooter;
import org.internetstore.scootersrentapplication.entity.User;
import org.internetstore.scootersrentapplication.entity.enums.ReservationStatus;
import org.internetstore.scootersrentapplication.entity.enums.ScooterStatus;
import org.internetstore.scootersrentapplication.repository.ReservationRepository;
import org.internetstore.scootersrentapplication.repository.ScooterRepository;
import org.internetstore.scootersrentapplication.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ScooterRepository scooterRepository;
    private final UserRepository userRepository;
    //Add WalletService for checking balance before reservation

    @Transactional
    public ReservationDto.Response reserveScooter(Integer userId, Integer scooterId) {
        if (reservationRepository.findByUserIdAndStatus(userId, ReservationStatus.ACTIVE).isPresent()) {
            throw new IllegalStateException("You already have an active reservation");
        }

        Scooter scooter = scooterRepository.findByIdWithLock(scooterId)
                .orElseThrow(() -> new IllegalArgumentException("The scooter has not been found"));

        if (scooter.getStatus() != ScooterStatus.AVAILABLE) {
            throw new IllegalStateException("The scooter is already taken");
        }

        scooter.setStatus(ScooterStatus.RESERVED);
        scooterRepository.save(scooter);

        Reservation reservation = new Reservation();


        reservation.setUser(userRepository.getReferenceById(userId));
        reservation.setScooter(scooter);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        Reservation savedReservation = reservationRepository.save(reservation);

        return new ReservationDto.Response(
                savedReservation.getId(),
                savedReservation.getScooter().getId(),
                savedReservation.getStatus(),
                savedReservation.getExpiresAt()
        );

    }

    @Transactional(readOnly = true)
    public Optional<ReservationDto.Response> getActiveUserReservation(Integer userId) {

        return reservationRepository.findByUserIdAndStatus(userId, ReservationStatus.ACTIVE)
                .map(reservation -> new ReservationDto.Response(
                        reservation.getId(),
                        reservation.getScooter().getId(),
                        reservation.getStatus(),
                        reservation.getExpiresAt()
                ));
    }

    @Transactional
    public void cancelReservation(Integer userId, Integer reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("No reservation found"));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new SecurityException("You do not have permission to cancel this reservation");
        }

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("You can only cancel an active reservation");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        Scooter scooter = scooterRepository.findById(reservation.getScooter().getId())
                .orElseThrow(() -> new IllegalStateException("The scooter has not been found"));

        scooter.setStatus(ScooterStatus.AVAILABLE);

        reservationRepository.save(reservation);
        scooterRepository.save(scooter);
    }
}