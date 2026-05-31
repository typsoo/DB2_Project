package org.internetstore.scootersrentapplication.controller;

import org.internetstore.scootersrentapplication.dto.LocationUpdateDto;
import org.internetstore.scootersrentapplication.dto.ScooterCreateDto;
import org.internetstore.scootersrentapplication.entity.Scooter;
import org.internetstore.scootersrentapplication.entity.enums.ScooterStatus;
import org.internetstore.scootersrentapplication.service.ScooterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scooters")
public class ScooterController {

    private final ScooterService scooterService;

    public ScooterController(ScooterService scooterService) {
        this.scooterService = scooterService;
    }

    @PostMapping
    public ResponseEntity<Scooter> createScooter(@RequestBody ScooterCreateDto dto) {
        Scooter createdScooter = scooterService.createScooter(dto);
        return new ResponseEntity<>(createdScooter, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Scooter>> getScooters(
            @RequestParam(required = false) ScooterStatus status) {

        List<Scooter> scooters = scooterService.getScooters(status);
        return ResponseEntity.ok(scooters);
    }

    @PutMapping("/{id}/location")
    public ResponseEntity<Scooter> updateLocation(
            @PathVariable Integer id,
            @RequestBody LocationUpdateDto dto) {

        Scooter updatedScooter = scooterService.updateLocation(id, dto);
        return ResponseEntity.ok(updatedScooter);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScooter(@PathVariable Integer id) {
        scooterService.deleteScooter(id);

        return ResponseEntity.noContent().build();
    }
}