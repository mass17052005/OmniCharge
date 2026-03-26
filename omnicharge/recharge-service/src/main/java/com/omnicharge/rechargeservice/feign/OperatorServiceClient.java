package com.omnicharge.rechargeservice.feign;

import com.omnicharge.rechargeservice.dto.PlanResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "operator-service")
public interface OperatorServiceClient {

    @GetMapping("/api/operators/plans/{planId}")
    @CircuitBreaker(name = "operatorService", fallbackMethod = "getPlanFallback")
    PlanResponse getPlanById(@PathVariable Long planId);

    default PlanResponse getPlanFallback(Long planId, Throwable t) {
        return null;
    }
}