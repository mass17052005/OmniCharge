package com.omnicharge.userservice.controller;

import com.omnicharge.userservice.dto.*;
import com.omnicharge.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User profile and history endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get logged-in user profile")
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getProfile(userDetails.getUsername()));
    }

    @Operation(summary = "Update logged-in user profile")
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(
                userService.updateProfile(userDetails.getUsername(), request));
    }

    @Operation(summary = "Get recharge history of logged-in user")
    @GetMapping("/recharge-history")
    public ResponseEntity<List<?>> getRechargeHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getRechargeHistory(userDetails.getUsername()));
    }

    @Operation(summary = "Get transaction status of logged-in user")
    @GetMapping("/transactions")
    public ResponseEntity<List<?>> getTransactionStatus(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getTransactionStatus(userDetails.getUsername()));
    }
}
