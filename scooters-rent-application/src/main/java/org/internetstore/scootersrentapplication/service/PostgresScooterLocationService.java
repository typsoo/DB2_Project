package org.internetstore.scootersrentapplication.service;

import org.internetstore.scootersrentapplication.entity.Scooter;
import org.internetstore.scootersrentapplication.repository.ScooterRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class PostgresScooterLocationService implements ScooterLocationService {

    private final ScooterRepository scooterRepository;

    public PostgresScooterLocationService(ScooterRepository scooterRepository) {
        this.scooterRepository = scooterRepository;
    }

    @Override
    public void updateLocation(Long scooterId, double lat, double lon) {

    }

    @Override
    public List<Scooter> findAvailableInRadius(double lat, double lon, double radiusKm) {
        return List.of();
    }
}