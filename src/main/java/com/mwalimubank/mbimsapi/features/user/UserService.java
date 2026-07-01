package com.mwalimubank.mbimsapi.features.user;
import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import com.mwalimubank.mbimsapi.features.administration.department.DepartmentRepository;
import com.mwalimubank.mbimsapi.features.role.RoleRepository;
import com.mwalimubank.mbimsapi.features.role.RoleEntity;

import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.user.dto.CreateUserDTO;
import com.mwalimubank.mbimsapi.features.user.dto.UserResponseDTO;
import com.mwalimubank.mbimsapi.features.user.UserEntity;
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
public class UserService {
    private final UserRepository repository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;

    public PagedResponse<UserResponseDTO> findAll(
            PaginationRequest pagination,
            String search
    ) {
        Specification<UserEntity> spec = getEntitySpecification(search);
        boolean hasApprovalMode = approvalStatusUtil.hasApprovalMode(UserEntity.class.getSimpleName());

        Page<UserEntity> page =
                repository.findAll(spec, pagination.toPageable());

        List<UserEntity> entities = page.getContent();

        List<Long> ids = entities.stream()
                        .map(UserEntity::getId)
                        .toList();
        Map<Long, String> statusMap = hasApprovalMode
                        ? approvalStatusUtil.getBulkApprovalStatuses(UserEntity.class.getSimpleName(), ids)
                        : Collections.emptyMap();

      List<UserResponseDTO> result = entities.stream()
                      .map(entity -> {
                          UserResponseDTO dto = UserResponseDTO.fromEntity(entity);

                          if (hasApprovalMode) {
                              dto.setApprovalStatus(
                                      statusMap.get(entity.getId())
                              );
                          }

                          return dto;
                      })
                      .toList();

        return new PagedResponse<>(
                        result,
                        new PaginationDto(
                                page.getTotalElements(),
                                page.getNumber() + 1,
                                page.getSize(),
                                page.getTotalPages()
                        ),
                        hasApprovalMode // or dynamic logic
                );
    }

    private static Specification< UserEntity> getEntitySpecification(String search) {
        Specification< UserEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));

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
    public UserResponseDTO create(CreateUserDTO request) {
        repository.findByName(request.getName())
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "User with name '" + request.getName() + "' already exists"
                    );
                });

        UserEntity entity = new UserEntity();
        entity.setName(request.getName());
        DepartmentEntity department = validateDepartmentExists(request.getDepartment_id());
        entity.setDepartment(department);
        entity.setEmail(request.getEmail());
        RoleEntity role = validateRoleExists(request.getRole_id());
        entity.setRole(role);
        entity.setPassword("TEMP0000");
        UserEntity savedEntity = repository.save(entity);

        return  UserResponseDTO.fromEntity(savedEntity);
    }

    public  ApprovalAwareDTO<UserResponseDTO> findOne  (Long  userId) {
          UserEntity   user = repository.findById( userId)
                 .orElseThrow(() -> new IllegalStateException(" User not found"));

          UserResponseDTO dto = UserResponseDTO.fromEntity(user);

           return approvalStatusUtil.attachApprovalInfo(
                    dto,
                    user.getId(),
                    UserEntity.class.getSimpleName(),
                    currentUserService.getCurrentUserRoleId()
                );
     }

    @Transactional
    public UserResponseDTO update(Long id, CreateUserDTO request) {
        UserEntity entity = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "User not found with id: " + id
                        )
                );

        repository.findByName(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "User with name '" + request.getName() + "' already exists"
                    );
                });

        entity.setName(request.getName());
        DepartmentEntity department = validateDepartmentExists(request.getDepartment_id());
        entity.setDepartment(department);
        RoleEntity role = validateRoleExists(request.getRole_id());
        entity.setRole(role);

        UserEntity updatedEntity = repository.save(entity);

        return  UserResponseDTO.fromEntity(updatedEntity);
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        UserEntity entity = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "User not found with id: " + id
                        )
                );

        if (soft) {
            entity.setDeleted(true);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }

    private RoleEntity validateRoleExists(Long id) {
        if (id == null) {
            if ("false" == "false") {
                throw new IllegalArgumentException("Role ID is required");
            }
            return null;
        }
        return roleRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Role not found with id: " + id));
    }


    private DepartmentEntity validateDepartmentExists(Long id) {
        if (id == null) {
            if ("false" == "false") {
                throw new IllegalArgumentException("Department ID is required");
            }
            return null;
        }
        return departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Department not found with id: " + id));
    }

}
