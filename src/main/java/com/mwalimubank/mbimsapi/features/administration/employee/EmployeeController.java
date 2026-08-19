package com.mwalimubank.mbimsapi.features.administration.employee;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.administration.employee.dto.EmployeeResponseDTO;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.features.administration.employee.service.EmployeeService;
import com.mwalimubank.mbimsapi.features.administration.employee.service.EmployeeSyncService;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/administration/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;
    private final EmployeeSyncService employeeSyncService;

    @GetMapping
    public PagedResponse<EmployeeResponseDTO> findAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String q) {
        return service.findAll(pagination, q);
    }

//    @GetMapping("/{id}")
//    public EmployeeResponseDTO findOne(@PathVariable Long id) {
//        return service.findOne(id);
//    }


    @GetMapping("/{id}")
    public ApprovalAwareDTO<EmployeeResponseDTO> findOne(
            @PathVariable Long id
    ) {
        return service.findOne(id);
    }

    @PostMapping("/sync")
    public ResponseEntity<?> syncEmployees() {
        int affected = employeeSyncService.syncEmployees();
        return ResponseEntity.ok(Map.of(
                "message", "Employee sync completed successfully",
                "affectedRows", affected
        ));
    }
}
