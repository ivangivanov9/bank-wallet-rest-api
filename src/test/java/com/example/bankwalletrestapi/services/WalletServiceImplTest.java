package com.example.bankwalletrestapi.services;

import com.example.bankwalletrestapi.external.client.ExchangeRateClient;
import com.example.bankwalletrestapi.models.dtos.userDtos.UserResponseDto;
import com.example.bankwalletrestapi.models.dtos.walletDtos.MoneyOperationDto;
import com.example.bankwalletrestapi.models.dtos.walletDtos.TransferDto;
import com.example.bankwalletrestapi.models.entities.User;
import com.example.bankwalletrestapi.models.entities.Wallet;
import com.example.bankwalletrestapi.repositories.UserRepository;
import com.example.bankwalletrestapi.repositories.WalletRepository;
import com.example.bankwalletrestapi.services.impl.WalletServiceImpl;
import com.example.bankwalletrestapi.utils.DtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DtoMapper dtoMapper;

    @Mock
    private ExchangeRateClient exchangeRateClient;

    @InjectMocks
    private WalletServiceImpl walletService;

    private User sourceUser;
    private User targetUser;
    private Wallet sourceWallet;
    private Wallet targetWallet;
    private MoneyOperationDto depositDto;
    private MoneyOperationDto withdrawDto;
    private TransferDto transferDto;
    private UserResponseDto responseDto;

    @BeforeEach
    void setUp() {
        sourceUser = new User();
        sourceUser.setId(1L);
        sourceUser.setUsername("source");

        sourceWallet = new Wallet();
        sourceWallet.setId(1L);
        sourceWallet.setBalance(new BigDecimal("1000.00"));
        sourceWallet.setUser(sourceUser);
        sourceUser.setWallet(sourceWallet);

        targetUser = new User();
        targetUser.setId(2L);
        targetUser.setUsername("target");

        targetWallet = new Wallet();
        targetWallet.setId(2L);
        targetWallet.setBalance(new BigDecimal("500.00"));
        targetWallet.setUser(targetUser);
        targetUser.setWallet(targetWallet);

        depositDto = new MoneyOperationDto();
        depositDto.setAmount(new BigDecimal("100.00"));
        depositDto.setCurrency("EUR");

        withdrawDto = new MoneyOperationDto();
        withdrawDto.setAmount(new BigDecimal("50.00"));

        transferDto = new TransferDto();
        transferDto.setTargetUserId(2L);
        transferDto.setAmount(new BigDecimal("200.00"));

        responseDto = UserResponseDto.builder()
                .id(1L)
                .username("source")
                .walletBalance(new BigDecimal("1000.00"))
                .build();
    }

    @Test
    void deposit_Success_ShouldIncreaseBalance() {
        // Given
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(sourceWallet));
        when(exchangeRateClient.convertToEur("EUR", new BigDecimal("100.00"))).thenReturn(new BigDecimal("100.00"));
        when(dtoMapper.toUserResponse(sourceUser)).thenReturn(responseDto);

        // When
        UserResponseDto result = walletService.deposit(1L, depositDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(sourceWallet.getBalance()).isEqualTo(new BigDecimal("1100.00"));

        verify(walletRepository, times(1)).findByUserIdWithLock(1L);
        verify(exchangeRateClient, times(1)).convertToEur("EUR", new BigDecimal("100.00"));
    }

    @Test
    void deposit_WithUSD_ShouldConvertAndIncreaseBalance() {
        // Given
        depositDto.setCurrency("USD");
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(sourceWallet));
        when(exchangeRateClient.convertToEur("USD", new BigDecimal("100.00"))).thenReturn(new BigDecimal("94.50"));
        when(dtoMapper.toUserResponse(sourceUser)).thenReturn(responseDto);

        // When
        UserResponseDto result = walletService.deposit(1L, depositDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(sourceWallet.getBalance()).isEqualTo(new BigDecimal("1094.50"));

        verify(exchangeRateClient, times(1)).convertToEur("USD", new BigDecimal("100.00"));
    }

    @Test
    void deposit_WalletNotFound_ShouldThrowException() {
        // Given
        when(walletRepository.findByUserIdWithLock(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> walletService.deposit(999L, depositDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Wallet not found for user ID: 999");
    }

    @Test
    void withdraw_Success_ShouldDecreaseBalance() {
        // Given
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(sourceWallet));
        when(dtoMapper.toUserResponse(sourceUser)).thenReturn(responseDto);

        // When
        UserResponseDto result = walletService.withdraw(1L, withdrawDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(sourceWallet.getBalance()).isEqualTo(new BigDecimal("950.00"));
    }

    @Test
    void withdraw_WithInsufficientFunds_ShouldThrowException() {
        // Given
        withdrawDto.setAmount(new BigDecimal("2000.00"));
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(sourceWallet));

        // When/Then
        assertThatThrownBy(() -> walletService.withdraw(1L, withdrawDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Insufficient funds");
    }

    @Test
    void transfer_Success_ShouldUpdateBothBalances() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findByUserIdWithLock(2L)).thenReturn(Optional.of(targetWallet));

        when(dtoMapper.toUserResponse(sourceUser)).thenReturn(responseDto);

        // When
        UserResponseDto result = walletService.transfer(1L, transferDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(sourceWallet.getBalance()).isEqualTo(new BigDecimal("800.00"));
        assertThat(targetWallet.getBalance()).isEqualTo(new BigDecimal("700.00"));

        verify(walletRepository, times(2)).findByUserIdWithLock(anyLong());
    }

    @Test
    void transfer_ToSameUser_ShouldThrowException() {
        // Given
        transferDto.setTargetUserId(1L);

        // When/Then
        assertThatThrownBy(() -> walletService.transfer(1L, transferDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cannot transfer money to the same user");
    }

    @Test
    void transfer_SourceUserNotFound_ShouldThrowException() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> walletService.transfer(1L, transferDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Source user not found");
    }

    @Test
    void transfer_TargetUserNotFound_ShouldThrowException() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> walletService.transfer(1L, transferDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Target user not found");
    }

    @Test
    void transfer_WithInsufficientFunds_ShouldThrowException() {
        // Given
        transferDto.setAmount(new BigDecimal("2000.00"));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findByUserIdWithLock(2L)).thenReturn(Optional.of(targetWallet));

        // When/Then
        assertThatThrownBy(() -> walletService.transfer(1L, transferDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Insufficient funds for transfer");
    }

    @Test
    void getBalance_Success_ShouldReturnBalance() {
        // Given
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(sourceWallet));

        // When
        BigDecimal balance = walletService.getBalance(1L);

        // Then
        assertThat(balance).isEqualTo(new BigDecimal("1000.00"));
        verify(walletRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getBalance_WalletNotFound_ShouldThrowException() {
        // Given
        when(walletRepository.findByUserId(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> walletService.getBalance(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Wallet not found for user ID: 999");
    }
}