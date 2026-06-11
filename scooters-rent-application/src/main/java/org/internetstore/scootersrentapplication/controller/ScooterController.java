package org.internetstore.scootersrentapplication.controller;

import lombok.RequiredArgsConstructor;
import org.internetstore.scootersrentapplication.dto.ScooterCreateDto;
import org.internetstore.scootersrentapplication.dto.ScooterLocationUpdateDto;
import org.internetstore.scootersrentapplication.dto.ScooterResponseDto;
import org.internetstore.scootersrentapplication.dto.ScooterUpdateDto;
import org.internetstore.scootersrentapplication.entity.Scooter;
import org.internetstore.scootersrentapplication.service.ScooterLocationService;
import org.internetstore.scootersrentapplication.service.ScooterService;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/scooters")
@RequiredArgsConstructor
public class ScooterController {

    private final ScooterService scooterService;
    private final ScooterLocationService locationService;

    // GET /api/scooters
    // Retrieves a list of all scooters
    @GetMapping
    public ResponseEntity<List<ScooterResponseDto>> getAllScooters() {
        List<ScooterResponseDto> response = scooterService.getAllScooters().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // POST /api/scooters
    // Creates a new scooter (ADMIN only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ScooterResponseDto> createScooter(
            @Valid @RequestBody ScooterCreateDto dto
    ) {
        Scooter createdScooter = scooterService.createScooter(dto);
        return new ResponseEntity<>(mapToDto(createdScooter), HttpStatus.CREATED);
    }

    // GET /api/scooters/{id}
    // Retrieves details of a specific scooter by ID
    @GetMapping("/{id}")
    public ResponseEntity<ScooterResponseDto> getScooterById(
            @PathVariable Integer id
    ) {
        Scooter scooter = scooterService.getScooterById(id);
        return ResponseEntity.ok(mapToDto(scooter));
    }

    // PUT /api/scooters/{id}
    // Updates details of a specific scooter (ADMIN only)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ScooterResponseDto> updateScooter(
            @PathVariable Integer id,
            @Valid @RequestBody ScooterUpdateDto dto
    ) {
        Scooter updatedScooter = scooterService.updateScooter(id, dto);
        return ResponseEntity.ok(mapToDto(updatedScooter));
    }

    // DELETE /api/scooters/{id}
    // Deletes a specific scooter by ID (ADMIN only)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScooter(
            @PathVariable Integer id
    ) {
        scooterService.deleteScooter(id);
        return ResponseEntity.noContent().build();
    }

    // PUT /api/scooters/{id}/location
    // Updates the GPS location of a scooter (ADMIN only)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/location")
    public ResponseEntity<Void> updateLocation(
            @PathVariable Integer id,
            @Valid @RequestBody ScooterLocationUpdateDto dto
    ) {
        locationService.updateLocation(id, dto.latitude(), dto.longitude());
        return ResponseEntity.ok().build();
    }

    // GET /api/scooters/area
    // Retrieves all available scooters within a specific geographic bounding box
    @GetMapping("/area")
    public ResponseEntity<List<ScooterResponseDto>> getScootersInArea(
            @RequestParam double minLat,
            @RequestParam double minLon,
            @RequestParam double maxLat,
            @RequestParam double maxLon
    ) {
        Polygon boundingBox = locationService.createBoundingBox(minLat, minLon, maxLat, maxLon);
        List<Scooter> scooters = locationService.findAvailableInArea(boundingBox);

        List<ScooterResponseDto> response = scooters.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private ScooterResponseDto mapToDto(Scooter scooter) {
        Point loc = scooter.getLocation();
        return new ScooterResponseDto(
                scooter.getId(),
                scooter.getSerialNumber(),
                scooter.getChargeLevel(),
                scooter.getStatus().name(),
                loc != null ? scooter.getLocation().getY() : null,
                loc != null ? scooter.getLocation().getX() : null
        );
    }
}