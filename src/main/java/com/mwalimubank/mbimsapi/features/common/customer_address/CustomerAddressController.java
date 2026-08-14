package com.mwalimubank.mbimsapi.features.common.customer_address;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.common.customer_address.dto.CreateCustomerAddressDTO;
import com.mwalimubank.mbimsapi.features.common.customer_address.dto.CustomerAddressResponseDTO;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.common.customer_address.service.CustomerAddressService;
import com.mwalimubank.mbimsapi.features.common.customer_address.service.CustomerAddressSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/customer-addresses")
@RequiredArgsConstructor
public class CustomerAddressController {

    private final CustomerAddressService service;
    private final CustomerAddressSyncService customerAddressSyncService;

    @GetMapping
    public PagedResponse<CustomerAddressResponseDTO> findAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String search) {
        return service.findAll(pagination, search);
    }

    @PostMapping
    public CustomerAddressResponseDTO create(@RequestBody CreateCustomerAddressDTO request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ApprovalAwareDTO<CustomerAddressResponseDTO> findOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PatchMapping("/{id}")
    public CustomerAddressResponseDTO update(@PathVariable Long id, @RequestBody CreateCustomerAddressDTO request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @RequestParam(name = "soft", defaultValue = "false") boolean soft) {
        service.delete(id, soft);
        return ApiResponse.success(null);
    }

    @PostMapping("/sync")
    public ResponseEntity<?> syncCustomerAddresses() {
        customerAddressSyncService.syncCustomerAddressesAsync();   // fire and forget
        return ResponseEntity.accepted().body(Map.of(
                "message", "Customer sync started in background"
        ));
    }
}
