package com.mwalimubank.mbimsapi.features.customer;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.customer.dto.CreateCustomerDTO;
import com.mwalimubank.mbimsapi.features.customer.dto.CustomerResponseDTO;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.customer.services.CustomerService;
import com.mwalimubank.mbimsapi.features.customer.services.CustomerSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;
    private final CustomerSyncService customerSyncService;

    @GetMapping
    public PagedResponse<CustomerResponseDTO> findAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String search) {
        return service.findAll(pagination, search);
    }

    @PostMapping
    public CustomerResponseDTO create(@RequestBody CreateCustomerDTO request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ApprovalAwareDTO<CustomerResponseDTO> findOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PatchMapping("/{id}")
    public CustomerResponseDTO update(@PathVariable Long id, @RequestBody CreateCustomerDTO request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @RequestParam(name = "soft", defaultValue = "false") boolean soft) {
        service.delete(id, soft);
        return ApiResponse.success(null);
    }

//    @PostMapping("/sync")
//    public ResponseEntity<?> syncCustomers() {
//        int processed = customerSyncService.syncCustomers();
//        return ResponseEntity.ok(Map.of(
//                "message", "Customer sync completed successfully",
//                "processed", processed
//        ));
//    }

    @PostMapping("/sync")
    public ResponseEntity<?> syncCustomers() {
        customerSyncService.syncCustomersAsync();   // fire and forget
        return ResponseEntity.accepted().body(Map.of(
                "message", "Customer sync started in background"
        ));
    }
}
