package com.omnicharge.operatorservice.config;

import com.omnicharge.operatorservice.entity.Operator;
import com.omnicharge.operatorservice.entity.RechargePlan;
import com.omnicharge.operatorservice.enums.OperatorStatus;
import com.omnicharge.operatorservice.enums.PlanStatus;
import com.omnicharge.operatorservice.repository.OperatorRepository;
import com.omnicharge.operatorservice.repository.RechargePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final OperatorRepository operatorRepository;
    private final RechargePlanRepository rechargePlanRepository;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            if (operatorRepository.count() > 0) {
                return; // already seeded — skip
            }

            // ── Jio ──────────────────────────────────────────
            Operator jio = operatorRepository.save(
                    Operator.builder()
                            .name("Jio")
                            .code("JIO")
                            .status(OperatorStatus.ACTIVE)
                            .build());

            rechargePlanRepository.save(RechargePlan.builder()
                    .operator(jio)
                    .planName("Jio 239")
                    .price(new BigDecimal("239"))
                    .validityDays(28)
                    .dataPerDay("1.5GB")
                    .description("1.5GB/day for 28 days with unlimited calls")
                    .status(PlanStatus.ACTIVE)
                    .build());

            rechargePlanRepository.save(RechargePlan.builder()
                    .operator(jio)
                    .planName("Jio 479")
                    .price(new BigDecimal("479"))
                    .validityDays(56)
                    .dataPerDay("2GB")
                    .description("2GB/day for 56 days with unlimited calls")
                    .status(PlanStatus.ACTIVE)
                    .build());

            // ── Airtel ───────────────────────────────────────
            Operator airtel = operatorRepository.save(
                    Operator.builder()
                            .name("Airtel")
                            .code("AIRTEL")
                            .status(OperatorStatus.ACTIVE)
                            .build());

            rechargePlanRepository.save(RechargePlan.builder()
                    .operator(airtel)
                    .planName("Airtel 265")
                    .price(new BigDecimal("265"))
                    .validityDays(28)
                    .dataPerDay("1.5GB")
                    .description("1.5GB/day for 28 days with unlimited calls")
                    .status(PlanStatus.ACTIVE)
                    .build());

            rechargePlanRepository.save(RechargePlan.builder()
                    .operator(airtel)
                    .planName("Airtel 499")
                    .price(new BigDecimal("499"))
                    .validityDays(56)
                    .dataPerDay("2GB")
                    .description("2GB/day for 56 days with unlimited calls")
                    .status(PlanStatus.ACTIVE)
                    .build());

            // ── Vi ───────────────────────────────────────────
            Operator vi = operatorRepository.save(
                    Operator.builder()
                            .name("Vi")
                            .code("VI")
                            .status(OperatorStatus.ACTIVE)
                            .build());

            rechargePlanRepository.save(RechargePlan.builder()
                    .operator(vi)
                    .planName("Vi 219")
                    .price(new BigDecimal("219"))
                    .validityDays(28)
                    .dataPerDay("1GB")
                    .description("1GB/day for 28 days with unlimited calls")
                    .status(PlanStatus.ACTIVE)
                    .build());

            rechargePlanRepository.save(RechargePlan.builder()
                    .operator(vi)
                    .planName("Vi 459")
                    .price(new BigDecimal("459"))
                    .validityDays(56)
                    .dataPerDay("1.5GB")
                    .description("1.5GB/day for 56 days with unlimited calls")
                    .status(PlanStatus.ACTIVE)
                    .build());

            System.out.println("Seed data loaded — Jio, Airtel, Vi operators added");
        };
    }
}