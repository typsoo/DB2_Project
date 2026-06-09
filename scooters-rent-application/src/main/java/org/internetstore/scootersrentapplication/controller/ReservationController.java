package org.internetstore.scootersrentapplication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.internetstore.scootersrentapplication.dto.ReservationCreateRequestDto;
import org.internetstore.scootersrentapplication.dto.ReservationResponseDto;
import org.internetstore.scootersrentapplication.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.internetstore.scootersrentapplication.entity.User;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * New reservation creating
     * POST /api/reservations
     */
    @PostMapping
    public ResponseEntity<ReservationResponseDto> createReservation(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ReservationCreateRequestDto request) {

        ReservationResponseDto response = reservationService.reserveScooter(user.getId(), request.scooterId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieving the user's current active reservation (For frontend)
     * GET /api/v1/reservations/me/active
     */
    @GetMapping("/me/active")
    public ResponseEntity<ReservationResponseDto> getActiveReservation(
            @AuthenticationPrincipal User user) {

        return reservationService.getActiveUserReservation(user.getId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Cancellation of a reservation by the user
     * POST /api/v1/reservations/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Integer reservationId) {

        reservationService.cancelReservation(user.getId(), reservationId);

        return ResponseEntity.noContent().build();    }
}