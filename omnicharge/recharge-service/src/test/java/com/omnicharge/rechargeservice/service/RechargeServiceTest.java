package com.omnicharge.rechargeservice.service;

import com.omnicharge.rechargeservice.dto.*;
import com.omnicharge.rechargeservice.entity.RechargeRequest;
import com.omnicharge.rechargeservice.enums.RechargeStatus;
import com.omnicharge.rechargeservice.exception.ResourceNotFoundException;
import com.omnicharge.rechargeservice.feign.OperatorServiceClient;
import com.omnicharge.rechargeservice.feign.PaymentServiceClient;
import com.omnicharge.rechargeservice.feign.UserServiceClient;
import com.omnicharge.rechargeservice.repository.RechargeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RechargeServiceTest {

    @Mock private RechargeRepository rechargeRepository;
    @Mock private OperatorServiceClient operatorServiceClient;
    @Mock private PaymentServiceClient paymentServiceClient;
    @Mock private UserServiceClient userServiceClient;

    @InjectMocks
    private RechargeService rechargeService;

    private RechargeRequest mockRecharge;
    private PlanResponse mockPlan;
    private UserProfileResponse mockUserProfile;

    @BeforeEach
    void setup() {
        mockRecharge = RechargeRequest.builder()
                .id(1L)
                .userId(1L)
                .mobileNumber("9876543210")
                .operatorId(1L)
                .planId(1L)
                .amount(new BigDecimal("199"))
                .status(RechargeStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        mockPlan = new PlanResponse();
        mockPlan.setOperatorId(1L);
        mockPlan.setPrice(new BigDecimal("199"));
        mockPlan.setStatus("ACTIVE");
        
        mockUserProfile = new UserProfileResponse();
        mockUserProfile.setUserId(1L);
    }

    @Test
    void initiateRecharge_success() {
        RechargeRequestDto dto = new RechargeRequestDto();
        dto.setMobileNumber("9876543210");
        dto.setPlanId(1L);

        when(userServiceClient.getProfile()).thenReturn(mockUserProfile);
        when(operatorServiceClient.getPlanById(1L)).thenReturn(mockPlan);
        when(rechargeRepository.save(any(RechargeRequest.class))).thenReturn(mockRecharge);
        when(paymentServiceClient.processPayment(any(com.omnicharge.rechargeservice.dto.PaymentRequest.class))).thenReturn(new Object());

        RechargeResponse response = rechargeService.initiateRecharge(dto);

        assertNotNull(response);
        assertEquals(1L, response.getRechargeId());
        verify(rechargeRepository, times(2)).save(any(RechargeRequest.class));
        verify(paymentServiceClient, times(1)).processPayment(any());
    }

    @Test
    void initiateRecharge_operatorUnavailable_throwsException() {
        RechargeRequestDto dto = new RechargeRequestDto();
        dto.setPlanId(1L);

        when(userServiceClient.getProfile()).thenReturn(mockUserProfile);
        when(operatorServiceClient.getPlanById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> rechargeService.initiateRecharge(dto));
        verify(rechargeRepository, never()).save(any());
    }

    @Test
    void initiateRecharge_planInactive_throwsException() {
        RechargeRequestDto dto = new RechargeRequestDto();
        dto.setPlanId(1L);
        mockPlan.setStatus("INACTIVE");

        when(userServiceClient.getProfile()).thenReturn(mockUserProfile);
        when(operatorServiceClient.getPlanById(1L)).thenReturn(mockPlan);

        assertThrows(RuntimeException.class, () -> rechargeService.initiateRecharge(dto));
        verify(rechargeRepository, never()).save(any());
    }

    @Test
    void handlePaymentSuccess() {
        PaymentEvent event = new PaymentEvent();
        event.setRechargeId(1L);

        when(rechargeRepository.findById(1L)).thenReturn(Optional.of(mockRecharge));

        rechargeService.handlePaymentSuccess(event);

        assertEquals(RechargeStatus.SUCCESS, mockRecharge.getStatus());
        verify(rechargeRepository, times(1)).save(mockRecharge);
    }

    @Test
    void handlePaymentFailed() {
        PaymentEvent event = new PaymentEvent();
        event.setRechargeId(1L);

        when(rechargeRepository.findById(1L)).thenReturn(Optional.of(mockRecharge));

        rechargeService.handlePaymentFailed(event);

        assertEquals(RechargeStatus.FAILED, mockRecharge.getStatus());
        verify(rechargeRepository, times(1)).save(mockRecharge);
    }

    @Test
    void getRechargesByUserId_success() {
        when(rechargeRepository.findByUserId(1L)).thenReturn(List.of(mockRecharge));

        List<RechargeResponse> responses = rechargeService.getRechargesByUserId(1L);

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getRechargeId());
    }

    @Test
    void getRechargeById_success() {
        when(rechargeRepository.findById(1L)).thenReturn(Optional.of(mockRecharge));

        RechargeResponse response = rechargeService.getRechargeById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getRechargeId());
    }

    @Test
    void getRechargeById_notFound_throwsException() {
        when(rechargeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rechargeService.getRechargeById(99L));
    }
}
