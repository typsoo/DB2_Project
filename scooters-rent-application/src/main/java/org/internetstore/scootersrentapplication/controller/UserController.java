package org.internetstore.scootersrentapplication.controller;

import lombok.RequiredArgsConstructor;
import org.internetstore.scootersrentapplication.dto.UserProfileDto;
import org.internetstore.scootersrentapplication.dto.UserResponseDto;
import org.internetstore.scootersrentapplication.entity.User;
import org.internetstore.scootersrentapplication.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /api/users/me
    // Retrieves the profile and wallet balance of the currently authenticated user
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getUserProfile(
            @AuthenticationPrincipal User user
    ) {
        UserProfileDto profile = userService.getUserProfile(user.getId());
        return ResponseEntity.ok(profile);
    }

    // GET /api/users
    // Retrieves a list of all users in the system (ADMIN only)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET /api/users/{id}
    // Retrieves a specific user's details by ID (ADMIN only)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}