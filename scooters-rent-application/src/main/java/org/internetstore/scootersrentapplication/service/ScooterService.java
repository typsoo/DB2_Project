package org.internetstore.scootersrentapplication.service;

import org.internetstore.scootersrentapplication.dto.LocationUpdateDto;
import org.internetstore.scootersrentapplication.dto.ScooterCreateDto;
import org.internetstore.scootersrentapplication.entity.Scooter;
import org.internetstore.scootersrentapplication.entity.enums.ScooterStatus;
import org.internetstore.scootersrentapplication.repository.ScooterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ScooterService {

    private final ScooterRepository scooterRepository;

    public ScooterService(ScooterRepository scooterRepository) {
        this.scooterRepository = scooterRepository;
    }

    @Transactional
    public Scooter createScooter(ScooterCreateDto dto) {
        Scooter scooter = new Scooter();
        scooter.setSerialNumber(dto.getSerialNumber());
        scooter.setLatitude(dto.getLatitude());
        scooter.setLongitude(dto.getLongitude());

        scooter.setChargeLevel(100);
        scooter.setStatus(ScooterStatus.AVAILABLE);

        return scooterRepository.save(scooter);
    }

    public List<Scooter> getScooters(ScooterStatus status) {
        if (status != null) {
            return scooterRepository.findByStatus(status);
        }
        return scooterRepository.findAll();
    }

    @Transactional
    public Scooter updateLocation(Integer id, LocationUpdateDto dto) {
        Scooter scooter = scooterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Scooter not found"));

        scooter.setLatitude(dto.getLatitude());
        scooter.setLongitude(dto.getLongitude());

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