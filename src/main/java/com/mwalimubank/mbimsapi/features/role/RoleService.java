package com.mwalimubank.mbimsapi.features.role;

import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationDto;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.administration.position.PositionEntity;
import com.mwalimubank.mbimsapi.features.administration.position.dto.PositionResponseDTO;
import com.mwalimubank.mbimsapi.features.approval.util.ApprovalStatusUtil;
import com.mwalimubank.mbimsapi.features.common.PageSpecs;
import com.mwalimubank.mbimsapi.features.common.services.PagedQueryService;
import com.mwalimubank.mbimsapi.features.permission.PermissionEntity;
import com.mwalimubank.mbimsapi.features.permission.PermissionRepository;
import com.mwalimubank.mbimsapi.features.role.dto.AssignPermissionsRequestDTO;
import com.mwalimubank.mbimsapi.features.role.dto.AssignRoleRequest;
import com.mwalimubank.mbimsapi.features.role.dto.CreateRoleRequest;
import com.mwalimubank.mbimsapi.features.role.dto.RoleResponseDTO;
import com.mwalimubank.mbimsapi.features.role.dto.RoleWithPermissionsDTO;
import com.mwalimubank.mbimsapi.features.user.UserEntity;
import com.mwalimubank.mbimsapi.features.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService {

        private final UserRepository userRepository;
        private final RoleRepository repository;
        private final RoleRepository roleRepository;
        private final PermissionRepository permissionRepository;
        private final ApprovalStatusUtil approvalStatusUtil;


    private final PagedQueryService pagedQueryService;

    private static final Set<String> SORT_FIELDS = Set.of(
            "id", "name"
    );

    public PagedResponse<RoleResponseDTO> findAll(PaginationRequest pagination, String search) {
        Specification<RoleEntity> spec = PageSpecs.and(
                PageSpecs.notDeleted(),
                PageSpecs.searchLike(search, "name")
        );

        return pagedQueryService.findAll(
                repository,
                spec,
                pagination,
                RoleEntity.class,
                RoleEntity::getId,
                RoleResponseDTO::fromEntity,
                RoleResponseDTO::setApprovalStatus,
                SORT_FIELDS
        );
    }


    @Transactional
        public UserEntity assignRolesToUser(AssignRoleRequest request) {
                UserEntity user = userRepository.findById(request.getUserId())
                                .orElseThrow(() -> new IllegalStateException("User not found"));

                RoleEntity role = repository.findById(request.getRoleId())
                                .orElseThrow(() -> new IllegalStateException("User not found"));
                user.setRole(role);

                return userRepository.save(user);
        }


        @Transactional
        public RoleResponseDTO assignPermissions(Long roleId, AssignPermissionsRequestDTO request) {

                // Fetch the role
                RoleEntity role = roleRepository.findById(roleId)
                                .orElseThrow(() -> new IllegalStateException("Role not found with id: " + roleId));

                // Fetch all permissions by their IDs
                List<PermissionEntity> permissions = permissionRepository.findAllById(request.getPermissions());

                // Validate that all requested permissions exist
                if (permissions.size() != request.getPermissions().size()) {
                        throw new IllegalArgumentException("One or more permissions not found");
                }

                // Clear existing permissions and assign new ones
                role.getPermissions().clear();
                role.getPermissions().addAll(permissions);

                // Save and return
                RoleEntity savedRole = roleRepository.save(role);

                return RoleResponseDTO.fromEntity(savedRole);
        }

        public RoleResponseDTO findOne(Long id) {
                RoleEntity role = repository.findWithPermissionsById(id)
                                .orElseThrow(() -> new IllegalStateException("User not found"));

                return RoleResponseDTO.fromEntity(role);
        }

        @Transactional
        public RoleEntity create(CreateRoleRequest request) {
                // Check if role already exists
                repository.findByName(request.getName())
                                .ifPresent(r -> {
                                        throw new IllegalStateException(
                                                        "Role with name '" + request.getName() + "' already exists");
                                });

                // Map DTO -> Entity
                RoleEntity role = new RoleEntity();
                role.setName(request.getName());

                return repository.save(role);
        }

        @Transactional
        public RoleEntity update(Long id, CreateRoleRequest request) {
                RoleEntity role = repository.findById(id)
                                .orElseThrow(() -> new IllegalStateException("Role not found with id: " + id));

                // Check if another role with the same name exists
                repository.findByName(request.getName())
                                .filter(existing -> !existing.getId().equals(id))
                                .ifPresent(existing -> {
                                        throw new IllegalStateException(
                                                        "Role with name '" + request.getName() + "' already exists");
                                });

                // Update fields
                role.setName(request.getName());

                return repository.save(role);
        }

        public void delete(Long id, boolean soft) {
                RoleEntity role = repository.findById(id)
                                .orElseThrow(() -> new IllegalStateException("Role not found with id: " + id));

                if (soft) {
                        // Soft delete (flag from BaseEntity)
                        role.setDeleted(true);
                        repository.save(role);
                } else {
                        // Hard delete
                        repository.delete(role);
                }
        }

        public RoleWithPermissionsDTO getRoleWithPermissions(Long roleId) {

                // Fetch all permissions
                List<PermissionEntity> allPermissions = permissionRepository.findAll();

                // Fetch role with its permissions (eagerly loaded)
                RoleEntity role = roleRepository.findRoleWithPermissions(roleId)
                                .orElseThrow(() -> new RuntimeException("Role not found"));

                return RoleWithPermissionsDTO.builder()
                                .roleId(role.getId())
                                .roleName(role.getName())
                                .rolePermissions(role.getPermissions())
                                .allPermissions(allPermissions)
                                .build();
        }

}