package com.omnicharge.paymentservice.repository;

import com.omnicharge.paymentservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByTxnRef(String txnRef);
    
    List<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId);
}
