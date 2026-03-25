package com.omnicharge.rechargeservice.controller;

import com.omnicharge.rechargeservice.dto.RechargeRequestDto;
import com.omnicharge.rechargeservice.dto.RechargeResponse;
import com.omnicharge.rechargeservice.service.RechargeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recharges")
@RequiredArgsConstructor
@Tag(name = "Recharge", description = "Recharge management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class RechargeController {

    private final RechargeService rechargeService;

    @Operation(summary = "Initiate a new recharge")
    @PostMapping
    public ResponseEntity<RechargeResponse> initiateRecharge(
            @RequestBody RechargeRequestDto dto) {
        return ResponseEntity.ok(rechargeService.initiateRecharge(dto));
    }

    @Operation(summary = "Get recharge by ID")
    @GetMapping("/{id}")
    public ResponseEntity<RechargeResponse> getRechargeById(@PathVariable Long id) {
        return ResponseEntity.ok(rechargeService.getRechargeById(id));
    }

    @Operation(summary = "Get all recharges by user ID")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RechargeResponse>> getRechargesByUserId(
            @PathVariable Long userId) {
        return ResponseEntity.ok(rechargeService.getRechargesByUserId(userId));
    }
}