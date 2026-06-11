package org.internetstore.scootersrentapplication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.internetstore.scootersrentapplication.dto.RideDto;
import org.internetstore.scootersrentapplication.dto.RideEndRequestDto;
import org.internetstore.scootersrentapplication.dto.RideEndResponseDto;
import org.internetstore.scootersrentapplication.dto.RideHistoryResponseDto;
import org.internetstore.scootersrentapplication.dto.RideStartRequestDto;
import org.internetstore.scootersrentapplication.service.RideService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.internetstore.scootersrentapplication.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    // GET /api/rides
    // Retrieves all rides in the system (ADMIN only)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<RideHistoryResponseDto>> getAllRides() {
        return ResponseEntity.ok(rideService.getAllRides());
    }

    // GET /api/rides/me
    // Retrieves ride history for the currently authenticated user
    @GetMapping("/me")
    public ResponseEntity<List<RideHistoryResponseDto>> getMyRides(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(rideService.getUserRideHistory(user.getId()));
    }

    // POST /api/rides/start
    // Starts a new ride for the authenticated user
    @PostMapping("/start")
    public ResponseEntity<RideDto> startRide(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody RideStartRequestDto request
    ) {
        RideDto rideDto = rideService.startRide(user, request);
        return ResponseEntity.ok(rideDto);
    }

    // POST /api/rides/end
    // Ends an active ride
    @PostMapping("/end")
    public ResponseEntity<RideEndResponseDto> endRide(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody RideEndRequestDto request
    ) {
        return ResponseEntity.ok(rideService.endRide(request));
    }
}
