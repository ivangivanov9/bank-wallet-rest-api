package com.example.bankwalletrestapi.services.interfaces;

import com.example.bankwalletrestapi.models.dtos.authDtos.RegisterDto;
import com.example.bankwalletrestapi.models.dtos.userDtos.UserCreateDto;
import com.example.bankwalletrestapi.models.dtos.userDtos.UserResponseDto;
import java.util.List;

public interface IUserService {

    UserResponseDto createUser(UserCreateDto userCreateDto);

    UserResponseDto registerUser(RegisterDto registerDto);

    UserResponseDto getUserById(Long id);

    List<UserResponseDto> getAllUsers();

    void deleteUser(Long id);
}