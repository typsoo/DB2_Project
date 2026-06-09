package org.internetstore.scootersrentapplication.service;

import org.internetstore.scootersrentapplication.dto.RideDto;
import org.internetstore.scootersrentapplication.dto.RideEndRequestDto;
import org.internetstore.scootersrentapplication.dto.RideEndResponseDto;
import org.internetstore.scootersrentapplication.dto.RideStartRequestDto;
import org.internetstore.scootersrentapplication.entity.Scooter;
import org.internetstore.scootersrentapplication.entity.User;
import org.internetstore.scootersrentapplication.entity.Wallet;
import org.internetstore.scootersrentapplication.entity.enums.ScooterStatus;
import org.internetstore.scootersrentapplication.repository.ScooterRepository;
import org.internetstore.scootersrentapplication.repository.UserRepository;
import org.internetstore.scootersrentapplication.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional // Automatically rolls back changes after each test!
public class RideServiceIntegrationTest {

    @Autowired
    private RideService rideService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ScooterRepository scooterRepository;

    private User testUser;
    private Scooter testScooter;

    @BeforeEach
    public void setup() {
        testUser = new User();
        testUser.setEmail("test2@test.com");
        testUser.setPasswordHash("hash");
        testUser.setFirstName("Jane");
        testUser.setLastName("Doe");
        testUser = userRepository.save(testUser);

        Wallet wallet = new Wallet();
        wallet.setUser(testUser);
        wallet.setBalance(new BigDecimal("50.00")); 
        walletRepository.save(wallet);

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        testScooter = new Scooter();
        testScooter.setSerialNumber("RIDE-999");
        testScooter.setStatus(ScooterStatus.AVAILABLE);
        testScooter.setChargeLevel(100);
        testScooter.setLocation(geometryFactory.createPoint(new Coordinate(21.0, 52.0)));
        testScooter = scooterRepository.save(testScooter);
    }

    @Test
    public void testStartRide_Success() {
        RideStartRequestDto request = new RideStartRequestDto(testUser.getId(), testScooter.getId());

        RideDto response = rideService.startRide(request);

        assertThat(response).isNotNull();
        assertThat(response.scooterId()).isEqualTo(testScooter.getId());
        
        Scooter updatedScooter = scooterRepository.findById(testScooter.getId()).get();
        assertThat(updatedScooter.getStatus()).isEqualTo(ScooterStatus.IN_USE);
    }

    @Test
    public void testStartRide_FailsWhenInsufficientBalance() {
        Wallet wallet = walletRepository.findByUserId(testUser.getId()).get();
        wallet.setBalance(new BigDecimal("5.00"));
        walletRepository.save(wallet);

        RideStartRequestDto request = new RideStartRequestDto(testUser.getId(), testScooter.getId());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            rideService.startRide(request);
        });

        assertThat(exception.getMessage()).isEqualTo("Insufficient funds to start the ride");
    }

    @Test
    public void testEndRide_Success() {
        RideStartRequestDto startRequest = new RideStartRequestDto(testUser.getId(), testScooter.getId());
        RideDto startResponse = rideService.startRide(startRequest);
        
        Integer rideId = startResponse.rideId();
        RideEndRequestDto endRequest = new RideEndRequestDto(rideId, 2.5);

        RideEndResponseDto endResponse = rideService.endRide(endRequest);

        assertThat(endResponse).isNotNull();
        assertThat(endResponse.rideId()).isEqualTo(rideId);
        assertThat(endResponse.totalCost()).isNotNull();
        
        Scooter updatedScooter = scooterRepository.findById(testScooter.getId()).get();
        assertThat(updatedScooter.getStatus()).isEqualTo(ScooterStatus.AVAILABLE);
    }
}
