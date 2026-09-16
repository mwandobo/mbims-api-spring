package com.mwalimubank.mbimsapi.features.reconciliation;

import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.reconciliation.ReconciliationService;
import com.mwalimubank.mbimsapi.features.reconciliation.dto.ReconciliationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reconciliations")
@RequiredArgsConstructor
public class ReconciliationController {

    private final ReconciliationService service;


    @GetMapping
    public PagedResponse<ReconciliationResponseDTO> findAll(
            PaginationRequest pagination,
            @RequestParam(required = false, name = "q") String search
    ) {
        return service.findAll(pagination, search);
    }

    @GetMapping("/{id}")
    public ApprovalAwareDTO<ReconciliationResponseDTO> findOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PostMapping("/compare")
    public ResponseEntity<ReconciliationResponseDTO> compare(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(required = false) String name
    ) {
        Long userId = null; // or inject CurrentUserService and use getCurrentUserId()
        // return service.compareAndSave(files, name);
        return ResponseEntity.ok(service.compareAndSave(files, name, userId));
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<?> getItems(@PathVariable Long id) {
        return ResponseEntity.ok(service.getItems(id));
    }

//    @PostMapping("/compare")
//    public Map<String, Object> compare(@RequestParam("files") MultipartFile[] files) {
//        return service.compareExcel(files);
//    }
}