package com.mwalimubank.mbimsapi.features.administration.employee;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.administration.department.dto.CreateDepartmentDTO;
import com.mwalimubank.mbimsapi.features.administration.department.dto.DepartmentResponseDTO;
import com.mwalimubank.mbimsapi.features.administration.employee.dto.CreateEmployeeDTO;
import com.mwalimubank.mbimsapi.features.administration.employee.dto.EmployeeResponseDTO;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.features.administration.employee.service.EmployeeService;
import com.mwalimubank.mbimsapi.features.administration.employee.service.EmployeeSyncService;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.auth.dtos.RegisterRequest;
import com.mwalimubank.mbimsapi.features.auth.dtos.RegisterResponse;
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
            @ModelAttribute PaginationRequest pagination,  // or just PaginationRequest pagination
            @RequestParam(required = false ) String search
    ) {
        return service.findAll(pagination, search);
    }

//    @GetMapping("/{id}")
//    public EmployeeResponseDTO findOne(@PathVariable Long id) {
//        return service.findOne(id);
//    }

    @PatchMapping("/{id}")
    public EmployeeResponseDTO update(
            @PathVariable Long id,
            @RequestBody CreateEmployeeDTO request
    ) {
        return service.update(id, request);
    }


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

    @PostMapping("/{id}/share-credential")
    public  String shareCredential(@PathVariable Long id) {
       return  service.receiveCredentials(id);
    }
}
