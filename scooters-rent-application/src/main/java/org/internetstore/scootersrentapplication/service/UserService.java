package org.internetstore.scootersrentapplication.service;

import org.internetstore.scootersrentapplication.dto.UserProfileDto;
import org.internetstore.scootersrentapplication.dto.UserRegisterDto;
import org.internetstore.scootersrentapplication.entity.User;
import org.internetstore.scootersrentapplication.entity.Wallet;
import org.internetstore.scootersrentapplication.repository.UserRepository;
import org.internetstore.scootersrentapplication.repository.WalletRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public UserService(UserRepository userRepository, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public User registerUser(UserRegisterDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

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

        UserProfileDto profileDto = new UserProfileDto();
        profileDto.setId(user.getId());
        profileDto.setEmail(user.getEmail());
        profileDto.setFirstName(user.getFirstName());
        profileDto.setLastName(user.getLastName());
        profileDto.setCreatedAt(user.getCreatedAt());

        profileDto.setBalance(wallet.getBalance());

        return profileDto;
    }
}