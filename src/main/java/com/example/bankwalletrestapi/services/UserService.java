package com.example.bankwalletrestapi.services;

import com.example.bankwalletrestapi.models.dtos.userDtos.UserCreateDto;
import com.example.bankwalletrestapi.models.dtos.userDtos.UserResponseDto;
import com.example.bankwalletrestapi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto createUser(UserCreateDto userCreateDto) {
        // TODO
        return null;
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        // TODO
        return null;
    }

    public void deleteUser(Long id) {
        // TODO
    }
}