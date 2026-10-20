package com.mwalimubank.mbimsapi.features.administration.unit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1) // run early if you have other seeders
public class UnitSeeder implements CommandLineRunner {

    private final UnitRepository unitRepository;

    private static final List<UnitSeed> UNITS = List.of(
            new UnitSeed("100", "HEAD OFFICE - FINANCE"),
            new UnitSeed("101", "BUSINESS BANKING AND MARKETING"),
            new UnitSeed("102", "CEO OFFICE"),
            new UnitSeed("103", "CREDIT ADMINISTRATION"),
            new UnitSeed("104", "HR & ADMINISTRATION"),
            new UnitSeed("105", "RISK AND COMPLIANCE"),
            new UnitSeed("106", "OPERATION AND ICT"),
            new UnitSeed("200", "SAMORA BRANCH"),
            new UnitSeed("201", "MLIMAN BRANCH")
    );

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting Unit seeder...");

        int inserted = 0;
        for (UnitSeed seed : UNITS) {
            if (unitRepository.existsByCode(seed.code())) {
                log.debug("Unit with code {} already exists – skipping", seed.code());
                continue;
            }

            UnitEntity unit = new UnitEntity();
            unit.setCode(seed.code());
            unit.setName(seed.name());
            // description & manager left null (as in your create method when not provided)

            unitRepository.save(unit);
            inserted++;
            log.info("Seeded unit: {} – {}", seed.code(), seed.name());
        }

        log.info("Unit seeder finished. Inserted {} new unit(s).", inserted);
    }

    private record UnitSeed(String code, String name) {}
}