package com.mwalimubank.mbimsapi.features.approval.services;

import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.core.services.CurrentUserService;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.approval.dto.UserApprovalRequestDTO;
import com.mwalimubank.mbimsapi.features.approval.dto.UserApprovalResponseDTO;
import com.mwalimubank.mbimsapi.features.approval.entity.SysApprovalEntity;
import com.mwalimubank.mbimsapi.features.approval.entity.UserApprovalEntity;
import com.mwalimubank.mbimsapi.features.approval.repository.SysApprovalRepository;
import com.mwalimubank.mbimsapi.features.approval.repository.UserApprovalRepository;
import com.mwalimubank.mbimsapi.features.approval.util.ApprovalStatusUtil;
import com.mwalimubank.mbimsapi.features.common.PageSpecs;
import com.mwalimubank.mbimsapi.features.common.services.PagedQueryService;
import com.mwalimubank.mbimsapi.features.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserApprovalService {
    private final SysApprovalRepository sysApprovalRepository;
    private final UserApprovalRepository repository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;
    private final PagedQueryService pagedQueryService;

    private static final Set<String> DEPARTMENT_SORT_FIELDS = Set.of(
            "id", "name"
    );

    public PagedResponse<UserApprovalResponseDTO> findAll(PaginationRequest pagination, String search) {
        Specification<UserApprovalEntity> spec = PageSpecs.and(
                PageSpecs.notDeleted(),
                PageSpecs.searchLike(search, "name")
        );

        return pagedQueryService.findAll(
                repository,
                spec,
                pagination,
                UserApprovalEntity.class,
                UserApprovalEntity::getId,
                UserApprovalResponseDTO::fromEntity,
                UserApprovalResponseDTO::setApprovalStatus,
                DEPARTMENT_SORT_FIELDS
        );
    }

    private static Specification<UserApprovalEntity> getEntitySpecification(String search) {
        Specification<UserApprovalEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));

        // Optional search filter (case-insensitive)
        if (search != null && !search.trim().isEmpty()) {
            String likePattern = "%" + search.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("title")), likePattern),
                            cb.like(cb.lower(root.get("description")), likePattern)
                    )
            );
        }
        return spec;
    }

    @Transactional
    public UserApprovalEntity create(UserApprovalRequestDTO request) {
        UserApprovalEntity userApproval = new UserApprovalEntity();
        userApproval.setName(request.getName());
        userApproval.setDescription(request.getDescription());

        SysApprovalEntity sysApproval = sysApprovalRepository.findById(request.getSysApprovalId())
                .orElseThrow(() -> new IllegalStateException("System approval does not exist"));
        userApproval.setSysApproval(sysApproval);

        return repository.save(userApproval);
    }

    public ApprovalAwareDTO<UserApprovalResponseDTO> findOne  (Long  userId) {
        UserApprovalEntity entity = repository.findById( userId)
                .orElseThrow(() -> new IllegalStateException(" User not found"));

        UserApprovalResponseDTO dto = UserApprovalResponseDTO.fromEntity(entity);

        return approvalStatusUtil.attachApprovalInfo(
                dto,
                entity
        );
    }

    @Transactional
    public UserApprovalEntity update(Long id, UserApprovalRequestDTO request) {
        UserApprovalEntity userApproval = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("UserApproval not found with id: " + id));

        // Check if another userApproval with the same name exists
        UserApprovalEntity existing = repository.findByName(request.getName());
        if (existing != null && !existing.getId().equals(id)) {
            throw new IllegalStateException("UserApproval with name '" + request.getName() + "' already exists");
        }

        userApproval.setName(request.getName());
        userApproval.setDescription(request.getDescription());

        SysApprovalEntity sysApproval = sysApprovalRepository.findById(request.getSysApprovalId())
                .orElseThrow(() -> new IllegalStateException("System approval does not exist"));
        userApproval.setSysApproval(sysApproval);

        return repository.save(userApproval);
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        UserApprovalEntity userApproval = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("UserApproval not found with id: " + id));

        if (soft) {
            userApproval.setDeleted(true); // soft delete flag from BaseEntity
            repository.save(userApproval);
        } else {
            repository.delete(userApproval);
        }
    }
}
