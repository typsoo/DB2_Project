package org.internetstore.scootersrentapplication.service;

import org.internetstore.scootersrentapplication.dto.ScooterCreateDto;
import org.internetstore.scootersrentapplication.dto.ScooterLocationUpdateDto;
import org.internetstore.scootersrentapplication.dto.ScooterUpdateDto;
import org.internetstore.scootersrentapplication.entity.Scooter;
import org.internetstore.scootersrentapplication.entity.enums.ScooterStatus;
import org.internetstore.scootersrentapplication.repository.ScooterRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ScooterService {

    private final ScooterRepository scooterRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public ScooterService(ScooterRepository scooterRepository) {
        this.scooterRepository = scooterRepository;
    }

    @Transactional
    public Scooter createScooter(ScooterCreateDto dto) {
        Scooter scooter = new Scooter();
        scooter.setSerialNumber(dto.serialNumber());

        scooter.setLocation(geometryFactory.createPoint(
                new Coordinate(dto.longitude(), dto.latitude())
        ));


        scooter.setChargeLevel(100);
        scooter.setStatus(ScooterStatus.AVAILABLE);

        return scooterRepository.save(scooter);
    }

    public Scooter getScooterById(Integer id) {
        return scooterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scooter with id " + id + " not found"));
    }

    public List<Scooter> getAllScooters() {
        return scooterRepository.findAll();
    }

    @Transactional
    public Scooter updateScooter(Integer id, ScooterUpdateDto dto) {
        Scooter scooter = scooterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Scooter not found"));

        if (dto.chargeLevel() != null) {
            scooter.setChargeLevel(dto.chargeLevel());
        }
        if (dto.status() != null) {
            scooter.setStatus(dto.status());
        }

        return scooterRepository.save(scooter);
    }

    @Transactional
    public void deleteScooter(Integer id) {
        if (!scooterRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Scooter not found");
        }

        scooterRepository.deleteById(id);
    }
}