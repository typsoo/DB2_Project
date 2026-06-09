package org.internetstore.scootersrentapplication.service;

import org.internetstore.scootersrentapplication.dto.RideDto;
import org.internetstore.scootersrentapplication.dto.RideEndRequestDto;
import org.internetstore.scootersrentapplication.dto.RideEndResponseDto;
import org.internetstore.scootersrentapplication.dto.RideStartRequestDto;
import org.internetstore.scootersrentapplication.entity.*;
import org.internetstore.scootersrentapplication.entity.enums.ScooterStatus;
import org.internetstore.scootersrentapplication.entity.enums.ReservationStatus;
import org.internetstore.scootersrentapplication.entity.enums.TransactionType;
import org.internetstore.scootersrentapplication.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final ScooterRepository scooterRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final TransactionRepository transactionRepository;

    // Minimum balance
    private static final BigDecimal MIN_START_BALANCE = new BigDecimal("10.00");
    private static final BigDecimal PRICE_PER_MINUTE = new BigDecimal("1.50");

    public RideService(RideRepository rideRepository, ScooterRepository scooterRepository,
                       WalletRepository walletRepository, UserRepository userRepository,
                       ReservationRepository reservationRepository, TransactionRepository transactionRepository) {
        this.rideRepository = rideRepository;
        this.scooterRepository = scooterRepository;
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public RideDto startRide(User user, RideStartRequestDto request) {

        if (rideRepository.existsByUserIdAndEndTimeIsNull(user.getId())) {
            throw new IllegalStateException("You already have an active ride");
        }

        // Fetch scooter from DB with pessimistic lock to prevent concurrent rentals
        Scooter scooter = scooterRepository.findByIdWithLock(request.scooterId())
                .orElseThrow(() -> new RuntimeException("Scooter was not found"));

        // Check if scooter is not in use or maintenance
        if (scooter.getStatus() == ScooterStatus.IN_USE || scooter.getStatus() == ScooterStatus.MAINTENANCE) {
            throw new RuntimeException("Scooter is unavailable");
        }

        // Reservation logic handling
        if (scooter.getStatus() == ScooterStatus.RESERVED) {
            Reservation activeReservation = reservationRepository.findByScooterIdAndStatus(scooter.getId(), ReservationStatus.ACTIVE)
                    .orElseThrow(() -> new RuntimeException("System error: Scooter is reserved but active reservation not found"));

            // Check if reservation belongs to the user trying to start the ride
            if (!activeReservation.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Scooter is reserved by another user");
            }

            // Close the reservation as the user is taking the scooter
            activeReservation.setStatus(ReservationStatus.COMPLETED);
            reservationRepository.save(activeReservation);
        }


        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet was not found"));

        // Check if account has the minimum amount needed to start
        if (wallet.getBalance().compareTo(MIN_START_BALANCE) < 0) {
            throw new RuntimeException("Insufficient funds to start the ride");
        }

        // Change scooter status to in use and save to DB
        scooter.setStatus(ScooterStatus.IN_USE);
        scooterRepository.save(scooter);

        // Register new ride in the system and assign current time
        Ride ride = new Ride();
        ride.setUser(user);
        ride.setScooter(scooter);
        ride.setStartTime(LocalDateTime.now());
        Ride savedRide = rideRepository.save(ride);

        // Pack the necessary data into the dto object to send it back to the client
        return new RideDto(
                savedRide.getId(),
                scooter.getId(),
                savedRide.getStartTime()
        );
    }

    @Transactional
    public RideEndResponseDto endRide(RideEndRequestDto request) {

        // find ride by id
        Ride ride = rideRepository.findById(request.rideId())
                .orElseThrow(() -> new RuntimeException("Ride was not found"));

        // check if ride wasn't already finished
        if (ride.getEndTime() != null) {
            throw new RuntimeException("This ride is already finished");
        }

        // register end time and provided distance
        LocalDateTime endTime = LocalDateTime.now();
        ride.setEndTime(endTime);
        ride.setDistance(request.distance());

        // calculate duration in minutes
        long durationInMinutes = Duration.between(ride.getStartTime(), endTime).toMinutes();

        // if someone rode less than a minute we still count it as one to prevent free rides
        if (durationInMinutes == 0) {
            durationInMinutes = 1;
        }

        // calculate cost based on time
        BigDecimal totalCost = PRICE_PER_MINUTE.multiply(BigDecimal.valueOf(durationInMinutes));
        ride.setTotalCost(totalCost);

        // lock wallet to prevent double spending in the same fraction of a second
        Wallet wallet = walletRepository.findByUserIdWithLock(ride.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Wallet was not found"));

        // withdraw money from account even if it goes negative
        wallet.setBalance(wallet.getBalance().subtract(totalCost));
        walletRepository.save(wallet);

        // create a transaction record for payment history
        Transaction paymentTransaction = new Transaction();
        paymentTransaction.setWallet(wallet);
        paymentTransaction.setRide(ride);
        paymentTransaction.setAmount(totalCost);
        paymentTransaction.setType(TransactionType.RIDE_PAYMENT);
        paymentTransaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(paymentTransaction);

        // release scooter for other users
        Scooter scooter = ride.getScooter();
        scooter.setStatus(ScooterStatus.AVAILABLE);
        scooterRepository.save(scooter);

        // save updated ride
        rideRepository.save(ride);

        // prepare response with receipt
        return new RideEndResponseDto(
                ride.getId(),
                totalCost,
                endTime,
                durationInMinutes
        );
    }
}