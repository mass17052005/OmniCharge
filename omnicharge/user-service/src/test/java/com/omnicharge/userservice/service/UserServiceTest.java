package com.omnicharge.userservice.service;

import com.omnicharge.userservice.dto.*;
import com.omnicharge.userservice.entity.User;
import com.omnicharge.userservice.enums.Role;
import com.omnicharge.userservice.exception.ResourceNotFoundException;
import com.omnicharge.userservice.feign.PaymentServiceClient;
import com.omnicharge.userservice.feign.RechargeServiceClient;
import com.omnicharge.userservice.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RechargeServiceClient rechargeServiceClient;
    @Mock private PaymentServiceClient paymentServiceClient;

    @InjectMocks private UserService userService;

    private User mockUser;

    @BeforeEach
    void setup() {
        mockUser = User.builder()
                .id(1L)
                .name("Aditya")
                .email("aditya@omnicharge.com")
                .phone("9876543210")
                .role(Role.USER)
                .build();
    }

    @Test
    void getProfile_success() {
        when(userRepository.findByEmail("aditya@omnicharge.com"))
                .thenReturn(Optional.of(mockUser));

        UserProfileResponse response = userService.getProfile("aditya@omnicharge.com");

        assertNotNull(response);
        assertEquals("Aditya", response.getName());
        assertEquals("aditya@omnicharge.com", response.getEmail());
        assertEquals("USER", response.getRole());
    }

    @Test
    void getProfile_userNotFound_throwsException() {
        when(userRepository.findByEmail("unknown@omnicharge.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getProfile("unknown@omnicharge.com"));
    }

    @Test
    void updateProfile_success() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("Aditya Updated");
        request.setPhone("9999999999");

        when(userRepository.findByEmail("aditya@omnicharge.com"))
                .thenReturn(Optional.of(mockUser));

        UserProfileResponse response =
                userService.updateProfile("aditya@omnicharge.com", request);

        assertEquals("Aditya Updated", response.getName());
        assertEquals("9999999999", response.getPhone());
        verify(userRepository, times(2)).findByEmail("aditya@omnicharge.com");
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void getRechargeHistory_success() {
        when(userRepository.findByEmail("aditya@omnicharge.com"))
                .thenReturn(Optional.of(mockUser));
        when(rechargeServiceClient.getRechargesByUserId(1L))
                .thenReturn(List.of());

        List<?> result = userService.getRechargeHistory("aditya@omnicharge.com");

        assertNotNull(result);
        verify(rechargeServiceClient, times(1)).getRechargesByUserId(1L);
    }

    @Test
    void getTransactionStatus_success() {
        when(userRepository.findByEmail("aditya@omnicharge.com"))
                .thenReturn(Optional.of(mockUser));
        when(paymentServiceClient.getTransactionsByUserId(1L))
                .thenReturn(List.of());

        List<?> result = userService.getTransactionStatus("aditya@omnicharge.com");

        assertNotNull(result);
        verify(paymentServiceClient, times(1)).getTransactionsByUserId(1L);
    }

    @Test
    void getTransactionStatus_userNotFound_throwsException() {
        when(userRepository.findByEmail("unknown@omnicharge.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getTransactionStatus("unknown@omnicharge.com"));
    }

    @Test
    void deleteUser_success() {
        when(userRepository.findByEmail("aditya@omnicharge.com"))
                .thenReturn(Optional.of(mockUser));

        userService.deleteUser("aditya@omnicharge.com");

        verify(userRepository, times(1)).findByEmail("aditya@omnicharge.com");
        verify(userRepository, times(1)).delete(mockUser);
    }
}
