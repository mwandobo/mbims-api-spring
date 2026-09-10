package com.mwalimubank.mbimsapi.features.administration.employee.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mwalimubank.mbimsapi.core.constants.FrontEndRouteConstants;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.core.utils.PasswordGenerator;
import com.mwalimubank.mbimsapi.features.administration.department.DepartmentEntity;
import com.mwalimubank.mbimsapi.features.administration.department.DepartmentRepository;
import com.mwalimubank.mbimsapi.features.administration.employee.dto.CreateEmployeeDTO;
import com.mwalimubank.mbimsapi.features.administration.employee.dto.EmployeeResponseDTO;
import com.mwalimubank.mbimsapi.features.administration.employee.entity.EmployeeEntity;
import com.mwalimubank.mbimsapi.features.administration.employee.repository.EmployeeRepository;
import com.mwalimubank.mbimsapi.features.administration.position.PositionEntity;
import com.mwalimubank.mbimsapi.features.administration.position.PositionRepository;
import com.mwalimubank.mbimsapi.features.administration.unit.UnitEntity;
import com.mwalimubank.mbimsapi.features.administration.unit.UnitRepository;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;
import com.mwalimubank.mbimsapi.features.common.PageSpecs;
import com.mwalimubank.mbimsapi.features.common.services.PagedQueryService;
import com.mwalimubank.mbimsapi.features.notification.NotificationService;
import com.mwalimubank.mbimsapi.features.notification.dto.SendNotificationDto;
import com.mwalimubank.mbimsapi.features.notification.enums.NotificationChannelsEnum;
import com.mwalimubank.mbimsapi.features.role.RoleEntity;
import com.mwalimubank.mbimsapi.features.role.RoleRepository;
import com.mwalimubank.mbimsapi.features.user.UserEntity;
import com.mwalimubank.mbimsapi.features.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationDto;
import com.mwalimubank.mbimsapi.features.approval.util.ApprovalStatusUtil;
import com.mwalimubank.mbimsapi.core.services.CurrentUserService;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository repository;
    private final UnitRepository unitRepository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final NotificationService notificationService;
    private final Random random = new Random();
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PagedQueryService pagedQueryService;

    private static final Set<String> SORT_FIELDS = Set.of(
            "id", "firstName", "middleName", "lastName", "name",
            "email", "mobilePhone", "staffNo", "gender",
            "createdAt", "updatedAt"
    );

    @Value("${spring.front.end.url}")
    private String frontEndUrl;


    public PagedResponse<EmployeeResponseDTO> findAll(PaginationRequest pagination, String search) {
        Specification<EmployeeEntity> spec = PageSpecs.and(
                PageSpecs.notDeleted(),
                PageSpecs.searchLike(search, "firstName", "middleName", "lastName", "email", "mobilePhone")
        );

        return pagedQueryService.findAll(
                repository,
                spec,
                pagination,
                EmployeeEntity.class,
                EmployeeEntity::getId,
                EmployeeResponseDTO::fromEntity,
                EmployeeResponseDTO::setApprovalStatus,
                SORT_FIELDS
        );
    }

























    public ApprovalAwareDTO<EmployeeResponseDTO> findOne  (Long  departmentId) {
        EmployeeEntity   department = repository.findById( departmentId)
                .orElseThrow(() -> new IllegalStateException(" Employee not found"));

        EmployeeResponseDTO dto = EmployeeResponseDTO.fromEntity(department);

        return approvalStatusUtil.attachApprovalInfo(
                dto,
                department.getId(),
                EmployeeEntity.class.getSimpleName(),
                currentUserService.getCurrentUserRoleId()
        );
    }

    @Transactional
    public EmployeeResponseDTO update(Long id, CreateEmployeeDTO request) {
        EmployeeEntity entity = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException("Employee not found with id: " + id)
                );

        // Email uniqueness check (excluding current employee)
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            repository.findByEmail(request.getEmail())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new IllegalStateException(
                                "Employee with email '" + request.getEmail() + "' already exists"
                        );
                    });
        }

        // Unit (optional)
        if (request.getUnitId() != null) {
            UnitEntity unit = unitRepository.findById(request.getUnitId())
                    .orElseThrow(() -> new IllegalStateException("Unit not found with id: " + request.getUnitId()));
            entity.setUnit(unit);
        } else {
            entity.setUnit(null);          // allow clearing the unit
        }

        // Department (optional)
        if (request.getDepartmentId() != null) {
            DepartmentEntity department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new IllegalStateException("Department not found with id: " + request.getDepartmentId()));
            entity.setDepartment(department);
        } else {
            entity.setDepartment(null);
        }

        // Position (optional)
        if (request.getPositionId() != null) {
            PositionEntity position = positionRepository.findById(request.getPositionId())
                    .orElseThrow(() -> new IllegalStateException("Position not found with id: " + request.getPositionId()));
            entity.setPosition(position);
        } else {
            entity.setPosition(null);
        }

        // Simple fields
        entity.setEmail(request.getEmail());
        entity.setMobilePhone(request.getMobilePhone());
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setMiddleName(request.getMiddleName());
        // add any other fields you need...

        EmployeeEntity updatedEntity = repository.save(entity);
        return EmployeeResponseDTO.fromEntity(updatedEntity);
    }

