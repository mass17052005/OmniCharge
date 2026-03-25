package com.omnicharge.operatorservice.repository;

import com.omnicharge.operatorservice.entity.Operator;
import com.omnicharge.operatorservice.enums.OperatorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, Long> {

    List<Operator> findByStatus(OperatorStatus status);

    Optional<Operator> findByCode(String code);

    boolean existsByCode(String code);
}