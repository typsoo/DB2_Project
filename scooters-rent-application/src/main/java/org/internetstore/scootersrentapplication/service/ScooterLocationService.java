package org.internetstore.scootersrentapplication.service;

import org.locationtech.jts.geom.Polygon;
import org.internetstore.scootersrentapplication.entity.Scooter;

import java.util.List;

public interface ScooterLocationService {
    void updateLocation(Integer scooterId, double lat, double lon);
    List<Scooter> findAvailableInArea(Polygon bbox);
    public Polygon createBoundingBox(double minLat, double minLon, double maxLat, double maxLon);
    }