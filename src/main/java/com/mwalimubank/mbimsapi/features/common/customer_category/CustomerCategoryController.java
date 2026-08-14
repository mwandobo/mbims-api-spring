package com.mwalimubank.mbimsapi.features.common.customer_category;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.common.customer_category.dto.CreateCustomerCategoryDTO;
import com.mwalimubank.mbimsapi.features.common.customer_category.dto.CustomerCategoryResponseDTO;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.common.customer_category.service.CustomerCategoryService;
import com.mwalimubank.mbimsapi.features.common.customer_category.service.CustomerCategorySyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/customer-categories")
@RequiredArgsConstructor
public class CustomerCategoryController {

    private final CustomerCategoryService service;
    private final CustomerCategorySyncService customerCategorySyncService;

    @GetMapping
    public PagedResponse<CustomerCategoryResponseDTO> findAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String search) {
        return service.findAll(pagination, search);
    }

    @PostMapping
    public CustomerCategoryResponseDTO create(@RequestBody CreateCustomerCategoryDTO request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ApprovalAwareDTO<CustomerCategoryResponseDTO> findOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PatchMapping("/{id}")
    public CustomerCategoryResponseDTO update(@PathVariable Long id, @RequestBody CreateCustomerCategoryDTO request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @RequestParam(name = "soft", defaultValue = "false") boolean soft) {
        service.delete(id, soft);
        return ApiResponse.success(null);
    }

    @PostMapping("/sync")
    public ResponseEntity<?> syncCustomerCategory() {
        customerCategorySyncService.syncCustomerCategoriesAsync();   // fire and forget
        return ResponseEntity.accepted().body(Map.of(
                "message", "Customer Category sync started in background"
        ));
    }
}
