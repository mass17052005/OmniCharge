package com.omnicharge.userservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "recharge-service")
public interface RechargeServiceClient {

    @GetMapping("/api/recharges/user/{userId}")
    List<?> getRechargesByUserId(@PathVariable Long userId);
}
