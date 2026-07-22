package com.mwalimubank.mbimsapi.features.administration.employee;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.administration.employee.dto.CreateEmployeeDTO;
import com.mwalimubank.mbimsapi.features.administration.employee.dto.EmployeeResponseDTO;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/administration/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    @GetMapping
    public PagedResponse<EmployeeResponseDTO> findAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String search) {
        return service.findAll(pagination, search);
    }

//    @PostMapping
//    public EmployeeResponseDTO create(@RequestBody CreateEmployeeDTO request) {
//        return service.create(request);
//    }

//    @GetMapping("/{id}")
//    public ApprovalAwareDTO<EmployeeResponseDTO> findOne(@PathVariable Long id) {
//        return service.findOne(id);
//    }
//
//    @PatchMapping("/{id}")
//    public EmployeeResponseDTO update(@PathVariable Long id, @RequestBody CreateEmployeeDTO request) {
//        return service.update(id, request);
//    }
//
//    @DeleteMapping("/{id}")
//    public ApiResponse<Void> delete(@PathVariable Long id,
//                                    @RequestParam(name = "soft", defaultValue = "false") boolean soft) {
//        service.delete(id, soft);
//        return ApiResponse.success(null);
//    }
}
