package com.mwalimubank.mbimsapi.features.administration.employee;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.administration.employee.dto.CreateEmployeeDTO;
import com.mwalimubank.mbimsapi.features.administration.employee.dto.EmployeeResponseDTO;
import com.mwalimubank.mbimsapi.features.administration.employee.EmployeeEntity;
import com.mwalimubank.mbimsapi.features.customer.dto.CustomerResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationDto;
import com.mwalimubank.mbimsapi.features.approval.util.ApprovalStatusUtil;
import com.mwalimubank.mbimsapi.core.services.CurrentUserService;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;

import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository repository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;

    public PagedResponse<EmployeeResponseDTO> findAll(PaginationRequest pagination, String search) {

        Page<Object[]> page;

        if (search == null || search.trim().isEmpty()) {
            page = repository.findAllActiveEICEmployees(pagination.toPageable());
        } else {
            page = repository.findAllActiveEICEmployeesWithSearch(search.trim(), pagination.toPageable());
        }

        List<EmployeeResponseDTO> dtos = page.getContent().stream()
                .map(row -> {
                    EmployeeResponseDTO dto = new EmployeeResponseDTO();
                    dto.setName(getAsString(row, 0));
                    dto.setGender(getAsString(row, 1));
                    dto.setId(getAsString(row, 2));
                    dto.setCreatedAt(getAsString(row, 3));
                    // Add more fields as needed
                    return dto;
                })
                .toList();

        return new PagedResponse<>(
                dtos,
                new PaginationDto(
                        page.getTotalElements(),
                        page.getNumber() + 1,
                        page.getSize(),
                        page.getTotalPages()
                ),
                false
        );
    }


    public EmployeeResponseDTO findOne(String id) {
        log.info("=== findOne START ===");
        log.info("Searching for ID: {}", id);

        Optional<Object[]> optionalRow = repository.findEmployeeById(id);

        if (optionalRow.isEmpty()) {
            log.error("No row found for ID: {}", id);
            throw new RuntimeException("Employee not found with id: " + id);
        }

        Object[] row = optionalRow.get();

        log.info("Row length: {}", row.length);
        if (row.length == 1 && row[0] instanceof Object[]) {
            row = (Object[]) row[0];
        }

        EmployeeResponseDTO dto = new EmployeeResponseDTO();

        dto.setName(getAsString(row, 0));
        dto.setGender(getAsString(row, 1));
        dto.setId(getAsString(row, 2));
        dto.setCreatedAt(getAsString(row, 3));

        log.info("Final DTO - Name: {}, Gender: {}, ID: {}", dto.getName(), dto.getGender(), dto.getId());
        log.info("=== findOne END ===");

        return dto;
    }

    // Safe string converter
    private String getAsString(Object[] row, int index) {
        if (row == null || index >= row.length || row[index] == null) {
            return null;
        }
        return row[index].toString();
    }
}
