package org.internetstore.scootersrentapplication.service;

import org.internetstore.scootersrentapplication.dto.UserProfileDto;
import org.internetstore.scootersrentapplication.dto.UserRegisterDto;
import org.internetstore.scootersrentapplication.dto.UserResponseDto;
import org.internetstore.scootersrentapplication.entity.User;
import org.internetstore.scootersrentapplication.entity.Wallet;
import org.internetstore.scootersrentapplication.repository.UserRepository;
import org.internetstore.scootersrentapplication.repository.WalletRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, WalletRepository walletRepository, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(UserRegisterDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalStateException("User with this email already exists");
        }

        User user = new User();
        user.setEmail(dto.email());
        user.setPasswordHash(passwordEncoder.encode(dto.password()));
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());

        User savedUser = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(BigDecimal.ZERO);

        walletRepository.save(wallet);

        return savedUser;
    }

    public UserProfileDto getUserProfile(Integer id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found for this user"));

        return new UserProfileDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                wallet.getBalance(),
                user.getCreatedAt()
        );
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return mapToUserResponseDto(user);
    }

    private UserResponseDto mapToUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}