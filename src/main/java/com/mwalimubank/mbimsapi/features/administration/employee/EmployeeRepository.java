package com.mwalimubank.mbimsapi.features.administration.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, String> { }
