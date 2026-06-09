package org.internetstore.scootersrentapplication.controller;

import org.internetstore.scootersrentapplication.dto.UserProfileDto;
import org.internetstore.scootersrentapplication.dto.UserRegisterDto;
import org.internetstore.scootersrentapplication.entity.User;
import org.internetstore.scootersrentapplication.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> register(@Valid @RequestBody UserRegisterDto dto) {
        User createdUser = userService.registerUser(dto);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Integer id) {
        UserProfileDto profile = userService.getUserProfile(id);

        return ResponseEntity.ok(profile);
    }
}