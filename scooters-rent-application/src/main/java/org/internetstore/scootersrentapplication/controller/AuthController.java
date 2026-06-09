package org.internetstore.scootersrentapplication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.internetstore.scootersrentapplication.dto.AuthenticationRequestDto;
import org.internetstore.scootersrentapplication.dto.AuthenticationResponseDto;
import org.internetstore.scootersrentapplication.dto.UserRegisterDto;
import org.internetstore.scootersrentapplication.entity.User;
import org.internetstore.scootersrentapplication.service.AuthService;
import org.internetstore.scootersrentapplication.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRegisterDto dto) {
        User createdUser = userService.registerUser(dto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @Valid @RequestBody AuthenticationRequestDto request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