@Transactional
public String receiveCredentials(Long id) {
    EmployeeEntity employee = repository.findById(id)
            .orElseThrow(() -> new IllegalStateException("Employee not found"));

    if (employee.getEmail() == null || employee.getEmail().isBlank()) {
        throw new IllegalStateException("Employee has no email address");
    }

    // Try to find existing user by email
    UserEntity user = userRepository.findByEmail(employee.getEmail())
            .orElse(null);

    String rawPassword = PasswordGenerator.generate(12);

    if (user == null) {
        // ===== CREATE NEW USER =====
        user = new UserEntity();
        user.setEmail(employee.getEmail());
        user.setName(employee.getName());
        user.setPhone(employee.getMobilePhone());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setIsOtpVerified(true);
        user.setIsRecoveryRequested(false);

        // Assign role based on position
        assignRoleFromPosition(user, employee);

        user = userRepository.save(user);
        log.info("Created new user for employee id={}", id);
    } else {
        // ===== UPDATE EXISTING USER =====
        user.setName(employee.getName());
        user.setPhone(employee.getMobilePhone());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setIsRecoveryRequested(false);

        // Also update the role in case the position changed
        assignRoleFromPosition(user, employee);

        user = userRepository.save(user);
        log.info("Updated existing user for employee id={}", id);
    }

    // Send credentials
    sendAuthNotification(user, rawPassword, "receive-credentials", "Mbims Credentials");

    employee.setIsCredentialsShared(true);
    repository.save(employee);

    log.info("Credentials sent to user with email: {}", user.getEmail());
    return "Credentials Shared Successfully";
}

    /**
     * Finds the role that matches the employee's position name and attaches it to the user.
     */
    private void assignRoleFromPosition(UserEntity user, EmployeeEntity employee) {
        if (employee.getPosition() == null) {
            log.warn("Employee id={} has no position – role not assigned", employee.getId());
            return;
        }

        String positionName = employee.getPosition().getName();

        RoleEntity role = roleRepository.findByName(positionName)
                .orElseThrow(() -> new IllegalStateException(
                        "Role not found for position: " + positionName
                ));

        user.setRole(role);
    }

        public void sendAuthNotification(UserEntity user, String password, String template, String subject) {
        try {
            log.info("Auth notification for user={}", toJson(user));

            String redirectUrl = frontEndUrl + "/"
                    + FrontEndRouteConstants.LOGIN;

            Map<String, Object> context = new HashMap<>();

    //            String password = PasswordGenerator.generate(12);
    //
    //            context.put("expiryMinutes", 5);
            context.put("email", user.getEmail());
            context.put("name", user.getName());           // ← added
            context.put("password", password);
            context.put("redirectUrl", redirectUrl);

            List<String> recipients = List.of(user.getEmail());

            SendNotificationDto dto = new SendNotificationDto();
            dto.setChannel(NotificationChannelsEnum.EMAIL);
            dto.setRecipients(recipients);
            dto.setForName(user.getName());
            dto.setForId(user.getId());
            dto.setContext(context);
            dto.setTemplate(template);
            dto.setSubject(subject);
            dto.setDescription(subject);
            dto.setRedirectUrl(redirectUrl);

            notificationService.sendNotification(dto);

        } catch (Exception e) {
            log.error("Failed to send auth notification for user={}", user.getEmail(), e);
        }
    }

    private String toJson(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        EmployeeEntity entity = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Employee not found with id: " + id
                        )
                );

        if (soft) {
            entity.setDeleted(true);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }
}
