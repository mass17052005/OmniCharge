package com.omnicharge.operatorservice.service;

import com.omnicharge.operatorservice.dto.*;
import com.omnicharge.operatorservice.entity.*;
import com.omnicharge.operatorservice.enums.*;
import com.omnicharge.operatorservice.exception.*;
import com.omnicharge.operatorservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperatorService {

    private final OperatorRepository operatorRepository;
    private final RechargePlanRepository rechargePlanRepository;

    // ─── Operator Methods ───────────────────────────────────────────

    public OperatorResponse addOperator(OperatorRequest operatorRequest) {
        if (operatorRepository.existsByCode(
                operatorRequest.getCode().toUpperCase())) {
            throw new DuplicateResourceException(
                    "Operator with code "
                    + operatorRequest.getCode() + " already exists");
        }

        Operator newOperator = Operator.builder()
                .name(operatorRequest.getName())
                .code(operatorRequest.getCode().toUpperCase())
                .status(OperatorStatus.ACTIVE)
                .build();

        Operator savedOperator = operatorRepository.save(newOperator);
        return mapToOperatorResponse(savedOperator);
    }

    public List<OperatorResponse> getAllActiveOperators() {
        return operatorRepository.findByStatus(OperatorStatus.ACTIVE)
                .stream()
                .map(this::mapToOperatorResponse)
                .collect(Collectors.toList());
    }

    public OperatorResponse getOperatorById(Long operatorId) {
        Operator foundOperator = operatorRepository.findById(operatorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Operator not found with id: " + operatorId));
        return mapToOperatorResponse(foundOperator);
    }

    public OperatorResponse updateOperator(
            Long operatorId, OperatorRequest operatorRequest) {
        Operator existingOperator = operatorRepository.findById(operatorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Operator not found with id: " + operatorId));

        existingOperator.setName(operatorRequest.getName());
        existingOperator.setCode(operatorRequest.getCode().toUpperCase());

        Operator updatedOperator = operatorRepository.save(existingOperator);
        return mapToOperatorResponse(updatedOperator);
    }

    public void deleteOperator(Long operatorId) {
        Operator existingOperator = operatorRepository.findById(operatorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Operator not found with id: " + operatorId));
        operatorRepository.delete(existingOperator);
    }

    // ─── Plan Methods ────────────────────────────────────────────────

    public PlanResponse addPlanToOperator(
            Long operatorId, PlanRequest planRequest) {
        Operator existingOperator = operatorRepository.findById(operatorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Operator not found with id: " + operatorId));

        RechargePlan newPlan = RechargePlan.builder()
                .operator(existingOperator)
                .planName(planRequest.getPlanName())
                .price(planRequest.getPrice())
                .validityDays(planRequest.getValidityDays())
                .dataPerDay(planRequest.getDataPerDay())
                .description(planRequest.getDescription())
                .status(PlanStatus.ACTIVE)
                .build();

        RechargePlan savedPlan = rechargePlanRepository.save(newPlan);
        return mapToPlanResponse(savedPlan);
    }

    public List<PlanResponse> getAllPlansByOperator(Long operatorId) {
        operatorRepository.findById(operatorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Operator not found with id: " + operatorId));

        return rechargePlanRepository
                .findByOperatorIdAndStatus(operatorId, PlanStatus.ACTIVE)
                .stream()
                .map(this::mapToPlanResponse)
                .collect(Collectors.toList());
    }

    public PlanResponse getPlanByIdAndOperator(
            Long operatorId, Long planId) {
        RechargePlan foundPlan = rechargePlanRepository
                .findByIdAndOperatorId(planId, operatorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Plan not found with id: " + planId
                        + " for operator: " + operatorId));
        return mapToPlanResponse(foundPlan);
    }

    public PlanResponse getPlanById(Long planId) {
        RechargePlan foundPlan = rechargePlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Plan not found with id: " + planId));
        return mapToPlanResponse(foundPlan);
    }

    // ─── Mappers ─────────────────────────────────────────────────────

    private OperatorResponse mapToOperatorResponse(Operator operator) {
        return OperatorResponse.builder()
                .operatorId(operator.getId())
                .name(operator.getName())
                .code(operator.getCode())
                .status(operator.getStatus())
                .createdAt(operator.getCreatedAt())
                .build();
    }

    private PlanResponse mapToPlanResponse(RechargePlan rechargePlan) {
        return PlanResponse.builder()
                .planId(rechargePlan.getId())
                .operatorId(rechargePlan.getOperator().getId())
                .operatorName(rechargePlan.getOperator().getName())
                .planName(rechargePlan.getPlanName())
                .price(rechargePlan.getPrice())
                .validityDays(rechargePlan.getValidityDays())
                .dataPerDay(rechargePlan.getDataPerDay())
                .description(rechargePlan.getDescription())
                .status(rechargePlan.getStatus())
                .build();
    }
}