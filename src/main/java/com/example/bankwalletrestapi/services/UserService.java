package com.example.bankwalletrestapi.services;

import com.example.bankwalletrestapi.models.dtos.authDtos.RegisterDto;
import com.example.bankwalletrestapi.models.dtos.userDtos.UserCreateDto;
import com.example.bankwalletrestapi.models.dtos.userDtos.UserResponseDto;
import com.example.bankwalletrestapi.models.entities.User;
import com.example.bankwalletrestapi.models.entities.Wallet;
import com.example.bankwalletrestapi.repositories.UserRepository;
import com.example.bankwalletrestapi.utils.DtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final DtoMapper dtoMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto createUser(UserCreateDto userCreateDto) {
        log.info("Creating new user: {}", userCreateDto.getUsername());

        if (userRepository.existsByUsername(userCreateDto.getUsername())) {
            throw new RuntimeException("Username already exists: " + userCreateDto.getUsername());
        }

        User user = new User();
        user.setUsername(userCreateDto.getUsername());
        user.setEmail(userCreateDto.getEmail());

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUser(user);

        user.setWallet(wallet);

        User savedUser = userRepository.save(user);
        log.info("Successfully created user with ID: {}, balance: {} EUR",
                savedUser.getId(), savedUser.getWallet().getBalance());

        return dtoMapper.toUserResponse(savedUser);
    }

    public UserResponseDto registerUser(RegisterDto registerDto) {
        log.info("Registering new user: {}", registerDto.getUsername());

        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User(
                registerDto.getUsername(),
                registerDto.getEmail(),
                passwordEncoder.encode(registerDto.getPassword())
        );

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUser(user);
        user.setWallet(wallet);

        User savedUser = userRepository.save(user);
        return dtoMapper.toUserResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        log.info("Searching for user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        return dtoMapper.toUserResponse(user);
    }

    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }

        userRepository.deleteById(id);
        log.info("User with ID {} has been deleted", id);
    }
}