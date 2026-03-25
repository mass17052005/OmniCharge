package com.omnicharge.rechargeservice.service;

import com.omnicharge.rechargeservice.config.RabbitMQConfig;
import com.omnicharge.rechargeservice.dto.*;
import com.omnicharge.rechargeservice.entity.RechargeRequest;
import com.omnicharge.rechargeservice.enums.RechargeStatus;
import com.omnicharge.rechargeservice.exception.ResourceNotFoundException;
import com.omnicharge.rechargeservice.feign.OperatorServiceClient;
import com.omnicharge.rechargeservice.feign.PaymentServiceClient;
import com.omnicharge.rechargeservice.repository.RechargeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RechargeService {

    private final RechargeRepository rechargeRepository;
    private final OperatorServiceClient operatorServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    public RechargeResponse initiateRecharge(RechargeRequestDto dto) {

        // Step 1 — validate plan via Feign
        PlanResponse plan = operatorServiceClient.getPlanById(dto.getOperatorId(), dto.getPlanId());
        if (plan == null) {
            throw new RuntimeException("Operator service unavailable. Please try again.");
        }
        if (!"ACTIVE".equals(plan.getStatus())) {
            throw new RuntimeException("Selected plan is not active.");
        }

        // Step 2 — save recharge with CREATED status
        RechargeRequest recharge = RechargeRequest.builder()
                .userId(dto.getUserId())
                .mobileNumber(dto.getMobileNumber())
                .operatorId(dto.getOperatorId())
                .planId(dto.getPlanId())
                .amount(plan.getPrice())
                .status(RechargeStatus.CREATED)
                .build();
        recharge = rechargeRepository.save(recharge);

        // Step 3 — update to PENDING_PAYMENT
        recharge.setStatus(RechargeStatus.PENDING_PAYMENT);
        rechargeRepository.save(recharge);

        // Step 4 — call Payment Service via Feign
        PaymentRequest paymentRequest = new PaymentRequest(
                recharge.getId(), recharge.getUserId(), recharge.getAmount());
        Object paymentResult = paymentServiceClient.processPayment(paymentRequest);

        log.info("Payment result for recharge {}: {}", recharge.getId(), paymentResult);

        return mapToResponse(recharge);
    }

    // Consume SUCCESS event from RabbitMQ
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_SUCCESS_QUEUE)
    public void handlePaymentSuccess(PaymentEvent event) {
        log.info("Received payment SUCCESS event for recharge: {}", event.getRechargeId());
        updateRechargeStatus(event.getRechargeId(), RechargeStatus.SUCCESS);
    }

    // Consume FAILED event from RabbitMQ
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_FAILED_QUEUE)
    public void handlePaymentFailed(PaymentEvent event) {
        log.info("Received payment FAILED event for recharge: {}", event.getRechargeId());
        updateRechargeStatus(event.getRechargeId(), RechargeStatus.FAILED);
    }

    private void updateRechargeStatus(Long rechargeId, RechargeStatus status) {
        RechargeRequest recharge = rechargeRepository.findById(rechargeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recharge not found: " + rechargeId));
        recharge.setStatus(status);
        rechargeRepository.save(recharge);
    }

    public List<RechargeResponse> getRechargesByUserId(Long userId) {
        return rechargeRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RechargeResponse getRechargeById(Long id) {
        RechargeRequest recharge = rechargeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recharge not found: " + id));
        return mapToResponse(recharge);
    }

    private RechargeResponse mapToResponse(RechargeRequest recharge) {
        RechargeResponse response = new RechargeResponse();
        response.setRechargeId(recharge.getId());
        response.setUserId(recharge.getUserId());
        response.setMobileNumber(recharge.getMobileNumber());
        response.setOperatorId(recharge.getOperatorId());
        response.setPlanId(recharge.getPlanId());
        response.setAmount(recharge.getAmount());
        response.setStatus(recharge.getStatus());
        response.setCreatedAt(recharge.getCreatedAt());
        return response;
    }
}