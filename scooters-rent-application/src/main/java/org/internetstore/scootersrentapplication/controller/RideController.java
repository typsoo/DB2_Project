package org.internetstore.scootersrentapplication.controller;

import jakarta.validation.Valid;
import org.internetstore.scootersrentapplication.dto.RideDto;
import org.internetstore.scootersrentapplication.dto.RideEndRequestDto;
import org.internetstore.scootersrentapplication.dto.RideStartRequestDto;
import org.internetstore.scootersrentapplication.service.RideService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.internetstore.scootersrentapplication.entity.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping("/start")
    public ResponseEntity<?> startRide(@AuthenticationPrincipal User user, @Valid @RequestBody RideStartRequestDto request) {
        try {
            RideDto rideDto = rideService.startRide(user, request);
            return ResponseEntity.ok(rideDto);
        } catch (RuntimeException e) {
            // return error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/end")
    public ResponseEntity<?> endRide(@Valid @RequestBody RideEndRequestDto request) {
        try {
            return ResponseEntity.ok(rideService.endRide(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
