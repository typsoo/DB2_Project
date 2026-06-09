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



    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getUserProfile(@org.springframework.security.core.annotation.AuthenticationPrincipal User user) {
        UserProfileDto profile = userService.getUserProfile(user.getId());

        return ResponseEntity.ok(profile);
    }
}