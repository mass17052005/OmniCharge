package com.omnicharge.paymentservice.service;

import com.omnicharge.paymentservice.dto.PaymentEvent;
import com.omnicharge.paymentservice.dto.PaymentRequest;
import com.omnicharge.paymentservice.dto.PaymentResponse;
import com.omnicharge.paymentservice.entity.Transaction;
import com.omnicharge.paymentservice.enums.PaymentStatus;
import com.omnicharge.paymentservice.exception.ResourceNotFoundException;
import com.omnicharge.paymentservice.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequest request;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "exchange", "omnicharge.exchange");
        ReflectionTestUtils.setField(paymentService, "paymentSuccessRoutingKey", "payment.success");
        ReflectionTestUtils.setField(paymentService, "paymentFailedRoutingKey", "payment.failed");

        request = PaymentRequest.builder()
                .userId(1L)
                .rechargeId(101L)
                .amount(new BigDecimal("299.00"))
                .build();

        transaction = Transaction.builder()
                .id(1L)
                .txnRef("TRX1234567890ABC")
                .userId(1L)
                .rechargeId(101L)
                .amount(new BigDecimal("299.00"))
                .status(PaymentStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void processPayment_Success() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        
        PaymentService spyService = spy(paymentService);
        doReturn(true).when(spyService).simulatePaymentGateway();
        
        PaymentResponse response = spyService.processPayment(request);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(rabbitTemplate, times(1)).convertAndSend(eq("omnicharge.exchange"), eq("payment.success"), any(PaymentEvent.class));
    }

    @Test
    void processPayment_Failed() {
        Transaction failedTx = Transaction.builder()
                .id(2L)
                .txnRef("TRX0987654321XYZ")
                .userId(1L)
                .rechargeId(101L)
                .amount(new BigDecimal("299.00"))
                .status(PaymentStatus.FAILED)
                .createdAt(LocalDateTime.now())
                .build();
                
        when(transactionRepository.save(any(Transaction.class))).thenReturn(failedTx);
        
        PaymentService spyService = spy(paymentService);
        doReturn(false).when(spyService).simulatePaymentGateway();
        
        PaymentResponse response = spyService.processPayment(request);

        assertNotNull(response);
        assertEquals(PaymentStatus.FAILED, response.getStatus());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(rabbitTemplate, times(1)).convertAndSend(eq("omnicharge.exchange"), eq("payment.failed"), any(PaymentEvent.class));
    }

    @Test
    void getPaymentById_Success() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        PaymentResponse response = paymentService.getPaymentById(1L);

        assertNotNull(response);
        assertEquals("TRX1234567890ABC", response.getTxnRef());
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
    }

    @Test
    void getPaymentById_NotFound() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            paymentService.getPaymentById(99L));
    }

    @Test
    void getPaymentsByUserId_ReturnsList() {
        when(transactionRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(transaction));

        var responses = paymentService.getPaymentsByUserId(1L);

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals("TRX1234567890ABC", responses.get(0).getTxnRef());
    }
}
