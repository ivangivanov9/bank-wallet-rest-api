package com.example.bankwalletrestapi.controllers;

import com.example.bankwalletrestapi.models.dtos.userDtos.UserCreateDto;
import com.example.bankwalletrestapi.models.dtos.userDtos.UserResponseDto;
import com.example.bankwalletrestapi.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        log.info("REST request to create user: {}", userCreateDto.getUsername());
        UserResponseDto createdUser = userService.createUser(userCreateDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        log.info("REST request to get user by id: {}", id);
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("REST request to delete user by id: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
