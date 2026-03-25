package com.omnicharge.operatorservice.controller;

import com.omnicharge.operatorservice.dto.*;
import com.omnicharge.operatorservice.service.OperatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/operators")
@RequiredArgsConstructor
@Tag(name = "Operator", description = "Operator and recharge plan management APIs")
@SecurityRequirement(name = "bearerAuth")
public class OperatorController {

    private final OperatorService operatorService;

    // ─── Operator APIs ───────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Add new operator — ADMIN only")
    public ResponseEntity<OperatorResponse> addOperator(
            @Valid @RequestBody OperatorRequest operatorRequest) {
        return ResponseEntity.ok(operatorService.addOperator(operatorRequest));
    }

    @GetMapping
    @Operation(summary = "Get all active operators")
    public ResponseEntity<List<OperatorResponse>> getAllOperators() {
        return ResponseEntity.ok(operatorService.getAllActiveOperators());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get operator by ID")
    public ResponseEntity<OperatorResponse> getOperatorById(
            @PathVariable Long id) {
        return ResponseEntity.ok(operatorService.getOperatorById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update operator — ADMIN only")
    public ResponseEntity<OperatorResponse> updateOperator(
            @PathVariable Long id,
            @Valid @RequestBody OperatorRequest operatorRequest) {
        return ResponseEntity.ok(
                operatorService.updateOperator(id, operatorRequest));
    }

    // ─── Plan APIs ───────────────────────────────────────────────────

    @PostMapping("/{id}/plans")
    @Operation(summary = "Add plan to operator — ADMIN only")
    public ResponseEntity<PlanResponse> addPlan(
            @PathVariable Long id,
            @Valid @RequestBody PlanRequest planRequest) {
        return ResponseEntity.ok(
                operatorService.addPlanToOperator(id, planRequest));
    }

    @GetMapping("/{id}/plans")
    @Operation(summary = "Get all plans for operator")
    public ResponseEntity<List<PlanResponse>> getAllPlans(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                operatorService.getAllPlansByOperator(id));
    }

    @GetMapping("/{id}/plans/{planId}")
    @Operation(summary = "Get plan by ID — Internal Feign")
    public ResponseEntity<PlanResponse> getPlanById(
            @PathVariable Long id,
            @PathVariable Long planId) {
        return ResponseEntity.ok(
                operatorService.getPlanByIdAndOperator(id, planId));
    }
}