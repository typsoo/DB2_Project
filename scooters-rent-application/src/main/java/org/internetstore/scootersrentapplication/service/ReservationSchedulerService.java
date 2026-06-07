package org.internetstore.scootersrentapplication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.internetstore.scootersrentapplication.entity.Reservation;
import org.internetstore.scootersrentapplication.entity.Scooter;
import org.internetstore.scootersrentapplication.entity.enums.ReservationStatus;
import org.internetstore.scootersrentapplication.entity.enums.ScooterStatus;
import org.internetstore.scootersrentapplication.repository.ReservationRepository;
import org.internetstore.scootersrentapplication.repository.ScooterRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationSchedulerService {

    private final ReservationRepository reservationRepository;
    private final ScooterRepository scooterRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkExpiredReservations() {
        List<Reservation> expired = reservationRepository
                .findByStatusAndExpiresAtBefore(ReservationStatus.ACTIVE, LocalDateTime.now());

        for (Reservation res : expired) {
            res.setStatus(ReservationStatus.EXPIRED);
            Scooter scooter = res.getScooter();
            scooter.setStatus(ScooterStatus.AVAILABLE);

            reservationRepository.save(res);
            scooterRepository.save(scooter);
        }
    }
}