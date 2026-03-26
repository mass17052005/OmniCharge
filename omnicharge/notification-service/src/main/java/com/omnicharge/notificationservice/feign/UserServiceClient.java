package com.omnicharge.notificationservice.feign;

import com.omnicharge.notificationservice.dto.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Note: No auth configuration because this is an internal unauthenticated endpoint
@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users/internal/{id}")
    UserProfileResponse getUserByIdInternal(@PathVariable("id") Long id);
}
