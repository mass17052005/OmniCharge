package com.omnicharge.paymentservice.feign;

import com.omnicharge.paymentservice.config.FeignConfig;
import com.omnicharge.paymentservice.dto.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserServiceClient {

    @GetMapping("/api/users/profile")
    UserProfileResponse getProfile();
}
