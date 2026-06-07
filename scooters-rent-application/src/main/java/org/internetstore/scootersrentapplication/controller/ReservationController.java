package org.internetstore.scootersrentapplication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.internetstore.scootersrentapplication.dto.ReservationDto;
import org.internetstore.scootersrentapplication.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * New reservation creating
     * POST /api/v1/reservations
     */
    @PostMapping
    public ResponseEntity<ReservationDto.Response> createReservation(
            @RequestHeader("X-User-Id") Integer userId, // We should take it from Security Context (JWT)
            @Valid @RequestBody ReservationDto.CreateRequest request) {

        ReservationDto.Response response = reservationService.reserveScooter(userId, request.scooterId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieving the user's current active reservation (For frontend)
     * GET /api/v1/reservations/me/active
     */
    @GetMapping("/me/active")
    public ResponseEntity<ReservationDto.Response> getActiveReservation(
            @RequestHeader("X-User-Id") Integer userId) {

        return reservationService.getActiveUserReservation(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Cancellation of a reservation by the user
     * POST /api/v1/reservations/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(
            @RequestHeader("X-User-Id") Integer userId,
            @PathVariable("id") Integer reservationId) {

        reservationService.cancelReservation(userId, reservationId);

        return ResponseEntity.noContent().build();    }
}