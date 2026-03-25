package com.omnicharge.rechargeservice.feign;

import com.omnicharge.rechargeservice.dto.PlanResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "operator-service")
public interface OperatorServiceClient {

    @GetMapping("/api/operators/{operatorId}/plans/{planId}")
    @CircuitBreaker(name = "operatorService", fallbackMethod = "getPlanFallback")
    PlanResponse getPlanById(@PathVariable Long operatorId, @PathVariable Long planId);

    default PlanResponse getPlanFallback(Long operatorId, Long planId, Throwable t) {
        return null;
    }
}