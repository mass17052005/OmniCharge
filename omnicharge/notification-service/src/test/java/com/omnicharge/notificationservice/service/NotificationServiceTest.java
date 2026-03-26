package com.omnicharge.notificationservice.service;

import com.omnicharge.notificationservice.dto.PaymentEvent;
import com.omnicharge.notificationservice.dto.UserProfileResponse;
import com.omnicharge.notificationservice.entity.Notification;
import com.omnicharge.notificationservice.enums.NotificationType;
import com.omnicharge.notificationservice.exception.ResourceNotFoundException;
import com.omnicharge.notificationservice.feign.UserServiceClient;
import com.omnicharge.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private UserServiceClient userServiceClient;
    @Mock private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    private Notification mockNotification;
    private PaymentEvent mockEvent;
    private UserProfileResponse mockUserProfile;

    @BeforeEach
    void setup() {
        mockNotification = Notification.builder()
                .id(1L)
                .userId(1L)
                .rechargeId(100L)
                .message("Test Message")
                .type(NotificationType.SUCCESS)
                .build();

        mockEvent = new PaymentEvent();
        mockEvent.setUserId(1L);
        mockEvent.setRechargeId(100L);
        mockEvent.setTxnRef("TXN12345");

        mockUserProfile = new UserProfileResponse();
        mockUserProfile.setUserId(1L);
        mockUserProfile.setEmail("test@omnicharge.com");
    }

    @Test
    void handlePaymentSuccess_savesNotificationAndSendsEmail() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);
        when(userServiceClient.getUserByIdInternal(1L)).thenReturn(mockUserProfile);

        notificationService.handlePaymentSuccess(mockEvent);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(userServiceClient, times(1)).getUserByIdInternal(1L);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void handlePaymentFailed_savesNotificationAndSendsEmail() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);
        when(userServiceClient.getUserByIdInternal(1L)).thenReturn(mockUserProfile);

        notificationService.handlePaymentFailed(mockEvent);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(userServiceClient, times(1)).getUserByIdInternal(1L);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void handlePaymentSuccess_emailFails_doesNotThrowException() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);
        when(userServiceClient.getUserByIdInternal(1L)).thenThrow(new RuntimeException("Service Down"));

        // Should not throw because of the try-catch inside sendEmailNotification
        assertDoesNotThrow(() -> notificationService.handlePaymentSuccess(mockEvent));

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void handlePaymentSuccess_userEmailIsNull_doesNotSendEmail() {
        mockUserProfile.setEmail(null);
        when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);
        when(userServiceClient.getUserByIdInternal(1L)).thenReturn(mockUserProfile);

        notificationService.handlePaymentSuccess(mockEvent);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void getNotificationsByUserId_returnsList() {
        when(notificationRepository.findByUserId(1L)).thenReturn(List.of(mockNotification));
        List<Notification> result = notificationService.getNotificationsByUserId(1L);
        assertEquals(1, result.size());
    }

    @Test
    void getNotificationsByRechargeId_returnsList() {
        when(notificationRepository.findByRechargeId(100L)).thenReturn(List.of(mockNotification));
        List<Notification> result = notificationService.getNotificationsByRechargeId(100L);
        assertEquals(1, result.size());
    }

    @Test
    void getNotificationById_success() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(mockNotification));
        Notification result = notificationService.getNotificationById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getNotificationById_notFound_throwsException() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> notificationService.getNotificationById(99L));
    }
}
