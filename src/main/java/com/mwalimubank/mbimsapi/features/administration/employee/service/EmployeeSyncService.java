package com.mwalimubank.mbimsapi.features.administration.employee.service;

import com.mwalimubank.mbimsapi.features.administration.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeSyncService {


    private final EmployeeRepository repository;

    @Transactional
    public int syncEmployees() {
        log.info("Starting employee sync from PROFITS.BANKEMPLOYEE → MBIMS.EMPLOYEE...");
        int affected = repository.syncEmployees();
        log.info("Employee sync completed. Rows affected: {}", affected);
        return affected;
    }
}