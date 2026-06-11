package org.internetstore.scootersrentapplication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.internetstore.scootersrentapplication.dto.ReservationCreateRequestDto;
import org.internetstore.scootersrentapplication.dto.ReservationResponseDto;
import org.internetstore.scootersrentapplication.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.internetstore.scootersrentapplication.entity.User;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // GET /api/reservations
    // Retrieves a list of all reservations in the system (ADMIN only)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ReservationResponseDto>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    // GET /api/reservations/me
    // Retrieves reservation history for the currently authenticated user
    @GetMapping("/me")
    public ResponseEntity<List<ReservationResponseDto>> getMyReservations(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(reservationService.getUserReservationHistory(user.getId()));
    }

    // GET /api/reservations/me/active
    // Retrieves the user's current active reservation
    @GetMapping("/me/active")
    public ResponseEntity<ReservationResponseDto> getActiveReservation(
            @AuthenticationPrincipal User user
    ) {
        return reservationService.getActiveUserReservation(user.getId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/reservations
    // Creates a new reservation for a scooter
    @PostMapping
    public ResponseEntity<ReservationResponseDto> createReservation(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ReservationCreateRequestDto request
    ) {
        ReservationResponseDto response = reservationService.reserveScooter(user.getId(), request.scooterId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // POST /api/reservations/{id}/cancel
    // Cancels an active reservation
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Integer reservationId
    ) {
        reservationService.cancelReservation(user.getId(), reservationId);
        return ResponseEntity.noContent().build();
    }
}