package com.omnicharge.rechargeservice.repository;

import com.omnicharge.rechargeservice.entity.RechargeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RechargeRepository extends JpaRepository<RechargeRequest, Long> {
    List<RechargeRequest> findByUserId(Long userId);
}