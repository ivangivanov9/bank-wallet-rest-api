package com.example.bankwalletrestapi.services;

import com.example.bankwalletrestapi.models.dtos.authDtos.RegisterDto;
import com.example.bankwalletrestapi.models.dtos.userDtos.UserCreateDto;
import com.example.bankwalletrestapi.models.dtos.userDtos.UserResponseDto;
import com.example.bankwalletrestapi.models.entities.User;
import com.example.bankwalletrestapi.models.entities.Wallet;
import com.example.bankwalletrestapi.models.enums.Role;
import com.example.bankwalletrestapi.repositories.UserRepository;
import com.example.bankwalletrestapi.services.impl.UserServiceImpl;
import com.example.bankwalletrestapi.utils.DtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DtoMapper dtoMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserCreateDto createDto;
    private RegisterDto registerDto;
    private UserResponseDto responseDto;
    private Wallet testWallet;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword123");
        testUser.setRole(Role.USER);

        testWallet = new Wallet();
        testWallet.setId(1L);
        testWallet.setBalance(BigDecimal.ZERO);
        testWallet.setUser(testUser);
        testUser.setWallet(testWallet);

        createDto = new UserCreateDto();
        createDto.setUsername("testuser");
        createDto.setEmail("test@example.com");
        createDto.setPassword("password123");

        registerDto = new RegisterDto();
        registerDto.setUsername("testuser");
        registerDto.setEmail("test@example.com");
        registerDto.setPassword("password123");

        responseDto = UserResponseDto.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .walletBalance(BigDecimal.ZERO)
                .build();
    }

    @Test
    void createUser_Success_ShouldReturnUserResponse() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(dtoMapper.toUserResponse(any(User.class))).thenReturn(responseDto);

        // When
        UserResponseDto result = userService.createUser(createDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getWalletBalance()).isEqualTo(BigDecimal.ZERO);

        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
        verify(dtoMapper, times(1)).toUserResponse(any(User.class));
    }

    @Test
    void createUser_WithExistingUsername_ShouldThrowException() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> userService.createUser(createDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Username already exists: testuser");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_Success_ShouldReturnUserResponse() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(dtoMapper.toUserResponse(any(User.class))).thenReturn(responseDto);

        // When
        UserResponseDto result = userService.registerUser(registerDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");

        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_WithExistingUsername_ShouldThrowException() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> userService.registerUser(registerDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Username already exists");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithExistingEmail_ShouldThrowException() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> userService.registerUser(registerDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already exists");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_WithExistingId_ShouldReturnUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(dtoMapper.toUserResponse(testUser)).thenReturn(responseDto);

        // When
        UserResponseDto result = userService.getUserById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_WithNonExistingId_ShouldThrowException() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with ID: 999");
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Given
        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setUsername("seconduser");

        List<User> users = Arrays.asList(testUser, secondUser);

        UserResponseDto secondResponse = UserResponseDto.builder()
                .id(2L)
                .username("seconduser")
                .email("second@example.com")
                .walletBalance(BigDecimal.ZERO)
                .build();

        when(userRepository.findAll()).thenReturn(users);
        when(dtoMapper.toUserResponse(testUser)).thenReturn(responseDto);
        when(dtoMapper.toUserResponse(secondUser)).thenReturn(secondResponse);

        // When
        List<UserResponseDto> result = userService.getAllUsers();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");
        assertThat(result.get(1).getUsername()).isEqualTo("seconduser");

        verify(userRepository, times(1)).findAll();
        verify(dtoMapper, times(2)).toUserResponse(any(User.class));
    }

    @Test
    void deleteUser_WithExistingId_ShouldSucceed() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_WithNonExistingId_ShouldThrowException() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with ID: 999");

        verify(userRepository, never()).deleteById(anyLong());
    }
}