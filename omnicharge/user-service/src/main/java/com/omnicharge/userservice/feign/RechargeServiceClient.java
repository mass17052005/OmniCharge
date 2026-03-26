package com.omnicharge.userservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import com.omnicharge.userservice.config.FeignConfig;
import com.omnicharge.userservice.dto.RechargeResponse;

@FeignClient(name = "recharge-service", configuration = FeignConfig.class)
public interface RechargeServiceClient {

    @GetMapping("/api/recharges/user/{userId}")
    List<RechargeResponse> getRechargesByUserId(@PathVariable Long userId);
}
