package org.internetstore.scootersrentapplication.service;

import lombok.RequiredArgsConstructor;
import org.internetstore.scootersrentapplication.dto.ReservationResponseDto;
import org.internetstore.scootersrentapplication.entity.Reservation;
import org.internetstore.scootersrentapplication.entity.Scooter;
import org.internetstore.scootersrentapplication.entity.User;
import org.internetstore.scootersrentapplication.entity.enums.ReservationStatus;
import org.internetstore.scootersrentapplication.entity.enums.ScooterStatus;
import org.internetstore.scootersrentapplication.repository.ReservationRepository;
import org.internetstore.scootersrentapplication.repository.RideRepository;
import org.internetstore.scootersrentapplication.repository.ScooterRepository;
import org.internetstore.scootersrentapplication.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ScooterRepository scooterRepository;
    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    //Add WalletService for checking balance before reservation

    @Transactional
    public ReservationResponseDto reserveScooter(Integer userId, Integer scooterId) {
        if (reservationRepository.findByUserIdAndStatus(userId, ReservationStatus.ACTIVE).isPresent()) {
            throw new IllegalStateException("You already have an active reservation");
        }

        if (rideRepository.existsByUserIdAndEndTimeIsNull(userId)) {
            throw new IllegalStateException("You cannot reserve a scooter while having an active ride");
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

        return new ReservationResponseDto(
                savedReservation.getId(),
                savedReservation.getScooter().getId(),
                savedReservation.getStatus(),
                savedReservation.getExpiresAt()
        );

    }

    @Transactional(readOnly = true)
    public Optional<ReservationResponseDto> getActiveUserReservation(Integer userId) {

        return reservationRepository.findByUserIdAndStatus(userId, ReservationStatus.ACTIVE)
                .map(reservation -> new ReservationResponseDto(
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

    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getUserReservationHistory(Integer userId) {
        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getUser().getId().equals(userId))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ReservationResponseDto mapToDto(Reservation reservation) {
        return new ReservationResponseDto(
                reservation.getId(),
                reservation.getScooter().getId(),
                reservation.getStatus(),
                reservation.getExpiresAt()
        );
    }
}