package org.internetstore.scootersrentapplication.service;

import lombok.RequiredArgsConstructor;
import org.internetstore.scootersrentapplication.entity.Scooter;
import org.internetstore.scootersrentapplication.entity.enums.ScooterStatus;
import org.internetstore.scootersrentapplication.repository.ScooterRepository;
import org.locationtech.jts.geom.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScooterLocationService {

    private final ScooterRepository scooterRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Transactional
    public void updateLocation(Integer scooterId, double lat, double lon) {

        Scooter scooter = scooterRepository.findById(scooterId)
                .orElseThrow(() -> new IllegalArgumentException("Scooter was not found with id: " + scooterId));

        Point newLocation = geometryFactory.createPoint(new Coordinate(lon, lat));

        scooter.setLocation(newLocation);
    }

    @Transactional(readOnly = true)
    public List<Scooter> findAvailableInArea(Polygon bbox) {
        return scooterRepository.findAvailableInArea(ScooterStatus.AVAILABLE, bbox);
    }

    public Polygon createBoundingBox(double minLat, double minLon, double maxLat, double maxLon) {
        Coordinate[] coords = new Coordinate[] {
                new Coordinate(minLon, minLat),
                new Coordinate(minLon, maxLat),
                new Coordinate(maxLon, maxLat),
                new Coordinate(maxLon, minLat),
                new Coordinate(minLon, minLat)
        };
        return geometryFactory.createPolygon(coords);
    }
}