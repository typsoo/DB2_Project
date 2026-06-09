package org.internetstore.scootersrentapplication.service;

import org.internetstore.scootersrentapplication.dto.ScooterCreateDto;
import org.internetstore.scootersrentapplication.entity.Scooter;
import org.internetstore.scootersrentapplication.entity.enums.ScooterStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // Automatically rolls back changes after each test!
public class ScooterServiceIntegrationTest {

    @Autowired
    private ScooterService scooterService;

    @Test
    public void testCreateScooter() {
        ScooterCreateDto dto = new ScooterCreateDto("TEST-123", 52.2297, 21.0122);

        Scooter created = scooterService.createScooter(dto);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getSerialNumber()).isEqualTo("TEST-123");
        assertThat(created.getStatus()).isEqualTo(ScooterStatus.AVAILABLE);
        assertThat(created.getChargeLevel()).isEqualTo(100);
    }

    @Test
    public void testGetScooterById() {
        ScooterCreateDto dto = new ScooterCreateDto("TEST-456", 52.2297, 21.0122);
        Scooter created = scooterService.createScooter(dto);

        Scooter fetched = scooterService.getScooterById(created.getId());

        assertThat(fetched).isNotNull();
        assertThat(fetched.getSerialNumber()).isEqualTo("TEST-456");
    }
}
