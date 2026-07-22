package com.mwalimubank.mbimsapi.features.administration.employee;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.administration.employee.dto.CreateEmployeeDTO;
import com.mwalimubank.mbimsapi.features.administration.employee.dto.EmployeeResponseDTO;
import com.mwalimubank.mbimsapi.features.administration.employee.EmployeeEntity;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository repository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;

    public PagedResponse<EmployeeResponseDTO> findAll(PaginationRequest pagination, String search) {
//        Specification<EmployeeEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));
        // Add search logic here if needed

        Page<EmployeeEntity> page = repository.findAll(pagination.toPageable());


        List<EmployeeResponseDTO> result = page.getContent().stream()
                .map(EmployeeResponseDTO::fromEntity)
                .toList();

        return new PagedResponse<>(
                result,
                new PaginationDto(page.getTotalElements(), page.getNumber() + 1, page.getSize(), page.getTotalPages()),
                false
        );
    }

    @Transactional
    public EmployeeResponseDTO create(CreateEmployeeDTO request) {
        EmployeeEntity entity = new EmployeeEntity();
        EmployeeEntity saved = repository.save(entity);
        return EmployeeResponseDTO.fromEntity(saved);
    }
//
//    public ApprovalAwareDTO<EmployeeResponseDTO> findOne(String id) {
//        EmployeeEntity entity = repository.findById(id)
//                .orElseThrow(() -> new IllegalStateException("Employee not found"));
//        return approvalStatusUtil.attachApprovalInfo(
//                EmployeeResponseDTO.fromEntity(entity),
//                entity.getId(),
//                EmployeeEntity.class.getSimpleName(),
//                currentUserService.getCurrentUserRoleId()
//        );
//    }

//    @Transactional
//    public EmployeeResponseDTO update(Long id, CreateEmployeeDTO request) {
//        EmployeeEntity entity = repository.findById(id)
//                .orElseThrow(() -> new IllegalStateException("Employee not found"));
//
//        entity.setName(request.getName());
//        entity.setDescription(request.getDescription());
//
//        EmployeeEntity updated = repository.save(entity);
//        return EmployeeResponseDTO.fromEntity(updated);
//    }

//    @Transactional
//    public void delete(Long id, boolean soft) {
//        EmployeeEntity entity = repository.findById(id)
//                .orElseThrow(() -> new IllegalStateException("Employee not found"));
//        if (soft) {
//            entity.setDeleted(true);
//            repository.save(entity);
//        } else {
//            repository.delete(entity);
//        }
//    }
}
