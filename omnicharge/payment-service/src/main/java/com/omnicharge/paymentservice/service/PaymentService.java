package com.omnicharge.paymentservice.service;

import com.omnicharge.paymentservice.dto.PaymentEvent;
import com.omnicharge.paymentservice.dto.PaymentRequest;
import com.omnicharge.paymentservice.dto.PaymentResponse;
import com.omnicharge.paymentservice.entity.Transaction;
import com.omnicharge.paymentservice.enums.PaymentStatus;
import com.omnicharge.paymentservice.exception.ResourceNotFoundException;
import com.omnicharge.paymentservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.omnicharge.paymentservice.feign.UserServiceClient;
import com.omnicharge.paymentservice.dto.UserProfileResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final RabbitTemplate rabbitTemplate;
    private final UserServiceClient userServiceClient;

    @Value("${rabbitmq.exchange.name:omnicharge.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing.payment.success.key:payment.success}")
    private String paymentSuccessRoutingKey;

    @Value("${rabbitmq.routing.payment.failed.key:payment.failed}")
    private String paymentFailedRoutingKey;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        
        UserProfileResponse userProfile = userServiceClient.getProfile();
        Long userId = userProfile.getUserId();

        log.info("Processing payment for user {}", userId);

        // 1. Generate unique transaction ID
        String txnRef = UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();

        // 2. Perform dummy payment logic (Random success/failure)
        PaymentStatus status = simulatePaymentGateway() ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

        // 3. Save to database
        Transaction transaction = Transaction.builder()
                .txnRef(txnRef)
                .userId(userId)
                .rechargeId(request.getRechargeId())
                .amount(request.getAmount())
                .status(status)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Payment saved: ID={}, Status={}", txnRef, status);

        // 4. Publish Event
        publishPaymentEvent(savedTransaction);

        return mapToResponse(savedTransaction);
    }

    public PaymentResponse getPaymentById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
        return mapToResponse(transaction);
    }

    public List<PaymentResponse> getPaymentsByUserId(Long userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void publishPaymentEvent(Transaction transaction) {
        PaymentEvent event = PaymentEvent.builder()
                .rechargeId(transaction.getRechargeId())
                .userId(transaction.getUserId())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .txnRef(transaction.getTxnRef())
                .build();
                
        String routingKey = transaction.getStatus() == PaymentStatus.SUCCESS ? 
                paymentSuccessRoutingKey : paymentFailedRoutingKey;

        rabbitTemplate.convertAndSend(exchange, routingKey, event);
        log.info("Payment event published to RabbitMQ: {}, RoutingKey: {}", event.getTxnRef(), routingKey);
    }

    boolean simulatePaymentGateway() {
        // 90% success rate
        return Math.random() > 0.1;
    }

    private PaymentResponse mapToResponse(Transaction transaction) {
        return PaymentResponse.builder()
                .paymentId(transaction.getId())
                .txnRef(transaction.getTxnRef())
                .userId(transaction.getUserId())
                .rechargeId(transaction.getRechargeId())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
