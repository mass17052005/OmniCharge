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

    @Operation(summary = "Delete logged-in user account")
    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteUser(userDetails.getUsername());
        return ResponseEntity.ok("User account deleted successfully");
    }

    @Operation(summary = "Get recharge history of logged-in user")
    @GetMapping("/recharge-history")
    public ResponseEntity<List<RechargeResponse>> getRechargeHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getRechargeHistory(userDetails.getUsername()));
    }

    @Operation(summary = "Get transaction status of logged-in user")
    @GetMapping("/transactions")
    public ResponseEntity<List<PaymentResponse>> getTransactionStatus(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getTransactionStatus(userDetails.getUsername()));
    }

    @Operation(summary = "Internal API to get user profile by ID")
    @GetMapping("/internal/{id}")
    public ResponseEntity<UserProfileResponse> getUserByIdInternal(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
