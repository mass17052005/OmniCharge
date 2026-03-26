package com.omnicharge.userservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import com.omnicharge.userservice.config.FeignConfig;
import com.omnicharge.userservice.dto.PaymentResponse;

@FeignClient(name = "payment-service", configuration = FeignConfig.class)
public interface PaymentServiceClient {

    @GetMapping("/api/payments/user/{userId}")
    List<PaymentResponse> getTransactionsByUserId(@PathVariable Long userId);
}
