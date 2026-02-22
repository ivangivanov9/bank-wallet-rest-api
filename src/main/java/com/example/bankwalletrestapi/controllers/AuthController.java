package com.example.bankwalletrestapi.controllers;

import com.example.bankwalletrestapi.models.dtos.authDtos.AuthResponseDto;
import com.example.bankwalletrestapi.models.dtos.authDtos.LoginDto;
import com.example.bankwalletrestapi.models.dtos.authDtos.RegisterDto;
import com.example.bankwalletrestapi.models.dtos.userDtos.UserResponseDto;
import com.example.bankwalletrestapi.security.CustomUserDetails;
import com.example.bankwalletrestapi.security.JwtService;
import com.example.bankwalletrestapi.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(user.getUsername());

        AuthResponseDto response = new AuthResponseDto(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "")
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody RegisterDto request) {
        UserResponseDto response = userService.registerUser(request);
        return ResponseEntity.ok(response);
    }
}