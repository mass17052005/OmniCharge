package com.omnicharge.rechargeservice.feign;

import com.omnicharge.rechargeservice.dto.PaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @PostMapping("/api/payments/process")
    @CircuitBreaker(name = "paymentService", fallbackMethod = "processPaymentFallback")
    Object processPayment(@RequestBody PaymentRequest request);

    default Object processPaymentFallback(PaymentRequest request, Throwable t) {
        return "FAILED";
    }
}