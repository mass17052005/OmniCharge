package com.omnicharge.operatorservice.repository;

import com.omnicharge.operatorservice.entity.RechargePlan;
import com.omnicharge.operatorservice.enums.PlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RechargePlanRepository extends JpaRepository<RechargePlan, Long> {

    List<RechargePlan> findByOperatorIdAndStatus(
            Long operatorId, PlanStatus status);

    Optional<RechargePlan> findByIdAndOperatorId(
            Long planId, Long operatorId);
}