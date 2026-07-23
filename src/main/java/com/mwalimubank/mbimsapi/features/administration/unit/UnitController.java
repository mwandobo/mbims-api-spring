package com.mwalimubank.mbimsapi.features.administration.unit;

import com.mwalimubank.mbimsapi.core.dto.ApiResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.administration.unit.dto.CreateUnitDTO;
import com.mwalimubank.mbimsapi.features.administration.unit.dto.UnitResponseDTO;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/administration/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService service;

    @GetMapping
    public PagedResponse<UnitResponseDTO> findAll(
            PaginationRequest pagination,
            @RequestParam(required = false) String search) {
        return service.findAll(pagination, search);
    }

    @PostMapping
    public UnitResponseDTO create(@RequestBody CreateUnitDTO request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ApprovalAwareDTO<UnitResponseDTO> findOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PatchMapping("/{id}")
    public UnitResponseDTO update(@PathVariable Long id, @RequestBody CreateUnitDTO request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @RequestParam(name = "soft", defaultValue = "false") boolean soft) {
        service.delete(id, soft);
        return ApiResponse.success(null);
    }
}
