package org.internetstore.scootersrentapplication.service;

import lombok.RequiredArgsConstructor;
import org.internetstore.scootersrentapplication.dto.AuthenticationRequestDto;
import org.internetstore.scootersrentapplication.dto.AuthenticationResponseDto;
import org.internetstore.scootersrentapplication.repository.UserRepository;
import org.internetstore.scootersrentapplication.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User; // Wait, we have our own User entity, so we need to be careful with imports
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final org.internetstore.scootersrentapplication.repository.UserRepository userRepository;
    private final org.internetstore.scootersrentapplication.security.JwtService jwtService;
    private final org.springframework.security.authentication.AuthenticationManager authenticationManager;

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        var user = userRepository.findByEmail(request.email())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponseDto(jwtToken);
    }
}
