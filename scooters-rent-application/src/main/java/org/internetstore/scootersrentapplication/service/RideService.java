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

    // Minimalne saldo
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
    public RideDto startRide(RideStartRequestDto request) {

        // Wyciągamy hulajnogę z bazy z pesymistyczną blokadą, aby nikt inny nie mógł jej w tym samym czasie wypożyczyć
        Scooter scooter = scooterRepository.findByIdWithLock(request.getScooterId())
                .orElseThrow(() -> new RuntimeException("Scooter was not found"));

        // Sprawdzamy, czy hulajnoga nie jest w trakcie jazdy lub w naprawie
        if (scooter.getStatus() == ScooterStatus.IN_USE || scooter.getStatus() == ScooterStatus.MAINTENANCE) {
            throw new RuntimeException("Scooter is unavailable");
        }

        // Obsługa logiki rezerwacji
        if (scooter.getStatus() == ScooterStatus.RESERVED) {
            Reservation activeReservation = reservationRepository.findByScooterIdAndStatus(scooter.getId(), ReservationStatus.ACTIVE)
                    .orElseThrow(() -> new RuntimeException("System error: Scooter is reserved but active reservation not found"));

            // Sprawdzamy, czy rezerwacja należy do użytkownika, który próbuje zacząć przejazd
            if (!activeReservation.getUser().getId().equals(request.getUserId())) {
                throw new RuntimeException("Scooter is reserved by another user");
            }

            // Zamykamy rezerwację, bo użytkownik właśnie ją odbiera
            activeReservation.setStatus(ReservationStatus.COMPLETED);
            reservationRepository.save(activeReservation);
        }

        // Pobieramy dane użytkownika oraz przypisany do niego portfel
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User was not found"));

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet was not found"));

        // Sprawdzamy, czy na koncie jest minimalna kwota potrzebna na start
        if (wallet.getBalance().compareTo(MIN_START_BALANCE) < 0) {
            throw new RuntimeException("Insufficient funds to start the ride");
        }

        // Zmieniamy status hulajnogi na zajętą i zapisujemy zmiany w bazie
        scooter.setStatus(ScooterStatus.IN_USE);
        scooterRepository.save(scooter);

        // Rejestrujemy nowy przejazd w systemie i przypisujemy mu obecny czas
        Ride ride = new Ride();
        ride.setUser(user);
        ride.setScooter(scooter);
        ride.setStartTime(LocalDateTime.now());
        Ride savedRide = rideRepository.save(ride);

        // Pakujemy najpotrzebniejsze dane w obiekt dto, żeby odesłać je do klienta
        RideDto response = new RideDto();
        response.setRideId(savedRide.getId());
        response.setScooterId(scooter.getId());
        response.setStartTime(savedRide.getStartTime());

        return response;
    }

    @Transactional
    public RideEndResponseDto endRide(RideEndRequestDto request) {

        // szukamy przejazdu po id
        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride was not found"));

        // sprawdzamy czy przejazd nie został już wcześniej zakończony
        if (ride.getEndTime() != null) {
            throw new RuntimeException("This ride is already finished");
        }

        // rejestrujemy czas zakończenia i przekazany dystans
        LocalDateTime endTime = LocalDateTime.now();
        ride.setEndTime(endTime);
        ride.setDistance(request.getDistance());

        // obliczamy czas trwania w minutach
        long durationInMinutes = Duration.between(ride.getStartTime(), endTime).toMinutes();

        // jeśli ktoś jeździł krócej niż minutę to i tak liczymy jako jedną żeby nie było darmowych przejazdów
        if (durationInMinutes == 0) {
            durationInMinutes = 1;
        }

        // obliczamy koszt na podstawie czasu
        BigDecimal totalCost = PRICE_PER_MINUTE.multiply(BigDecimal.valueOf(durationInMinutes));
        ride.setTotalCost(totalCost);

        // blokujemy portfel żeby zapobiec podwójnemu wydaniu środków w tym samym ułamku sekundy
        Wallet wallet = walletRepository.findByUserIdWithLock(ride.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Wallet was not found"));

        // ściągamy pieniądze z konta nawet jeśli wejdzie na minus
        wallet.setBalance(wallet.getBalance().subtract(totalCost));
        walletRepository.save(wallet);

        // tworzymy zapis o transakcji dla historii płatności
        Transaction paymentTransaction = new Transaction();
        paymentTransaction.setWallet(wallet);
        paymentTransaction.setRide(ride);
        paymentTransaction.setAmount(totalCost);
        paymentTransaction.setType(TransactionType.RIDE_PAYMENT);
        paymentTransaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(paymentTransaction);

        // zwalniamy hulajnogę dla innych użytkowników
        Scooter scooter = ride.getScooter();
        scooter.setStatus(ScooterStatus.AVAILABLE);
        scooterRepository.save(scooter);

        // zapisujemy zaktualizowany przejazd
        rideRepository.save(ride);

        // przygotowujemy odpowiedź z paragonem
        RideEndResponseDto response = new RideEndResponseDto();
        response.setRideId(ride.getId());
        response.setTotalCost(totalCost);
        response.setEndTime(endTime);
        response.setDurationInMinutes(durationInMinutes);

        return response;
    }
}