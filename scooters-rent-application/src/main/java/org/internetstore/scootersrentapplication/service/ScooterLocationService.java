package org.internetstore.scootersrentapplication.service;

import org.internetstore.scootersrentapplication.entity.Scooter;

import java.util.List;

public interface ScooterLocationService {
    void updateLocation(Long scooterId, double lat, double lon);
    List<Scooter> findAvailableInRadius(double lat, double lon, double radiusKm);
}