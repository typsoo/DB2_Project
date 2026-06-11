package org.internetstore.scootersrentapplication.controller;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@Transactional // Rollback changes in Neon DB!
public class RideControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

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
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testUser = new User();
        testUser.setEmail("testcloud@test.com");
        testUser.setPasswordHash("hash");
        testUser.setFirstName("Cloud");
        testUser.setLastName("Tester");
        testUser = userRepository.save(testUser);

        Wallet wallet = new Wallet();
        wallet.setUser(testUser);
        wallet.setBalance(new BigDecimal("50.00"));
        walletRepository.save(wallet);

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        testScooter = new Scooter();
        testScooter.setSerialNumber("CLOUD-999");
        testScooter.setStatus(ScooterStatus.AVAILABLE);
        testScooter.setChargeLevel(100);
        testScooter.setLocation(geometryFactory.createPoint(new Coordinate(21.0, 52.0)));
        testScooter = scooterRepository.save(testScooter);
    }

    @Test
    public void testStartRide_Success() throws Exception {
        String json = "{\"scooterId\": " + testScooter.getId() + "}";

        mockMvc.perform(post("/api/rides/start")
                .with(user(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rideId").exists())
                .andExpect(jsonPath("$.scooterId").value(testScooter.getId()));
    }

    @Test
    public void testStartRide_FailsWhenInsufficientBalance() throws Exception {
        Wallet wallet = walletRepository.findByUserId(testUser.getId()).get();
        wallet.setBalance(new BigDecimal("5.00"));
        walletRepository.save(wallet);

        String json = "{\"scooterId\": " + testScooter.getId() + "}";

        mockMvc.perform(post("/api/rides/start")
                .with(user(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient funds to start the ride"));
    }

    @Test
    public void testEndRide_Success() throws Exception {
        String startJson = "{\"scooterId\": " + testScooter.getId() + "}";
        String response = mockMvc.perform(post("/api/rides/start")
                .with(user(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(startJson))
                .andReturn().getResponse().getContentAsString();

        // Manual extraction of ID
        String idStr = response.split("\"rideId\":")[1].split(",")[0].trim();
        
        String endJson = "{\"rideId\": " + idStr + ", \"distance\": 2.5}";

        mockMvc.perform(post("/api/rides/end")
                .with(user(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(endJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rideId").value(Integer.parseInt(idStr)))
                .andExpect(jsonPath("$.totalCost").exists());
    }
}
